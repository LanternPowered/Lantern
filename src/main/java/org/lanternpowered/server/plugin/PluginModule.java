/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.plugin;

import java.io.File;
import java.nio.file.Path;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.lanternpowered.server.game.LanternGame;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.EventManager;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

public final class PluginModule extends AbstractModule {

    private final LanternPluginContainer container;
    private final Class<?> pluginClass;
    private final LanternGame game;

    PluginModule(LanternPluginContainer container, Class<?> pluginClass, LanternGame game) {
        this.pluginClass = pluginClass;
        this.container = container;
        this.game = game;
    }

    @Override
    protected void configure() {
        ConfigDir privateConfigDir = new ConfigDirAnnotation(false);
        DefaultConfig sharedConfigFile = new ConfigFileAnnotation(true);
        DefaultConfig privateConfigFile = new ConfigFileAnnotation(false);

        this.bind(this.pluginClass).in(Scopes.SINGLETON);
        this.bind(PluginContainer.class).toInstance(this.container);
        this.bind(Logger.class).toInstance(this.container.getLogger());
        this.bind(ServiceManager.class).toInstance(this.game.getServiceManager());
        this.bind(EventManager.class).toInstance(this.game.getEventManager());
        this.bind(PluginManager.class).toInstance(this.game.getPluginManager());
        this.bind(GameRegistry.class).toInstance(this.game.getRegistry());
        this.bind(LanternGame.class).toInstance(this.game);
        this.bind(Game.class).toInstance(this.game);

        // Plugin-private config directory (shared dir is in the global guice module)
        this.bind(Path.class).annotatedWith(privateConfigDir).toProvider(PrivateConfigDirProvider.class);
        this.bind(File.class).annotatedWith(privateConfigDir).toProvider(FilePrivateConfigDirProvider.class);
        this.bind(Path.class).annotatedWith(sharedConfigFile).toProvider(SharedConfigFileProvider.class); // Shared-directory config file
        this.bind(File.class).annotatedWith(sharedConfigFile).toProvider(FileSharedConfigFileProvider.class);
        this.bind(Path.class).annotatedWith(privateConfigFile).toProvider(PrivateConfigFileProvider.class); // Plugin-private directory config file
        this.bind(File.class).annotatedWith(privateConfigFile).toProvider(FilePrivateConfigFileProvider.class);

        this.bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        }).annotatedWith(sharedConfigFile).toProvider(SharedHoconConfigProvider.class); // Loader for shared-directory config file
        this.bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        }).annotatedWith(privateConfigFile).toProvider(PrivateHoconConfigProvider.class); // Loader for plugin-private directory config file
    }

    private static class PrivateConfigDirProvider implements Provider<Path> {

        private final PluginContainer container;
        private final LanternGame game;

        @Inject
        private PrivateConfigDirProvider(PluginContainer container, LanternGame game) {
            this.container = container;
            this.game = game;
        }

        @Override
        public Path get() {
            return this.game.getConfigManager().getPluginConfig(this.container).getDirectory();
        }
    }

    private static class PrivateConfigFileProvider implements Provider<Path> {

        private final PluginContainer container;
        private final LanternGame game;

        @Inject
        private PrivateConfigFileProvider(PluginContainer container, LanternGame game) {
            this.container = container;
            this.game = game;
        }

        @Override
        public Path get() {
            return this.game.getConfigManager().getPluginConfig(this.container).getConfigPath();
        }
    }

    private static class SharedConfigFileProvider implements Provider<Path> {

        private final PluginContainer container;
        private final LanternGame game;

        @Inject
        private SharedConfigFileProvider(PluginContainer container, LanternGame game) {
            this.container = container;
            this.game = game;
        }

        @Override
        public Path get() {
            return this.game.getConfigManager().getSharedConfig(this.container).getConfigPath();
        }
    }

    private static class SharedHoconConfigProvider implements Provider<ConfigurationLoader<CommentedConfigurationNode>> {

        private final PluginContainer container;
        private final LanternGame game;

        @Inject
        private SharedHoconConfigProvider(PluginContainer container, LanternGame game) {
            this.container = container;
            this.game = game;
        }

        @Override
        public ConfigurationLoader<CommentedConfigurationNode> get() {
            return this.game.getConfigManager().getSharedConfig(this.container).getConfig();
        }
    }

    private static class PrivateHoconConfigProvider implements Provider<ConfigurationLoader<CommentedConfigurationNode>> {

        private final PluginContainer container;
        private final LanternGame game;

        @Inject
        private PrivateHoconConfigProvider(PluginContainer container, LanternGame game) {
            this.container = container;
            this.game = game;
        }

        @Override
        public ConfigurationLoader<CommentedConfigurationNode> get() {
            return this.game.getConfigManager().getPluginConfig(this.container).getConfig();
        }
    }

    private static class FilePrivateConfigDirProvider implements Provider<File> {

        private final Path configDir;

        @Inject
        private FilePrivateConfigDirProvider(@ConfigDir(sharedRoot = false) Path configDir) {
            this.configDir = configDir;
        }

        @Override
        public File get() {
            return this.configDir.toFile();
        }
    }

    private static class FilePrivateConfigFileProvider implements Provider<File> {

        private final Path configPath;

        @Inject
        private FilePrivateConfigFileProvider(@DefaultConfig(sharedRoot = false) Path configPath) {
            this.configPath = configPath;
        }

        @Override
        public File get() {
            return this.configPath.toFile();
        }
    }

    private static class FileSharedConfigFileProvider implements Provider<File> {

        private final Path configPath;

        @Inject
        private FileSharedConfigFileProvider(@DefaultConfig(sharedRoot = true) Path configPath) {
            this.configPath = configPath;
        }

        @Override
        public File get() {
            return this.configPath.toFile();
        }
    }
}
