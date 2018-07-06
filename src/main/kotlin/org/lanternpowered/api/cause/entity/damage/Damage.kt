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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.cause.entity.damage

typealias DamageFunction = org.spongepowered.api.event.cause.entity.damage.DamageFunction
typealias DamageModifier = org.spongepowered.api.event.cause.entity.damage.DamageModifier
typealias DamageModifierType = org.spongepowered.api.event.cause.entity.damage.DamageModifierType
typealias DamageModifierTypes = org.spongepowered.api.event.cause.entity.damage.DamageModifierTypes
typealias DamageType = org.spongepowered.api.event.cause.entity.damage.DamageType
typealias DamageTypes = org.spongepowered.api.event.cause.entity.damage.DamageTypes

/**
 * Constructs a new [DamageFunction].
 *
 * @param modifier The damage modifier
 * @param fn The function
 * @return The damage function
 */
inline fun DamageFunction(modifier: DamageModifier, noinline fn: (Double) -> Double = { 0.0 }) = DamageFunction.of(modifier, fn)
