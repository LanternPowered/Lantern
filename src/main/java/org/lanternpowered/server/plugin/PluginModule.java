package org.lanternpowered.server.plugin;

import java.io.File;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.lanternpowered.server.service.config.LanternConfigService;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.service.config.DefaultConfig;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

public class PluginModule extends AbstractModule {

    private final LanternPluginContainer container;
    private final Class<?> pluginClass;
    private final Game game;

    public PluginModule(LanternPluginContainer container, Class<?> pluginClass, Game game) {
        this.pluginClass = pluginClass;
        this.container = container;
        this.game = game;
    }

    @Override
    protected void configure() {
        ConfigDir privateConfigDir = new ConfigDirAnnotation(false);
        DefaultConfig sharedConfigFile = new ConfigFileAnnotation(true);
        DefaultConfig privateConfigFile = new ConfigFileAnnotation(false);

        bind(this.pluginClass).in(Scopes.SINGLETON);
        bind(PluginContainer.class).toInstance(this.container);
        bind(Logger.class).toInstance(this.container.getLogger());
        bind(ServiceManager.class).toInstance(this.game.getServiceManager());
        bind(Game.class).toInstance(this.game);

        // Plugin-private config directory (shared dir is in the global guice module)
        bind(File.class).annotatedWith(privateConfigDir).toProvider(PrivateConfigDirProvider.class);
        bind(File.class).annotatedWith(sharedConfigFile).toProvider(SharedConfigFileProvider.class); // Shared-directory config file
        bind(File.class).annotatedWith(privateConfigFile).toProvider(PrivateConfigFileProvider.class); // Plugin-private directory config file

        bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        }).annotatedWith(sharedConfigFile)
                .toProvider(SharedHoconConfigProvider.class); // Loader for shared-directory config file
        bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        }).annotatedWith(privateConfigFile)
                .toProvider(PrivateHoconConfigProvider.class); // Loader for plugin-private directory config file
    }

    private static class PrivateConfigDirProvider implements Provider<File> {

        private final PluginContainer container;

        @Inject
        private PrivateConfigDirProvider(PluginContainer container) {
            this.container = container;
        }

        @Override
        public File get() {
            return LanternConfigService.getPrivateRoot(this.container).getDirectory();
        }
    }

    private static class PrivateConfigFileProvider implements Provider<File> {

        private final PluginContainer container;

        @Inject
        private PrivateConfigFileProvider(PluginContainer container) {
            this.container = container;
        }

        @Override
        public File get() {
            return LanternConfigService.getPrivateRoot(this.container).getConfigFile();
        }
    }

    private static class SharedConfigFileProvider implements Provider<File> {

        private final PluginContainer container;

        @Inject
        private SharedConfigFileProvider(PluginContainer container) {
            this.container = container;
        }

        @Override
        public File get() {
            return LanternConfigService.getSharedRoot(this.container).getConfigFile();
        }
    }

    private static class SharedHoconConfigProvider implements Provider<ConfigurationLoader<CommentedConfigurationNode>> {

        private final PluginContainer container;

        @Inject
        private SharedHoconConfigProvider(PluginContainer container) {
            this.container = container;
        }

        @Override
        public ConfigurationLoader<CommentedConfigurationNode> get() {
            return LanternConfigService.getSharedRoot(this.container).getConfig();
        }
    }

    private static class PrivateHoconConfigProvider implements Provider<ConfigurationLoader<CommentedConfigurationNode>> {

        private final PluginContainer container;

        @Inject
        private PrivateHoconConfigProvider(PluginContainer container) {
            this.container = container;
        }

        @Override
        public ConfigurationLoader<CommentedConfigurationNode> get() {
            return LanternConfigService.getPrivateRoot(this.container).getConfig();
        }
    }

}
