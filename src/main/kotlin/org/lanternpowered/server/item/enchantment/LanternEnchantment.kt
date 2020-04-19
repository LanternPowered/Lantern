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

import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.Queries
import org.spongepowered.api.item.enchantment.Enchantment
import org.spongepowered.api.item.enchantment.EnchantmentType

data class LanternEnchantment(private val enchantmentType: EnchantmentType, private val level: Int) : Enchantment {

    override fun getType(): EnchantmentType = this.enchantmentType
    override fun getLevel(): Int = this.level

    override fun getContentVersion(): Int = 1
    override fun toContainer(): DataContainer {
        return DataContainer.createNew()
                .set(Queries.CONTENT_VERSION, contentVersion)
                .set(Queries.ENCHANTMENT_ID, type.key)
                .set(Queries.LEVEL, level)
    }
}
