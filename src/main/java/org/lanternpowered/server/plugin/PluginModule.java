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
package org.lanternpowered.server.plugin;

import static com.google.inject.name.Names.named;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.lanternpowered.server.game.LanternGame;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Platform;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.scheduler.AsynchronousExecutor;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.scheduler.SynchronousExecutor;
import org.spongepowered.api.service.ServiceManager;

import java.io.File;
import java.nio.file.Path;

final class PluginModule extends AbstractModule {

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
        ConfigDir sharedRootDir = new ConfigDirAnnotation(true);
        ConfigDir privateConfigDir = new ConfigDirAnnotation(false);
        DefaultConfig sharedConfigFile = new ConfigFileAnnotation(true);
        DefaultConfig privateConfigFile = new ConfigFileAnnotation(false);

        PluginContainer pluginContainer = this.game.getImplementationPlugin();
        bind(PluginContainer.class).annotatedWith(named(pluginContainer.getId())).toInstance(pluginContainer);
        pluginContainer = this.game.getApiPlugin();
        bind(PluginContainer.class).annotatedWith(named(pluginContainer.getId())).toInstance(pluginContainer);
        pluginContainer = this.game.getMinecraftPlugin();
        bind(PluginContainer.class).annotatedWith(named(pluginContainer.getId())).toInstance(pluginContainer);
        pluginContainer = this.game.getSpongePlugin();
        bind(PluginContainer.class).annotatedWith(named(pluginContainer.getId())).toInstance(pluginContainer);

        bind(this.pluginClass).in(Scopes.SINGLETON);
        bind(PluginContainer.class).toInstance(this.container);
        bind(Logger.class).toInstance(this.container.getLogger());
        bind(LanternGame.class).toInstance(this.game);
        bind(Game.class).toInstance(this.game);
        bind(GameRegistry.class).toInstance(this.game.getRegistry());
        bind(ServiceManager.class).toInstance(this.game.getServiceManager());
        bind(PluginManager.class).toInstance(this.game.getPluginManager());
        bind(AssetManager.class).toInstance(this.game.getAssetManager());
        bind(EventManager.class).toInstance(this.game.getEventManager());
        bind(Platform.class).toInstance(this.game.getPlatform());
        bind(MinecraftVersion.class).toInstance(this.game.getPlatform().getMinecraftVersion());
        bind(ChannelRegistrar.class).toInstance(this.game.getChannelRegistrar());

        bind(Path.class).annotatedWith(sharedRootDir).toInstance(this.game.getConfigDir());
        bind(File.class).annotatedWith(sharedRootDir).toInstance(this.game.getConfigDir().toFile());
        bind(Path.class).annotatedWith(privateConfigDir).toProvider(PrivateConfigDirProvider.class);
        bind(File.class).annotatedWith(privateConfigDir).toProvider(FilePrivateConfigDirProvider.class);
        // Shared-directory config file
        bind(Path.class).annotatedWith(sharedConfigFile).toProvider(SharedConfigFileProvider.class);
        bind(File.class).annotatedWith(sharedConfigFile).toProvider(FileSharedConfigFileProvider.class);
        // Plugin-private directory config file
        bind(Path.class).annotatedWith(privateConfigFile).toProvider(PrivateConfigFileProvider.class);
        bind(File.class).annotatedWith(privateConfigFile).toProvider(FilePrivateConfigFileProvider.class);

        // Loader for shared-directory config file
        bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {})
                .annotatedWith(sharedConfigFile).toProvider(SharedHoconConfigProvider.class);
        // Loader for plugin-private directory config file
        bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {})
                .annotatedWith(privateConfigFile).toProvider(PrivateHoconConfigProvider.class);

        bind(SpongeExecutorService.class).annotatedWith(SynchronousExecutor.class)
                .toProvider(SynchronousExecutorProvider.class);
        bind(SpongeExecutorService.class).annotatedWith(AsynchronousExecutor.class)
                .toProvider(AsynchronousExecutorProvider.class);
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

    private static class SynchronousExecutorProvider implements Provider<SpongeExecutorService> {

        private final PluginContainer container;
        private final Scheduler schedulerService;

        @Inject
        private SynchronousExecutorProvider(PluginContainer container, Game game) {
            this.container = container;
            this.schedulerService = game.getScheduler();
        }

        @Override
        public SpongeExecutorService get() {
            return this.schedulerService.createSyncExecutor(this.container);
        }

    }

    private static class AsynchronousExecutorProvider implements Provider<SpongeExecutorService> {

        private final PluginContainer container;
        private final Scheduler schedulerService;

        @Inject
        private AsynchronousExecutorProvider(PluginContainer container, Game game) {
            this.container = container;
            this.schedulerService = game.getScheduler();
        }

        @Override
        public SpongeExecutorService get() {
            return this.schedulerService.createAsyncExecutor(this.container);
        }

    }

}
