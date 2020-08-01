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
package org.lanternpowered.server.data.key

import org.lanternpowered.api.effect.potion.PotionEffect
import org.lanternpowered.server.data.LocalKeyRegistry
import org.lanternpowered.server.item.ItemKeys
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Keys

fun <H : DataHolder> LocalKeyRegistry<H>.registerApplicablePotionEffects(vararg effects: PotionEffect) =
        registerApplicablePotionEffects(effects.toSet())

fun <H : DataHolder> LocalKeyRegistry<H>.registerApplicablePotionEffects(effects: Set<PotionEffect>) =
        registerApplicablePotionEffects { effects }

fun <H : DataHolder> LocalKeyRegistry<H>.registerApplicablePotionEffects(fn: H.() -> Set<PotionEffect>) {
    registerProvider(Keys.APPLICABLE_POTION_EFFECTS) {
        get(fn)
    }
}

fun <H : DataHolder> LocalKeyRegistry<H>.registerUseDuration(duration: Int) {
    register(ItemKeys.MINIMUM_USE_DURATION, duration)
    register(ItemKeys.MAXIMUM_USE_DURATION, duration)
}

fun <H : DataHolder> LocalKeyRegistry<H>.registerUseDuration(duration: IntRange) {
    register(ItemKeys.MINIMUM_USE_DURATION, duration.first)
    register(ItemKeys.MAXIMUM_USE_DURATION, duration.last)
}

