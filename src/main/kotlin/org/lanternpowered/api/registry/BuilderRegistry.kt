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
package org.lanternpowered.api.registry

import org.spongepowered.api.item.inventory.ItemStack
import kotlin.reflect.KClass

/**
 * Constructs a builder for the given builder type [T].
 *
 * @param T The builder type
 */
inline fun <reified T : BaseBuilder<*, in T>> builderOf(): T = BuilderRegistry.provideBuilder()

/**
 * Gets a builder of the desired class type, examples may include:
 * [ItemStack.Builder], etc.
 *
 * @param T The type of builder
 * @throws UnknownTypeException If the type provided has not been registered
 * @return The builder, if available
 */
inline fun <reified T : BaseBuilder<*, in T>> BuilderRegistry.provideBuilder(): T = provideBuilder(T::class)

/**
 * The builder registry.
 */
interface BuilderRegistry : org.spongepowered.api.registry.BuilderRegistry {

    /**
     * Gets a builder of the desired class type, examples may include:
     * [ItemStack.Builder], etc.
     *
     * @param builderClass The class of the builder
     * @param T The type of builder
     * @throws UnknownTypeException If the type provided has not been registered
     * @return The builder, if available
     */
    fun <T : BaseBuilder<*, in T>> provideBuilder(builderClass: KClass<T>): T = provideBuilder(builderClass.java)

    /**
     * Gets a builder of the desired class type, examples may include:
     * [ItemStack.Builder], etc.
     *
     * @param builderClass The class of the builder
     * @param T The type of builder
     * @throws UnknownTypeException If the type provided has not been registered
     * @return The builder, if available
     */
    override fun <T : BaseBuilder<*, in T>> provideBuilder(builderClass: Class<T>): T

    /**
     * The singleton instance of the builder registry.
     */
    companion object : BuilderRegistry by GameRegistry.builderRegistry
}
