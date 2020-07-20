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
package org.lanternpowered.server.config

import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.ObjectMapperFactory
import org.apache.logging.log4j.Logger
import org.lanternpowered.api.util.ToStringHelper
import org.spongepowered.api.config.ConfigRoot
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

internal class LanternConfigRoot(
        private val logger: Logger,
        private val mapperFactory: ObjectMapperFactory,
        private val pluginName: String,
        private val baseDirectory: Path
) : ConfigRoot {

    override fun getConfigPath(): Path {
        val configFile = this.baseDirectory.resolve("$pluginName.conf")
        if (!Files.exists(this.baseDirectory)) {
            try {
                Files.createDirectories(this.baseDirectory)
            } catch (ex: IOException) {
                this.logger.error("Failed to create plugin dir for $pluginName at $baseDirectory", ex)
            }
        }
        return configFile
    }

    override fun getConfig(): ConfigurationLoader<CommentedConfigurationNode> = HoconConfigurationLoader.builder()
            .setPath(this.configPath)
            .setDefaultOptions(ConfigurationOptions.defaults().setObjectMapperFactory(this.mapperFactory))
            .build()

    override fun getDirectory(): Path = this.baseDirectory

    override fun toString(): String = ToStringHelper(this)
            .add("plugin", this.pluginName)
            .add("directory", this.baseDirectory)
            .add("configPath", this.baseDirectory.resolve("$pluginName.conf"))
            .toString()
}
