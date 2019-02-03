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
package org.lanternpowered.api.item.enchantment

import org.lanternpowered.api.util.builder.CatalogBuilder
import org.lanternpowered.api.x.item.enchantment.XEnchantmentType
import java.util.function.Predicate

/**
 * A builder to construct [EnchantmentType]s.
 */
interface EnchantmentTypeBuilder : CatalogBuilder<XEnchantmentType, EnchantmentTypeBuilder> {

    fun weight(weight: Int): EnchantmentTypeBuilder
    fun levelRange(levelRange: IntRange): EnchantmentTypeBuilder
    fun maxLevel(level: Int): EnchantmentTypeBuilder = levelRange(1..level)
    fun curse(curse: Boolean = true): EnchantmentTypeBuilder
    fun treasure(treasure: Boolean = true): EnchantmentTypeBuilder
    fun enchantabilityRange(provider: (Int) -> IntRange): EnchantmentTypeBuilder
    fun compatibilityTester(tester: Predicate<EnchantmentType>): EnchantmentTypeBuilder = compatibilityTester(tester::test)
    fun compatibilityTester(tester: (EnchantmentType) -> Boolean): EnchantmentTypeBuilder
}
