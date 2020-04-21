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
package org.lanternpowered.server.registry

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.registry.FactoryRegistry

object LanternFactoryRegistry : FactoryRegistry {

    private val factories = mutableMapOf<Class<*>, Any>()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> provideFactory(clazz: Class<T>): T =
            this.factories[clazz] as? T ?: error("There's no factory registered with the type ${clazz.simpleName}.")

    fun register(factory: Any) {
        val factoryTypes = TypeToken.of(factory.javaClass).types.rawTypes()
                .filterNot { it as Class<*> != Object::class.java }
                .filter { it.isInterface }
        for (factoryType in factoryTypes)
            this.factories.putIfAbsent(factoryType, factory)
    }
}
