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

import com.google.inject.Injector
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.server.plugin.inject.PluginGuiceModule
import org.lanternpowered.server.util.guice.asSingleton
import org.lanternpowered.server.util.guice.getInstance
import org.spongepowered.api.config.ConfigManager
import org.spongepowered.plugin.Blackboard
import org.spongepowered.plugin.PluginCandidate
import org.spongepowered.plugin.PluginEnvironment
import org.spongepowered.plugin.jvm.JVMPluginLoader
import org.spongepowered.plugin.jvm.locator.JVMPluginResource
import java.util.Optional

class LanternPluginLoader : JVMPluginLoader<LanternPluginContainer>() {

    companion object {

        val PARENT_INJECTOR: Blackboard.Key<Injector> = Blackboard.Key.of("parent_injector", Injector::class.java)
    }

    override fun createPluginInstance(environment: PluginEnvironment, container: LanternPluginContainer, targetClassLoader: ClassLoader): Any {
        @Suppress("UNCHECKED_CAST")
        val pluginClass: Class<Any> = Class.forName(container.metadata.mainClass, true, targetClassLoader) as Class<Any>
        val objectInstance = pluginClass.kotlin.objectInstance

        val parentInjector = environment.blackboard.get(PARENT_INJECTOR).get()
        val configManager: ConfigManager = parentInjector.getInstance()

        if (objectInstance != null) {
            val module = object : PluginGuiceModule(container, configManager) {
                override fun configure() {
                    this.bind(pluginClass).toInstance(objectInstance)
                    super.configure()
                }
            }
            val injector = parentInjector.createChildInjector(module)
            container.injector = injector
            injector.injectMembers(objectInstance)
            return objectInstance
        }

        val module = object : PluginGuiceModule(container, configManager) {
            override fun configure() {
                this.bind(pluginClass).asSingleton()
                super.configure()
            }
        }
        val injector = parentInjector.createChildInjector(module)
        container.injector = injector
        return injector.getInstance(pluginClass)
    }

    override fun createPluginContainer(
            candidate: PluginCandidate<JVMPluginResource>, environment: PluginEnvironment
    ): Optional<LanternPluginContainer> = LanternPluginContainer(candidate).asOptional()
}
