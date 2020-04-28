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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.cause.entity.damage

typealias DamageFunction = org.spongepowered.api.event.cause.entity.damage.DamageFunction
typealias DamageModifier = org.spongepowered.api.event.cause.entity.damage.DamageModifier
typealias DamageModifierType = org.spongepowered.api.event.cause.entity.damage.DamageModifierType
typealias DamageModifierTypes = org.spongepowered.api.event.cause.entity.damage.DamageModifierTypes
typealias DamageType = org.spongepowered.api.event.cause.entity.damage.DamageType

/**
 * Constructs a new [DamageFunction].
 *
 * @param modifier The damage modifier
 * @param fn The function
 * @return The damage function
 */
inline fun damageFunctionOf(modifier: DamageModifier, noinline fn: (Double) -> Double): DamageFunction = DamageFunction.of(modifier, fn)
