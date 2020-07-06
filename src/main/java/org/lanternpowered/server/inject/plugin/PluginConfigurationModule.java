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
package org.lanternpowered.server.inject.plugin;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.lanternpowered.server.inject.config.ConfigDirAnnotation;
import org.lanternpowered.server.inject.config.DefaultConfigAnnotation;
import org.lanternpowered.server.inject.provider.PathAsFileProvider;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.plugin.PluginContainer;

import java.io.File;
import java.nio.file.Path;

/**
 * A module which provides bindings for configuration annotations.
 */
final class PluginConfigurationModule extends AbstractModule {

    private static final TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>> COMMENTED_CONFIGURATION_NODE_LOADER =
            new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {};

    /**
     * Provides a non-shared (private) directory.
     *
     * {@literal @}ConfigDir(sharedRoot = false) File configDir;
     */
    private static class NonSharedDirAsFile extends PathAsFileProvider {
        @Inject
        void init(@ConfigDir(sharedRoot = false) Provider<Path> path) {
            this.path = path;
        }
    }

    /**
     * Provides a configuration file within a non-shared (private) directory.
     *
     * {@literal @}DefaultConfig(sharedRoot = false) File configFile;
     */
    private static class NonSharedPathAsFile extends PathAsFileProvider {
        @Inject
        void init(@DefaultConfig(sharedRoot = false) Provider<Path> path) {
            this.path = path;
        }
    }

    /**
     * Provides a configuration file within a shared directory.
     *
     * {@literal @}DefaultConfig(sharedRoot = true) File configFile;
     */
    private static class SharedDirAsFile extends PathAsFileProvider {
        @Inject
        void init(@DefaultConfig(sharedRoot = true) Provider<Path> path) {
            this.path = path;
        }
    }

    private abstract static class ConfigObjectProvider<T> implements Provider<T> {

        @Inject ConfigManager configManager;
        @Inject PluginContainer pluginContainer;
    }

    private abstract static class PathProvider extends ConfigObjectProvider<Path> {
    }

    /**
     * Provides a non-shared (private) directory.
     *
     * {@literal @}ConfigDir(sharedRoot = false) Path configDir;
     */
    private static class NonSharedDirAsPath extends PathProvider {
        @Override
        public Path get() {
            return this.configManager.getPluginConfig(this.pluginContainer).getDirectory();
        }
    }

    /**
     * Provides a configuration file within a non-shared (private) directory.
     *
     * {@literal @}DefaultConfig(sharedRoot = false) Path configFile;
     */
    private static class NonSharedPathAsPath extends PathProvider {
        @Override
        public Path get() {
            return this.configManager.getPluginConfig(this.pluginContainer).getConfigPath();
        }
    }

    /**
     * Provides a configuration file within a shared directory.
     *
     * {@literal @}DefaultConfig(sharedRoot = true) Path configFile;
     */
    private static class SharedDirAsPath extends PathProvider {
        @Override
        public Path get() {
            return this.configManager.getSharedConfig(this.pluginContainer).getConfigPath();
        }
    }

    private abstract static class CommentedConfigurationLoader extends ConfigObjectProvider<ConfigurationLoader<CommentedConfigurationNode>> {
    }

    /**
     * Provides a commented configuration file loader within a non-shared directory.
     *
     * {@literal @}DefaultConfig(sharedRoot = false) ConfigurationLoader<CommentedConfigurationNode> configLoader;
     */
    private static class NonSharedCommentedConfigLoader extends CommentedConfigurationLoader {
        @Override
        public ConfigurationLoader<CommentedConfigurationNode> get() {
            return this.configManager.getPluginConfig(this.pluginContainer).getConfig();
        }
    }

    /**
     * Provides a commented configuration file loader within a shared directory.
     *
     * {@literal @}DefaultConfig(sharedRoot = true) ConfigurationLoader<CommentedConfigurationNode> configLoader;
     */
    private static class SharedCommentedConfigLoader extends CommentedConfigurationLoader {
        @Override
        public ConfigurationLoader<CommentedConfigurationNode> get() {
            return this.configManager.getSharedConfig(this.pluginContainer).getConfig();
        }
    }

    @Override
    protected void configure() {
        bind(Path.class).annotatedWith(ConfigDirAnnotation.NON_SHARED)
                .toProvider(NonSharedDirAsPath.class).in(Scopes.SINGLETON);
        bind(File.class).annotatedWith(ConfigDirAnnotation.NON_SHARED)
                .toProvider(NonSharedDirAsFile.class).in(Scopes.SINGLETON);
        // Plugin-private directory config file
        bind(Path.class).annotatedWith(DefaultConfigAnnotation.NON_SHARED)
                .toProvider(NonSharedPathAsPath.class).in(Scopes.SINGLETON);
        bind(File.class).annotatedWith(DefaultConfigAnnotation.NON_SHARED)
                .toProvider(NonSharedPathAsFile.class).in(Scopes.SINGLETON);
        // Shared-directory config file
        bind(Path.class).annotatedWith(DefaultConfigAnnotation.SHARED)
                .toProvider(SharedDirAsPath.class).in(Scopes.SINGLETON);
        bind(File.class).annotatedWith(DefaultConfigAnnotation.SHARED)
                .toProvider(SharedDirAsFile.class).in(Scopes.SINGLETON);
        // Loader for shared-directory config file
        bind(COMMENTED_CONFIGURATION_NODE_LOADER).annotatedWith(DefaultConfigAnnotation.SHARED)
                .toProvider(SharedCommentedConfigLoader.class);
        // Loader for plugin-private directory config file
        bind(COMMENTED_CONFIGURATION_NODE_LOADER).annotatedWith(DefaultConfigAnnotation.NON_SHARED)
                .toProvider(NonSharedCommentedConfigLoader.class);
    }
}
