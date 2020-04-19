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
package org.lanternpowered.api.item.enchantment

import org.lanternpowered.api.x.item.enchantment.XEnchantmentType
import org.spongepowered.api.util.NamedCatalogBuilder
import java.util.function.Predicate

/**
 * A builder to construct [EnchantmentType]s.
 */
interface EnchantmentTypeBuilder : NamedCatalogBuilder<XEnchantmentType, EnchantmentTypeBuilder> {

    fun weight(weight: Int): EnchantmentTypeBuilder
    fun levelRange(levelRange: IntRange): EnchantmentTypeBuilder
    fun maxLevel(level: Int): EnchantmentTypeBuilder = levelRange(1..level)
    fun curse(curse: Boolean = true): EnchantmentTypeBuilder
    fun treasure(treasure: Boolean = true): EnchantmentTypeBuilder
    fun enchantabilityRange(provider: (Int) -> IntRange): EnchantmentTypeBuilder
    fun compatibilityTester(tester: Predicate<EnchantmentType>): EnchantmentTypeBuilder = compatibilityTester(tester::test)
    fun compatibilityTester(tester: (EnchantmentType) -> Boolean): EnchantmentTypeBuilder
}
