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

import org.lanternpowered.api.registry.builderOf
import java.util.function.Supplier

typealias PotionEffect = org.spongepowered.api.effect.potion.PotionEffect
typealias PotionEffectBuilder = org.spongepowered.api.effect.potion.PotionEffect.Builder
typealias PotionEffectType = org.spongepowered.api.effect.potion.PotionEffectType
typealias PotionEffectTypes = org.spongepowered.api.effect.potion.PotionEffectTypes

fun potionEffectOf(
        type: PotionEffectType,
        amplifier: Int,
        duration: Int,
        ambient: Boolean = false,
        showParticles: Boolean = true,
        showIcon: Boolean = true
): PotionEffect = PotionEffect.builder().potionType(type).amplifier(amplifier).duration(duration)
        .ambient(ambient).showParticles(showParticles).showIcon(showIcon).build()

fun potionEffectOf(
        type: Supplier<out PotionEffectType>,
        amplifier: Int,
        duration: Int,
        ambient: Boolean = false,
        showParticles: Boolean = true,
        showIcon: Boolean = true
): PotionEffect = PotionEffect.builder().potionType(type).amplifier(amplifier).duration(duration)
        .ambient(ambient).showParticles(showParticles).showIcon(showIcon).build()

fun Collection<PotionEffect>.merge(that: Collection<PotionEffect>): MutableList<PotionEffect> {
    val effectsByType = mutableMapOf<PotionEffectType, PotionEffect>()
    for (effect in this) {
        effectsByType[effect.type] = effect
    }
    val result = mutableListOf<PotionEffect>()
    for (effect in that) {
        val other = effectsByType.remove(effect.type)
        if (other != null) {
            result.add(effect.merge(other))
        } else {
            result.add(effect)
        }
    }
    result.addAll(effectsByType.values)
    return result
}

/**
 * Merges this [PotionEffect] with the other one.
 *
 * @param that The potion effect to merge with
 * @return The merged potion effect
 */
fun PotionEffect.merge(that: PotionEffect): PotionEffect {
    val builder = builderOf<PotionEffectBuilder>().from(this)
    if (that.amplifier > amplifier) {
        builder.amplifier(that.amplifier).duration(that.duration)
    } else if (that.amplifier == amplifier && duration < that.duration) {
        builder.duration(that.duration)
    } else if (!that.isAmbient && isAmbient) {
        builder.ambient(that.isAmbient)
    }
    builder.showParticles(that.showsParticles())
    builder.showIcon(that.showsIcon())
    return builder.build()
}
