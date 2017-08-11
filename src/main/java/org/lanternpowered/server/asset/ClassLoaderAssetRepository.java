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

import com.google.common.collect.Sets;
import org.lanternpowered.server.plugin.LanternPluginManager;
import org.spongepowered.api.Sponge;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

import javax.annotation.Nullable;

public class ClassLoaderAssetRepository extends AbstractMultiAssetRepository {

    private static final ClassLoader CLASS_LOADER = Sponge.class.getClassLoader();

    private final Set<AssetRepository> repositories = Sets.newConcurrentHashSet();
    private final LanternPluginManager pluginManager;

    public ClassLoaderAssetRepository(LanternPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    // The class loader assets cannot be reloaded

    public void addRepository(Path path) {
        final AssetRepository assetRepository;
        if (Files.isDirectory(path)) {
            assetRepository = new ClassLoaderDirectoryAssetRepository(this.pluginManager, path);
        } else {
            assetRepository = new ClassLoaderFileAssetRepository(this.pluginManager, path);
        }
        this.repositories.add(assetRepository);
    }

    @Override
    protected Collection<AssetRepository> getRepositories() {
        return this.repositories;
    }

    @Override
    public void reload() {
    }

    @Override
    public void addReloadListener(ReloadListener reloadListener) {
    }

    @Override
    public void removeReloadListener(ReloadListener reloadListener) {
    }

    /**
     * Every source directory is also a {@link DirectoryAssetRepository},
     * but can't be reloaded (not supported).
     */
    private static class ClassLoaderDirectoryAssetRepository extends DirectoryAssetRepository {

        ClassLoaderDirectoryAssetRepository(LanternPluginManager pluginManager, Path directory) {
            super(pluginManager, directory);
        }

        /**
         * Use a {@link ClassLoader} if we want to request
         * a resource directly.
         *
         * @param path The path
         * @return The asset url, if found
         */
        @Nullable
        @Override
        protected URL getAssetURL(Path path) {
            return CLASS_LOADER.getResource(path.toString().replace(File.separatorChar, '/'));
        }

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

    /**
     * Every jar is also a {@link FileAssetRepository},
     * but can't be reloaded.
     */
    private static class ClassLoaderFileAssetRepository extends FileAssetRepository {

        ClassLoaderFileAssetRepository(LanternPluginManager pluginManager, Path file) {
            super(pluginManager, file);
        }

        /**
         * Use a {@link ClassLoader} if we want to request
         * a resource directly.
         *
         * @param path The path
         * @return The asset url, if found
         */
        @Nullable
        @Override
        protected URL getAssetURL(Path path) {
            return CLASS_LOADER.getResource(path.toString().replace(File.separatorChar, '/'));
        }

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
}
