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

package org.lanternpowered.server.ext

import org.lanternpowered.api.util.optional.orNull
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.data.type.NotePitch
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.particle.ParticleOption
import org.spongepowered.api.effect.particle.ParticleOptions
import org.spongepowered.api.effect.particle.ParticleType
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.util.Color
import org.spongepowered.api.util.Direction

inline fun <V> ParticleEffect.option(option: ParticleOption<V>): V? = getOption(option).orNull()
inline fun <V> ParticleEffect.optionOrDefault(option: ParticleOption<V>): V? = getOptionOrDefault(option).orNull()

inline fun ParticleEffect(type: ParticleType, fn: ParticleEffect.Builder.() -> Unit = {}): ParticleEffect =
        ParticleEffect.builder().type(type).apply(fn).build()

inline fun ParticleEffect.Builder.block(block: BlockState): ParticleEffect.Builder = option(ParticleOptions.BLOCK_STATE, block)
inline fun ParticleEffect.Builder.block(block: BlockType): ParticleEffect.Builder = option(ParticleOptions.BLOCK_STATE, block.defaultState)
inline fun ParticleEffect.Builder.item(item: ItemStackSnapshot): ParticleEffect.Builder = option(ParticleOptions.ITEM_STACK_SNAPSHOT, item)
inline fun ParticleEffect.Builder.item(item: ItemStack): ParticleEffect.Builder = option(ParticleOptions.ITEM_STACK_SNAPSHOT, item.createSnapshot())
inline fun ParticleEffect.Builder.item(item: ItemType): ParticleEffect.Builder =
        option(ParticleOptions.ITEM_STACK_SNAPSHOT, ItemStack.of(item, 1).createSnapshot())
inline fun ParticleEffect.Builder.color(color: Color): ParticleEffect.Builder = option(ParticleOptions.COLOR, color)
inline fun ParticleEffect.Builder.direction(direction: Direction): ParticleEffect.Builder = option(ParticleOptions.DIRECTION, direction)
inline fun ParticleEffect.Builder.note(notePitch: NotePitch): ParticleEffect.Builder = option(ParticleOptions.NOTE, notePitch)
inline fun ParticleEffect.Builder.scale(scale: Double): ParticleEffect.Builder = option(ParticleOptions.SCALE, scale)
inline fun ParticleEffect.Builder.slowHorizontalVelocity(slow: Boolean = true): ParticleEffect.Builder =
        option(ParticleOptions.SLOW_HORIZONTAL_VELOCITY, slow)
