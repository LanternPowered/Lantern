/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.api.registry

import org.lanternpowered.api.Lantern
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
    companion object : BuilderRegistry by Lantern.registry.builderRegistry
}
