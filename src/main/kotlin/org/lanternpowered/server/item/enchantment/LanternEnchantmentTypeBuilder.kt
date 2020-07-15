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
package org.lanternpowered.server.item.enchantment

import org.lanternpowered.api.item.enchantment.EnchantmentType
import org.lanternpowered.api.item.enchantment.EnchantmentTypeBuilder
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.api.x.item.enchantment.XEnchantmentType
import org.lanternpowered.server.catalog.AbstractNamedCatalogBuilder
import org.lanternpowered.server.text.translation.TranslationHelper.tr
import org.spongepowered.api.ResourceKey

class LanternEnchantmentTypeBuilder : AbstractNamedCatalogBuilder<XEnchantmentType, EnchantmentTypeBuilder>(), EnchantmentTypeBuilder {

    companion object {
        var idCounter: Int = 0
    }

    private var weight = 5
    private var levelRange = 1..1
    private var curse = false
    private var treasure = false
    private var enchantabilityRange: ((Int) -> IntRange)? = null
    private var compatibilityTester: ((EnchantmentType) -> Boolean)? = null

    override fun weight(weight: Int) = apply { this.weight = weight }
    override fun levelRange(levelRange: IntRange) = apply { this.levelRange = levelRange }
    override fun curse(curse: Boolean) = apply { this.curse = curse }
    override fun treasure(treasure: Boolean) = apply { this.treasure = treasure }
    override fun enchantabilityRange(provider: (Int) -> IntRange) = apply { this.enchantabilityRange = provider }
    override fun compatibilityTester(tester: (EnchantmentType) -> Boolean) = apply { this.compatibilityTester = tester }
    override fun name(name: Translation) = apply { this.name = name }
    override fun name(name: String) = apply { this.name = tr(name) }

    override fun build(key: ResourceKey, name: Translation): XEnchantmentType {
        return LanternEnchantmentType(key, name, idCounter++,
                this.levelRange, this.weight, this.treasure, this.curse, this.enchantabilityRange, this.compatibilityTester)
    }

    override fun reset() = apply {
        super.reset()
        this.name = null
        this.weight = 5
        this.levelRange = 1..1
        this.curse = false
        this.treasure = false
        this.enchantabilityRange = null
        this.compatibilityTester = null
    }
}
