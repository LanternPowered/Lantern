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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

import javax.annotation.Nullable;

public class ClassLoaderAssetRepository extends AbstractAssetRepository {

    private static final ClassLoader CLASS_LOADER = Sponge.class.getClassLoader();

    public ClassLoaderAssetRepository(PluginManager pluginManager) {
        super(pluginManager);
    }

    @Nullable
    @Override
    protected URL getAssetURL(PluginContainer plugin, Path path) {
        return CLASS_LOADER.getResource(path.toString().replace(File.separatorChar, '/'));
    }

    // The class loader assets cannot be reloaded

    @Override
    public void reload() {
    }

    @Override
    public void addReloadListener(ReloadListener reloadListener) {
    }

    @Override
    public void removeReloadListener(ReloadListener reloadListener) {
    }
}
