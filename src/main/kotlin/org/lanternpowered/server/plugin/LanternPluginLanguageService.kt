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

import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.server.plugin.inject.PluginGuiceModule
import org.lanternpowered.server.util.guice.getInstance
import org.spongepowered.api.config.ConfigManager
import org.spongepowered.plugin.PluginCandidate
import org.spongepowered.plugin.PluginEnvironment
import org.spongepowered.plugin.PluginKeys
import org.spongepowered.plugin.jvm.JVMPluginLanguageService
import java.util.Optional

class LanternPluginLanguageService : JVMPluginLanguageService<LanternPluginContainer>() {

    override fun getName(): String = "lantern"

    override fun createPluginInstance(environment: PluginEnvironment, container: LanternPluginContainer, targetClassLoader: ClassLoader): Any {
        val pluginClass = Class.forName(container.metadata.mainClass, true, targetClassLoader)
        val objectInstance = pluginClass.kotlin.objectInstance

        val parentInjector = environment.blackboard.get(PluginKeys.PARENT_INJECTOR).get()
        val configManager: ConfigManager = parentInjector.getInstance()
        val injector = parentInjector.createChildInjector(PluginGuiceModule(container, configManager))

        if (objectInstance != null) {
            injector.injectMembers(objectInstance)
            return objectInstance
        }

        return injector.getInstance(pluginClass)
    }

    override fun createPluginContainer(candidate: PluginCandidate, environment: PluginEnvironment): Optional<LanternPluginContainer> =
            LanternPluginContainer(candidate).asOptional()
}