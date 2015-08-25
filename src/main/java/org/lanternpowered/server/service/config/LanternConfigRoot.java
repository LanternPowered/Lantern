package org.lanternpowered.server.service.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.spongepowered.api.service.config.ConfigRoot;

import java.io.File;

/**
 * Root for lantern configurations.
 */
public class LanternConfigRoot implements ConfigRoot {

    private final String pluginName;
    private final File baseDir;

    public LanternConfigRoot(String pluginName, File baseDir) {
        this.pluginName = pluginName;
        this.baseDir = baseDir;
    }

    @Override
    public File getConfigFile() {
        File configFile = new File(this.baseDir, this.pluginName + ".conf");
        if (configFile.getParentFile().isDirectory()) {
            configFile.getParentFile().mkdirs();
        }
        return configFile;
    }

    @Override
    public ConfigurationLoader<CommentedConfigurationNode> getConfig() {
        return HoconConfigurationLoader.builder()
                .setFile(getConfigFile())
                .build();
    }

    @Override
    public File getDirectory() {
        return this.baseDir;
    }
}
