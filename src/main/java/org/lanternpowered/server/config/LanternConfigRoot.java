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
package org.lanternpowered.server.config;

import com.google.common.base.MoreObjects;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapperFactory;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.config.ConfigRoot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class LanternConfigRoot implements ConfigRoot {

    private final ObjectMapperFactory mapperFactory;
    private final String pluginName;
    private final Path baseDir;

    LanternConfigRoot(ObjectMapperFactory mapperFactory, String pluginName, Path baseDir) {
        this.mapperFactory = mapperFactory;
        this.pluginName = pluginName;
        this.baseDir = baseDir;
    }

    @Override
    public Path getConfigPath() {
        final Path configFile = this.baseDir.resolve(this.pluginName + ".conf");
        if (!Files.exists(this.baseDir)) {
            try {
                Files.createDirectories(this.baseDir);
            } catch (IOException e) {
                Lantern.getLogger().error("Failed to create plugin dir for {} at {}", this.pluginName, this.baseDir, e);
            }
        }
        return configFile;
    }

    @Override
    public ConfigurationLoader<CommentedConfigurationNode> getConfig() {
        return HoconConfigurationLoader.builder()
                .setPath(getConfigPath())
                .setDefaultOptions(ConfigurationOptions.defaults().setObjectMapperFactory(this.mapperFactory))
                .build();
    }

    @Override
    public Path getDirectory() {
        return this.baseDir;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("plugin", this.pluginName)
                .add("directory", this.baseDir)
                .add("configPath", this.baseDir.resolve(this.pluginName + ".conf"))
                .toString();
    }
}
