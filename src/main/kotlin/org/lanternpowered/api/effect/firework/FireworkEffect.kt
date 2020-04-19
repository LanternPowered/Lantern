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

package org.lanternpowered.api.effect.firework

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

typealias FireworkEffect = org.spongepowered.api.item.FireworkEffect
typealias FireworkEffectBuilder = org.spongepowered.api.item.FireworkEffect.Builder
typealias FireworkShape = org.spongepowered.api.item.FireworkShape
typealias FireworkShapes = org.spongepowered.api.item.FireworkShapes

inline val FireworkEffect.flickers: Boolean
    get() = flickers()

inline val FireworkEffect.hasTrail: Boolean
    get() = hasTrail()

/**
 * Constructs a new [FireworkEffect] using the builder function.
 *
 * @param fn The builder function
 * @return The constructed firework effect
 */
inline fun fireworkEffect(fn: FireworkEffectBuilder.() -> Unit): FireworkEffect {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return FireworkEffect.builder().apply(fn).build()
}

