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

import ninja.leaping.configurate.objectmapping.DefaultObjectMapperFactory
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory
import ninja.leaping.configurate.objectmapping.ObjectMapperFactory
import org.apache.logging.log4j.Logger
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.plugin.id
import org.lanternpowered.server.plugin.LanternPluginContainer
import org.spongepowered.api.config.ConfigManager
import org.spongepowered.api.config.ConfigRoot
import java.nio.file.Path

class LanternConfigManager(
        private val logger: Logger,
        private val configDirectory: Path
) : ConfigManager {

    override fun getSharedConfig(plugin: PluginContainer): ConfigRoot =
            LanternConfigRoot(this.logger, plugin.getMapperFactory(), plugin.id, this.configDirectory)

    override fun getPluginConfig(plugin: PluginContainer): ConfigRoot =
            LanternConfigRoot(this.logger, plugin.getMapperFactory(), plugin.id, this.configDirectory.resolve(plugin.id))

    private fun PluginContainer.getMapperFactory(): ObjectMapperFactory {
        if (this is LanternPluginContainer) {
            val injector = this.injector
            if (injector != null)
                return injector.getInstance(GuiceObjectMapperFactory::class.java)
        }
        return DefaultObjectMapperFactory.getInstance()
    }
}
