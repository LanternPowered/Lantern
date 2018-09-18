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
package org.lanternpowered.api.script

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.catalog.CatalogTypeProperty
import org.lanternpowered.api.item.enchantment.EnchantmentTypeBuilder
import org.lanternpowered.api.world.weather.WeatherBuilder

// These methods are only allowed to be called in scripts

// TODO: Error when called anywhere else

inline fun <reified T : CatalogType> catalogRef(key: CatalogKey): CatalogTypeProperty<T> = CatalogTypeProperty(key, T::class)

/**
 * Gets a read only property which provides access to a [CatalogType]
 * of a specific type. This can be used when a specific catalog type
 * isn't available yet.
 */
inline fun <reified T : CatalogType> catalogRef(key: String): CatalogTypeProperty<T> = CatalogTypeProperty(CatalogKey.resolve(key), T::class)

fun weather(fn: WeatherBuilder.() -> Unit) = WeatherBuilder().apply(fn)

fun enchantment(vararg parentScripts: String, fn: EnchantmentTypeBuilder.() -> Unit) = EnchantmentTypeBuilder().apply(fn)
fun enchantment(vararg parentScripts: CatalogKey, fn: EnchantmentTypeBuilder.() -> Unit) = EnchantmentTypeBuilder().apply(fn)
fun enchantment(fn: EnchantmentTypeBuilder.() -> Unit) = EnchantmentTypeBuilder().apply(fn)
