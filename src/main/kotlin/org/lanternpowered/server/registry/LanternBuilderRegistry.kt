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

import org.lanternpowered.api.registry.BaseBuilder
import org.lanternpowered.api.registry.BuilderRegistry
import org.lanternpowered.api.util.uncheckedCast
import java.util.function.Supplier
import kotlin.reflect.KClass

object LanternBuilderRegistry : BuilderRegistry {

    private val builders = mutableMapOf<Class<*>, Supplier<BaseBuilder<*,*>>>()

    @Suppress("UNCHECKED_CAST")
    override fun <T : BaseBuilder<*, in T>> provideBuilder(builderClass: Class<T>): T =
            this.builders[builderClass] as T

    inline fun <reified T : BaseBuilder<*, in T>> register(noinline supplier: () -> T) = register(T::class, supplier)

    fun <T : BaseBuilder<*, in T>> register(builderClass: KClass<T>, supplier: () -> T) =
            register(builderClass.java, Supplier(supplier))

    fun <T : BaseBuilder<*, in T>> register(builderClass: KClass<T>, supplier: Supplier<in T>) =
            register(builderClass.java, supplier)

    fun <T : BaseBuilder<*, in T>> register(builderClass: Class<T>, supplier: Supplier<in T>) {
        this.builders[builderClass] = supplier.uncheckedCast()
    }
}
