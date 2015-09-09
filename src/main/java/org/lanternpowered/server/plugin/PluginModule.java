package org.lanternpowered.server.plugin;

import java.io.File;
import java.lang.annotation.Annotation;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.service.config.DefaultConfig;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

public class PluginModule extends AbstractModule {

    private final PluginContainer container;
    private final Game game;

    public PluginModule(PluginContainer container, Game game) {
        this.container = container;
        this.game = game;
    }

    @Override
    protected void configure() {
        DefaultConfig pluginConfigPrivate = new DefaultConfigAnnotation(false);
        DefaultConfig pluginConfigShared = new DefaultConfigAnnotation(true);
        ConfigDir pluginDirPrivate = new ConfigDirAnnotation(false);

        this.bind(Game.class).toInstance(this.game);
        this.bind(PluginContainer.class).toInstance(this.container);
        this.bind(Logger.class).toInstance(LoggerFactory.getLogger(this.container.getId()));

        this.bind(File.class).annotatedWith(pluginDirPrivate).toProvider(PluginConfigDirProvider.class);
        this.bind(File.class).annotatedWith(pluginConfigShared).toProvider(PluginSharedConfigFileProvider.class);
        this.bind(File.class).annotatedWith(pluginConfigPrivate).toProvider(PluginPrivateConfigFileProvider.class);
        this.bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        }).annotatedWith(pluginConfigShared).toProvider(PluginSharedHoconConfigProvider.class);
        this.bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        }).annotatedWith(pluginConfigPrivate).toProvider(PluginPrivateHoconConfigProvider.class);
    }

    private static class DefaultConfigAnnotation implements DefaultConfig {

        private boolean shared;

        private DefaultConfigAnnotation(boolean isShared) {
            this.shared = isShared;
        }

        @Override
        public boolean sharedRoot() {
            return this.shared;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return DefaultConfig.class;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || !(o instanceof DefaultConfig)) {
                return false;
            }
            return this.sharedRoot() == ((DefaultConfig) o).sharedRoot();
        }

        @Override
        public int hashCode() {
            return (127 * "sharedRoot".hashCode()) ^ Boolean.valueOf(this.sharedRoot()).hashCode();
        }

        @Override
        public String toString() {
            return "@org.spongepowered.api.service.config.DefaultConfig(sharedRoot=" + this.sharedRoot() + ")";
        }
    }

    private static class ConfigDirAnnotation implements ConfigDir {

        private boolean shared;

        private ConfigDirAnnotation(boolean isShared) {
            this.shared = isShared;
        }

        @Override
        public boolean sharedRoot() {
            return this.shared;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return DefaultConfig.class;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || !(o instanceof ConfigDir)) {
                return false;
            }
            return this.sharedRoot() == ((ConfigDir) o).sharedRoot();
        }

        @Override
        public int hashCode() {
            return (127 * "sharedRoot".hashCode()) ^ Boolean.valueOf(this.sharedRoot()).hashCode();
        }

        @Override
        public String toString() {
            return "@org.spongepowered.api.service.config.ConfigDir(sharedRoot=" + this.sharedRoot() + ")";
        }
    }

    private static class PluginSharedConfigFileProvider implements Provider<File> {

        private final PluginContainer container;
        private final File root;

        @Inject
        private PluginSharedConfigFileProvider(PluginContainer container, @ConfigDir(sharedRoot = true) File sharedConfigDir) {
            this.container = container;
            this.root = sharedConfigDir;
        }

        @Override
        public File get() {
            return new File(this.root, this.container.getId() + ".conf");
        }
    }

    private static class PluginPrivateConfigFileProvider implements Provider<File> {

        private final PluginContainer container;
        private final File root;

        @Inject
        private PluginPrivateConfigFileProvider(PluginContainer container, @ConfigDir(sharedRoot = false) File sharedConfigDir) {
            this.container = container;
            this.root = sharedConfigDir;
        }

        @Override
        public File get() {
            return new File(this.root, this.container.getId() + ".conf");
        }
    }

    private static class PluginSharedHoconConfigProvider implements Provider<ConfigurationLoader<CommentedConfigurationNode>> {

        private final File configFile;

        @Inject
        private PluginSharedHoconConfigProvider(@DefaultConfig(sharedRoot = true) File configFile) {
            this.configFile = configFile;
        }

        @Override
        public ConfigurationLoader<CommentedConfigurationNode> get() {
            return HoconConfigurationLoader.builder().setFile(this.configFile).build();
        }
    }

    private static class PluginPrivateHoconConfigProvider implements Provider<ConfigurationLoader<CommentedConfigurationNode>> {

        private final File configFile;

        @Inject
        private PluginPrivateHoconConfigProvider(@DefaultConfig(sharedRoot = false) File configFile) {
            this.configFile = configFile;
        }

        @Override
        public ConfigurationLoader<CommentedConfigurationNode> get() {
            return HoconConfigurationLoader.builder().setFile(this.configFile).build();
        }
    }

    private static class PluginConfigDirProvider implements Provider<File> {

        private final PluginContainer container;
        private final File sharedConfigDir;

        @Inject
        private PluginConfigDirProvider(PluginContainer container, @ConfigDir(sharedRoot = true) File sharedConfigDir) {
            this.container = container;
            this.sharedConfigDir = sharedConfigDir;
        }

        @Override
        public File get() {
            return new File(this.sharedConfigDir, this.container.getId() + "/");
        }
    }

}
