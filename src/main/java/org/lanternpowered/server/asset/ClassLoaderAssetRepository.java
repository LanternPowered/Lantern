/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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

import org.checkerframework.checker.nullness.qual.Nullable;

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
