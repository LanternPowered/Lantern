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

import org.lanternpowered.api.util.optional.optional
import org.spongepowered.plugin.PluginCandidate
import org.spongepowered.plugin.PluginEnvironment
import org.spongepowered.plugin.PluginKeys
import org.spongepowered.plugin.jvm.JVMPluginContainer
import org.spongepowered.plugin.jvm.JVMPluginLanguageService
import java.util.Optional

class LanternPluginLanguageService : JVMPluginLanguageService<LanternPluginContainer>() {

    override fun getName(): String = "lantern"

    override fun createPluginInstance(environment: PluginEnvironment, container: JVMPluginContainer, targetClassLoader: ClassLoader): Any {
        val pluginClass = Class.forName(container.metadata.mainClass, true, targetClassLoader)
        val objectInstance = pluginClass.kotlin.objectInstance
        if (objectInstance != null)
            return objectInstance
        val parentInjector = environment.blackboard.get(PluginKeys.PARENT_INJECTOR)!!

        TODO()
    }

    override fun createPluginContainer(candidate: PluginCandidate, environment: PluginEnvironment): Optional<LanternPluginContainer> =
            LanternPluginContainer(candidate).optional()
}
