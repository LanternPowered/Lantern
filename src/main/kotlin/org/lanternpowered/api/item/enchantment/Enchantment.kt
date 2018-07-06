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
@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package org.lanternpowered.api.item.enchantment

import org.lanternpowered.api.ext.*

typealias Enchantment = org.spongepowered.api.item.enchantment.Enchantment
typealias EnchantmentBuilder = org.spongepowered.api.item.enchantment.Enchantment.Builder

/**
 * Constructs a new [Enchantment] with the given [EnchantmentType] and level. If no
 * level is specified, then will the minimum one be used instead.
 *
 * @param type The enchantment type
 * @param level The level of the enchantment
 * @return The constructed enchantment
 */
inline fun Enchantment(type: EnchantmentType, level: Int = type.minimumLevel, fn: EnchantmentBuilder.() -> Unit = {}): Enchantment =
        EnchantmentBuilder().type(type).level(level).apply(fn).build()

/**
 * Constructs a new [EnchantmentBuilder].
 *
 * @return The constructed enchantment builder
 */
inline fun EnchantmentBuilder(): EnchantmentBuilder = builderOf()
