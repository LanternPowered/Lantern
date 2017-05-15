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

import com.google.common.collect.ImmutableList;
import org.lanternpowered.api.asset.Asset;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

public abstract class AbstractAssetRepository implements AssetRepository {

    private static final String DEFAULT_ASSET_DIR = "assets";

    /**
     * A map with all the loaded assets.
     */
    final Map<Path, Optional<Asset>> loadedAssets = new ConcurrentHashMap<>();

    private final PluginManager pluginManager;

    protected AbstractAssetRepository(PluginManager pluginManager) {
        checkNotNull(pluginManager, "pluginManager");
        this.pluginManager = pluginManager;
    }

    @Override
    public Optional<Asset> get(Object plugin, String name) {
        checkNotNull(plugin, "plugin");
        checkNotNull(name, "name");
        final PluginContainer pluginContainer;
        String pluginId;
        if (plugin instanceof String) {
            pluginId = (String) plugin;
            pluginContainer = this.pluginManager.getPlugin(pluginId).get();
        } else {
            pluginContainer = this.pluginManager.fromInstance(plugin).get();
            pluginId = pluginContainer.getId();
        }
        final String id = pluginId + ':' + name.toLowerCase(Locale.ENGLISH);
        pluginId = pluginId.replace('.', File.separatorChar);
        if (!pluginId.endsWith(File.separator)) {
            pluginId += File.separator;
        }
        final String pluginId0 = pluginId;
        final Path pathLowerCase = Paths.get(DEFAULT_ASSET_DIR).resolve(pluginId).resolve(name.toLowerCase(Locale.ENGLISH));
        return this.loadedAssets.computeIfAbsent(pathLowerCase, path1 -> {
            URL url = getAssetURL(pluginContainer, path1);
            if (url == null) {
                url = getAssetURL(pluginContainer, Paths.get(DEFAULT_ASSET_DIR).resolve(pluginId0).resolve(name));
                if (url == null) {
                    return Optional.empty();
                }
            }
            return Optional.of(new LanternAsset(pluginContainer, id, pathLowerCase, url));
        });
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
    protected abstract URL getAssetURL(PluginContainer pluginContainer, Path path);

    @Override
    public Collection<Asset> getLoadedAssets() {
        return this.loadedAssets.values().stream().filter(Optional::isPresent).map(Optional::get).collect(ImmutableList.toImmutableList());
    }
}
