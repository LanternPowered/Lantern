package org.lanternpowered.server.plugin;

import java.io.File;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.lanternpowered.server.game.LanternGame;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.service.event.EventManager;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

public class PluginModule extends AbstractModule {

    private final LanternPluginContainer container;
    private final Class<?> pluginClass;
    private final LanternGame game;

    public PluginModule(LanternPluginContainer container, Class<?> pluginClass, LanternGame game) {
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
        this.bind(File.class).annotatedWith(privateConfigDir).toProvider(PrivateConfigDirProvider.class);
        this.bind(File.class).annotatedWith(sharedConfigFile).toProvider(SharedConfigFileProvider.class); // Shared-directory config file
        this.bind(File.class).annotatedWith(privateConfigFile).toProvider(PrivateConfigFileProvider.class); // Plugin-private directory config file

        this.bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        }).annotatedWith(sharedConfigFile).toProvider(SharedHoconConfigProvider.class); // Loader for shared-directory config file
        this.bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        }).annotatedWith(privateConfigFile).toProvider(PrivateHoconConfigProvider.class); // Loader for plugin-private directory config file
    }

    private static class PrivateConfigDirProvider implements Provider<File> {

        private final PluginContainer container;
        private final LanternGame game;

        @Inject
        private PrivateConfigDirProvider(PluginContainer container, LanternGame game) {
            this.container = container;
            this.game = game;
        }

        @Override
        public File get() {
            return this.game.getConfigService().getPluginConfig(this.container).getDirectory();
        }
    }

    private static class PrivateConfigFileProvider implements Provider<File> {

        private final PluginContainer container;
        private final LanternGame game;

        @Inject
        private PrivateConfigFileProvider(PluginContainer container, LanternGame game) {
            this.container = container;
            this.game = game;
        }

        @Override
        public File get() {
            return this.game.getConfigService().getPluginConfig(this.container).getConfigFile();
        }
    }

    private static class SharedConfigFileProvider implements Provider<File> {

        private final PluginContainer container;
        private final LanternGame game;

        @Inject
        private SharedConfigFileProvider(PluginContainer container, LanternGame game) {
            this.container = container;
            this.game = game;
        }

        @Override
        public File get() {
            return this.game.getConfigService().getSharedConfig(this.container).getConfigFile();
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
            return this.game.getConfigService().getSharedConfig(this.container).getConfig();
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
            return this.game.getConfigService().getPluginConfig(this.container).getConfig();
        }
    }

}
