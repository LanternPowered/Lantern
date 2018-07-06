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
@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package org.lanternpowered.api.effect.potion

import org.lanternpowered.api.ext.*

typealias PotionEffect = org.spongepowered.api.effect.potion.PotionEffect
typealias PotionEffectBuilder = org.spongepowered.api.effect.potion.PotionEffect.Builder
typealias PotionEffectType = org.spongepowered.api.effect.potion.PotionEffectType
typealias PotionEffectTypes = org.spongepowered.api.effect.potion.PotionEffectTypes

/**
 * Constructs a new [PotionEffect] with the given [PotionEffectType] and builder function.
 *
 * @param type The potion effect type
 * @param fn The builder function
 * @return The constructed potion effect
 */
inline fun PotionEffect(type: PotionEffectType, fn: PotionEffectBuilder.() -> Unit): PotionEffect =
        PotionEffectBuilder().potionType(type).duration(1).apply(fn).build()

inline fun PotionEffect(type: PotionEffectType, duration: Int = 1, amplifier: Int = 0,
                        ambient: Boolean = false, particles: Boolean = true): PotionEffect =
        PotionEffect(type) { duration(duration).amplifier(amplifier).ambience(ambient).particles(particles) }

/**
 * Constructs a new [PotionEffectBuilder].
 *
 * @return The constructed potion effect builder
 */
inline fun PotionEffectBuilder(): PotionEffectBuilder = builderOf()
