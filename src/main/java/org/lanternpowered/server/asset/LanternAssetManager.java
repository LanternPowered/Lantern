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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.plugin.PluginContainer;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public final class LanternAssetManager implements AssetManager {

    public static final String DEFAULT_ASSET_DIR = "assets";
    public static final ClassLoader CLASS_LOADER = Sponge.class.getClassLoader();

    @Override
    public Optional<Asset> getAsset(Object instance, String name) {
        checkNotNull(instance, "plugin instance");
        checkNotNull(name, "name");
        checkArgument(!name.isEmpty(), "name cannot be empty");
        PluginContainer plugin = Sponge.getPluginManager().fromInstance(instance).get();
        Path assetDir = plugin.getAssetDirectory().orElse(Paths.get(DEFAULT_ASSET_DIR).resolve(plugin.getId().replace('.', '/')));
        URL url = CLASS_LOADER.getResource(assetDir.resolve(name).toString());
        if (url == null) {
            return Optional.empty();
        }
        return Optional.of(new LanternAsset(plugin, url));
    }

    @Override
    public Optional<Asset> getAsset(String name) {
        return this.getAsset(LanternGame.plugin(), name);
    }

}
