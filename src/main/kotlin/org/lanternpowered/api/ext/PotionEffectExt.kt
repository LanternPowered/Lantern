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
@file:JvmName("PotionEffectHelper")
@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package org.lanternpowered.api.ext

import org.lanternpowered.api.effect.potion.PotionEffect
import org.lanternpowered.api.effect.potion.PotionEffectBuilder
import org.lanternpowered.api.effect.potion.PotionEffectType
import java.util.ArrayList
import java.util.HashMap

inline fun potionEffectOf(type: PotionEffectType, amplifier: Int, duration: Int, ambient: Boolean = false, particles: Boolean = true): PotionEffect =
        PotionEffect.builder().potionType(type).amplifier(amplifier).duration(duration).ambient(ambient).particles(particles).build()

// Fix sponge typo
inline fun PotionEffectBuilder.ambient(ambient: Boolean = true): PotionEffectBuilder = ambience(ambient)

fun Collection<PotionEffect>.merge(that: Collection<PotionEffect>): MutableList<PotionEffect> {
    val effectsByType = HashMap<PotionEffectType, PotionEffect>()
    for (effect in this) {
        effectsByType[effect.type] = effect
    }
    val result = ArrayList<PotionEffect>()
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
    builder.particles(that.showParticles)
    return builder.build()
}
