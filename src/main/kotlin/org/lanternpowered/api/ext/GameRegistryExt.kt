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
@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("GameRegistryHelper")

package org.lanternpowered.api.ext

import org.lanternpowered.api.GameRegistry
import org.lanternpowered.api.Lantern
import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.util.builder.BaseBuilder
import kotlin.reflect.KClass

/**
 * Constructs a builder for the given builder type [T].
 *
 * @param T The builder type
 */
inline fun <reified T : BaseBuilder<*, in T>> builderOf(): T = Lantern.registry.createBuilder(T::class.java)

/**
 * Constructs a builder for the given builder type [T].
 *
 * @param T The builder type
 */
inline fun <T : BaseBuilder<*, in T>> builderOf(clazz: KClass<T>): T = Lantern.registry.createBuilder(clazz.java)

/**
 * Gets the factory for the given factory type [T].
 *
 * @param T The factory type
 */
inline fun <T : Any> factoryOf(clazz: KClass<T>): T = Lantern.registry.requireFactory(clazz.java)

/**
 * Gets the factory for the given factory type [T].
 */
inline fun <reified T : Any> factoryOf(): T = Lantern.registry.requireFactory(T::class.java)

// Helpers to allow using the kotlin class, and unbox into nullable

fun <T : CatalogType> GameRegistry.getType(type: KClass<T>, id: String): T? = getType(type.java, CatalogKey.resolve(id)).orNull()
fun <T : CatalogType> GameRegistry.getType(type: KClass<T>, key: CatalogKey): T? = getType(type.java, key).orNull()
fun <T : CatalogType> GameRegistry.getAllOf(type: KClass<T>): Collection<T> = getAllOf(type.java)
