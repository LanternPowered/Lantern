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
@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package org.lanternpowered.api.effect.potion

typealias PotionEffect = org.spongepowered.api.effect.potion.PotionEffect
typealias PotionEffectBuilder = org.spongepowered.api.effect.potion.PotionEffect.Builder
typealias PotionEffectType = org.spongepowered.api.effect.potion.PotionEffectType
typealias PotionEffectTypes = org.spongepowered.api.effect.potion.PotionEffectTypes

fun potionEffectOf(
        type: PotionEffectType, amplifier: Int, duration: Int,
        ambient: Boolean = false,
        showParticles: Boolean = true,
        showIcon: Boolean = true
): PotionEffect = PotionEffect.builder().potionType(type).amplifier(amplifier).duration(duration)
        .ambient(ambient).showParticles(showParticles).showIcon(showIcon).build()
