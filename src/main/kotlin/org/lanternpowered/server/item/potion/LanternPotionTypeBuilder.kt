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
package org.lanternpowered.server.item.potion

import org.lanternpowered.api.effect.potion.PotionEffect
import org.lanternpowered.api.item.potion.PotionType
import org.lanternpowered.api.item.potion.PotionTypeBuilder
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.resolveNamespacedKey
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.server.catalog.AbstractCatalogBuilder
import org.lanternpowered.server.effect.potion.LanternPotionType
import org.lanternpowered.server.game.registry.InternalRegistries

class LanternPotionTypeBuilder : AbstractCatalogBuilder<PotionType, PotionTypeBuilder>(), PotionTypeBuilder {

    private var translationKey: String? = null
    private val effects = mutableListOf<PotionEffect>()

    override fun addEffect(potionEffect: PotionEffect) = apply { this.effects.add(potionEffect) }
    override fun translationKey(key: String) = apply { this.translationKey = key }

    override fun build(key: NamespacedKey): PotionType {
        val translationKey = this.translationKey ?: key.value
        val effects = this.effects.toImmutableList()
        val internalId = internalPotionTypes[key] ?: 0
        return LanternPotionType(key, translationKey, internalId, effects)
    }

    companion object {

        private val internalPotionTypes = InternalRegistries.load("potion", ::resolveNamespacedKey)
    }
}
