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
package org.lanternpowered.server.item.behavior.vanilla.consumable

import org.lanternpowered.api.effect.potion.mergeWith
import org.lanternpowered.api.util.collections.immutableSetOf
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.data.Keys
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.item.inventory.ItemStack

val PotionEffectsProvider: ItemStack.() -> Set<PotionEffect> = {
    val potionType = this.get(Keys.POTION_TYPE).orNull()
    // The base potion effects based on the potion type
    var potionEffects = potionType?.effects
    // Add extra customizable potion effects
    val extraPotionEffects = this.get(Keys.POTION_EFFECTS).orNull()
    if (extraPotionEffects != null) {
        potionEffects = potionEffects?.mergeWith(extraPotionEffects) ?: extraPotionEffects
    }
    potionEffects?.toImmutableSet() ?: immutableSetOf()
}
