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

import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.persistence.Queries
import org.spongepowered.api.item.enchantment.Enchantment
import org.spongepowered.api.item.enchantment.EnchantmentType
import java.util.Optional

class LanternEnchantmentBuilder : AbstractDataBuilder<Enchantment>(Enchantment::class.java, 1), Enchantment.Builder {

    private var enchantmentType: EnchantmentType? = null
    private var level: Int? = null

    override fun from(enchantment: Enchantment): Enchantment.Builder = apply {
        this.enchantmentType = enchantment.type
        this.level = enchantment.level
    }

    override fun reset(): Enchantment.Builder = apply {
        this.enchantmentType = null
        this.level = null
    }

    override fun type(enchantmentType: EnchantmentType): Enchantment.Builder = apply {
        this.enchantmentType = enchantmentType
    }

    override fun level(level: Int): Enchantment.Builder = apply {
        check(level in Short.MIN_VALUE..Short.MAX_VALUE) {
            "The level must be between ${Short.MIN_VALUE} and ${Short.MAX_VALUE}, and not $level" }
        this.level = level
    }

    override fun build(): Enchantment {
        val enchantmentType = checkNotNull(this.enchantmentType) { "The enchantment type must be set" }
        val level = checkNotNull(this.level) { "The level must be set" }
        return LanternEnchantment(enchantmentType, level)
    }

    override fun buildContent(container: DataView): Optional<Enchantment> {
        if (!container.contains(Queries.ENCHANTMENT_ID, Queries.LEVEL)) {
            return Optional.empty()
        }
        val enchantmentType = container.getCatalogType(Queries.ENCHANTMENT_ID, EnchantmentType::class.java)
        val level = container.getInt(Queries.LEVEL).get()
        return enchantmentType.map { type -> Enchantment.builder().type(type).level(level).build() }
    }

}
