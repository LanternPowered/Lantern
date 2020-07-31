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
package org.lanternpowered.server.plugin

import org.apache.logging.log4j.Logger
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.util.optional.asOptional
import org.spongepowered.plugin.metadata.PluginMetadata
import java.net.URL
import java.nio.file.Path
import java.util.Optional

class InternalPluginContainer(
        private val file: Path,
        private val metadata: PluginMetadata,
        private val logger: Logger,
        private val instance: Any
) : PluginContainer {

    override fun getFile(): Path = this.file
    override fun getMetadata(): PluginMetadata = this.metadata
    override fun getLogger(): Logger = this.logger
    override fun getInstance(): Any = this.instance

    override fun locateResource(relative: URL): Optional<URL> {
        val classLoader = this.instance.javaClass.classLoader
        val resolved: URL? = classLoader.getResource(relative.path)
        return resolved.asOptional()
    }

    override fun toString(): String = this.metadata.toString()
            .replace("PluginMetadata", this::class.java.simpleName)
}
