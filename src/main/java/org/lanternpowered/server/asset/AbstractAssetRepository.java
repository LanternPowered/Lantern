/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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

import org.lanternpowered.api.asset.Asset;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.GuavaCollectors;

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

    @Override
    public Optional<Asset> get(Object plugin, String name) {
        checkNotNull(plugin, "plugin");
        checkNotNull(name, "name");
        name = name.toLowerCase(Locale.ENGLISH);
        PluginContainer pluginContainer;
        String pluginId;
        if (plugin instanceof String) {
            pluginId = (String) plugin;
            pluginContainer = Sponge.getPluginManager().getPlugin(pluginId).get();
        } else {
            pluginContainer = Sponge.getPluginManager().fromInstance(plugin).get();
            pluginId = pluginContainer.getId();
        }
        final String id = pluginId + ':' + name;
        pluginId = pluginId.replace('.', File.separatorChar);
        if (!pluginId.endsWith(File.separator)) {
            pluginId += File.separator;
        }
        final Path path = Paths.get(DEFAULT_ASSET_DIR).resolve(pluginId).resolve(name);
        return this.loadedAssets.computeIfAbsent(path, path1 -> {
            final URL url = this.getAssetURL(pluginContainer, path1);
            if (url == null) {
                return Optional.empty();
            }
            return Optional.of(new LanternAsset(pluginContainer, id, path, url));
        });
    }

    @Override
    public Optional<Asset> get(String id) {
        int index = id.indexOf(':');
        if (index == -1) {
            return this.get(Lantern.getMinecraftPlugin(), id);
        } else {
            return this.get(id.substring(0, index), id.substring(index + 1));
        }
    }

    @Nullable
    protected abstract URL getAssetURL(PluginContainer pluginContainer, Path path);

    @Override
    public Collection<Asset> getLoadedAssets() {
        return this.loadedAssets.values().stream().filter(Optional::isPresent).map(Optional::get).collect(GuavaCollectors.toImmutableList());
    }
}
