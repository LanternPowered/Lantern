/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.asset;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.lanternpowered.api.asset.Asset;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.plugin.InfoPluginContainer;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.lanternpowered.server.plugin.LanternPluginManager;
import org.lanternpowered.server.plugin.SimplePluginContainer;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.plugin.meta.PluginMetadata;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public abstract class AbstractAssetRepository implements AssetRepository {

    private static final String DEFAULT_ASSET_DIR = "data";
    private static final Set<String> DEFAULT_ASSET_DIRS = Sets.newHashSet("data", "assets");

    private static final String ASSET_DIRS_REGEX = "(?:" + Joiner.on('|').join(DEFAULT_ASSET_DIRS) + ")";
    private static final Pattern INFO_FILE_PATTERN = Pattern.compile(".+:plugin\\.info");

    static String generateRegex(String path) {
        // Generate a regex to match files within the directory
        final int index = path.indexOf(':');
        String target;
        String regex = path.substring(index + 1);
        if (index == -1) {
            target = "(minecraft)";
        } else {
            target = path.substring(0, index);
            if (target.equals("*")) {
                target = "(.+)";
            } else {
                target = '(' + Pattern.quote(path.substring(0, index).replace('/', File.separatorChar)) + ')';
            }
        }
        // (?:data|assets)\/(.+)\/(path\/to\/directory\/.+)
        regex = ASSET_DIRS_REGEX + "\\/" + target + "\\/" + '(' + Pattern.quote(regex);
        if (regex.charAt(regex.length() - 1) != File.separatorChar) {
            regex += "\\/";
        }
        return regex + ".+)";
    }

    /**
     * A map with all the loaded assets.
     */
    final Map<String, Optional<Asset>> loadedAssets = new ConcurrentHashMap<>();
    private final List<ReloadListener> reloadListeners = new ArrayList<>();
    private final LanternPluginManager pluginManager;

    AbstractAssetRepository(LanternPluginManager pluginManager) {
        checkNotNull(pluginManager, "pluginManager");
        this.pluginManager = pluginManager;
    }

    Asset registerAsset(Object plugin, String id, Path file) {
        try {
            return registerAsset(plugin, id, file.toUri().toURL(), file);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    Asset registerAsset(Object plugin, String id, URL url, Path file) {
        final Optional<Asset> optAsset = this.loadedAssets.get(id);
        if (optAsset != null) {
            return optAsset.get();
        }
        PluginContainer pluginContainer;
        if (plugin instanceof String) {
            final String pluginId = (String) plugin;
            // Attempt to find a plugin container that is assigned to the id,
            // if not, create a plugin container that represents the plugin
            // with the id.
            pluginContainer = this.pluginManager.getPlugin(pluginId).orElse(null);
            if (pluginContainer == null) {
                // Somebody is requesting the plugin.info file and there is no plugin container,
                // don't register a plugin in this case, just return a asset with a dummy one.
                if (INFO_FILE_PATTERN.matcher(id).matches()) {
                    return new LanternAsset(new SimplePluginContainer(pluginId), id, url, file);
                }
                // Attempt to get plugin info from the repository, and use
                // that to define the plugin container
                final URL infoUrl = getAssetURL(Paths.get(DEFAULT_ASSET_DIR).resolve(pluginId).resolve("plugin.info"));
                if (infoUrl != null) {
                    try {
                        final PluginMetadata pluginMetadata = InfoPluginContainer.readPluginInfo(pluginId, infoUrl);
                        // Construct a plugin container
                        pluginContainer = new InfoPluginContainer(pluginId, pluginMetadata);
                    } catch (IOException e) {
                        Lantern.getLogger().error("Failed to read plugin.info");
                    }
                }
                if (pluginContainer == null) {
                    // Generate a simple plugin container
                    pluginContainer = new SimplePluginContainer(pluginId);
                }
                // Register the plugin container
                this.pluginManager.registerPlugin(pluginContainer);
                Lantern.getLogger().info("Registered data pack plugin: {} {}",
                        pluginContainer.getName(), pluginContainer.getVersion().orElse("unknown"));
            }
        } else {
            // Search for the plugin container based on the instance
            pluginContainer = this.pluginManager.fromInstance(plugin).get();
        }
        checkNotNull(pluginContainer);
        final LanternAsset asset = new LanternAsset(pluginContainer, id, url, file);
        this.loadedAssets.put(id, Optional.of(asset));
        return asset;
    }

    @Override
    public Optional<Asset> get(Object plugin, String name) {
        checkNotNull(plugin, "plugin");
        checkNotNull(name, "name");
        String pluginId;
        if (plugin instanceof String) {
            pluginId = (String) plugin;
        } else {
            pluginId = this.pluginManager.fromInstance(plugin).get().getId();
        }
        // All the resource paths are lowercase
        pluginId = pluginId.toLowerCase(Locale.ENGLISH);
        name = name.toLowerCase(Locale.ENGLISH);
        // Generate the id and check if the resource is already loaded
        final String id = pluginId + ':' + name;
        Optional<Asset> asset = this.loadedAssets.get(id);
        if (asset != null) {
            return asset;
        }
        pluginId = pluginId.replace('.', File.separatorChar);
        if (!pluginId.endsWith(File.separator)) {
            pluginId += File.separator;
        }
        // Get the path for the asset and attempt to find the file url
        for (String dir : DEFAULT_ASSET_DIRS) {
            final Path path = Paths.get(dir).resolve(pluginId).resolve(name);
            final URL url = getAssetURL(path);
            if (url != null) {
                return Optional.of(registerAsset(plugin, id, url, path));
            }
        }
        this.loadedAssets.put(id, Optional.empty());
        return Optional.empty();
    }

    @Override
    public Optional<Asset> get(String id) {
        final int index = id.indexOf(':');
        if (index == -1) {
            return get(this.pluginManager.getPlugin(InternalPluginsInfo.Minecraft.IDENTIFIER).get(), id);
        } else {
            return get(id.substring(0, index), id.substring(index + 1));
        }
    }

    @Nullable
    protected abstract URL getAssetURL(Path path);

    @Override
    public Collection<Asset> getLoadedAssets() {
        return this.loadedAssets.values().stream().filter(Optional::isPresent).map(Optional::get).collect(ImmutableList.toImmutableList());
    }

    @Override
    public void addReloadListener(ReloadListener reloadListener) {
        this.reloadListeners.add(checkNotNull(reloadListener, "reloadListener"));
    }

    @Override
    public void removeReloadListener(ReloadListener reloadListener) {
        this.reloadListeners.remove(checkNotNull(reloadListener, "reloadListener"));
    }

    @Override
    public void reload() {
        this.reloadListeners.forEach(ReloadListener::onReload);
    }
}
