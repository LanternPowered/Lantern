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

package org.lanternpowered.api.cause.entity.damage.source

import org.lanternpowered.api.cause.entity.damage.DamageType
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.FallingBlock
import org.spongepowered.api.world.Location

typealias DamageSource = org.spongepowered.api.event.cause.entity.damage.source.DamageSource
typealias DamageSourceBuilder = org.spongepowered.api.event.cause.entity.damage.source.DamageSource.Builder
typealias DamageSources = org.spongepowered.api.event.cause.entity.damage.source.DamageSources
typealias BlockDamageSource = org.spongepowered.api.event.cause.entity.damage.source.BlockDamageSource
typealias BlockDamageSourceBuilder = org.spongepowered.api.event.cause.entity.damage.source.BlockDamageSource.Builder
typealias EntityDamageSource = org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource
typealias EntityDamageSourceBuilder = org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource.Builder
typealias FallingBlockDamageSource = org.spongepowered.api.event.cause.entity.damage.source.FallingBlockDamageSource
typealias FallingBlockDamageSourceBuilder = org.spongepowered.api.event.cause.entity.damage.source.FallingBlockDamageSource.Builder
typealias IndirectEntityDamageSource = org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource
typealias IndirectEntityDamageSourceBuilder = org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource.Builder

/**
 * Constructs a new [DamageSource] with the given [DamageType] and builder function.
 */
inline fun DamageSource(type: DamageType, fn: DamageSourceBuilder.() -> Unit = {}): DamageSource =
        DamageSourceBuilder().type(type).apply(fn).build()

/**
 * Constructs a new [BlockDamageSource] with the given [DamageType], [Location] and builder function.
 */
inline fun BlockDamageSource(type: DamageType, location: Location,
                             fn: BlockDamageSourceBuilder.() -> Unit = {}): BlockDamageSource =
        BlockDamageSource.builder().type(type).block(location).apply(fn).build()

/**
 * Constructs a new [BlockDamageSource] with the given [DamageType], [BlockSnapshot] and builder function.
 */
inline fun BlockDamageSource(type: DamageType, snapshot: BlockSnapshot,
                             fn: BlockDamageSourceBuilder.() -> Unit = {}): BlockDamageSource =
        BlockDamageSourceBuilder().type(type).block(snapshot).apply(fn).build()

/**
 * Constructs a new [EntityDamageSource] with the given [DamageType], [Entity] and builder function.
 */
inline fun EntityDamageSource(type: DamageType, entity: Entity,
                              fn: EntityDamageSourceBuilder.() -> Unit = {}): EntityDamageSource =
        EntityDamageSourceBuilder().type(type).entity(entity).apply(fn).build()

/**
 * Constructs a new [IndirectEntityDamageSource] with the given [DamageType], [Entity], indirect [Entity] and builder function.
 */
inline fun IndirectEntityDamageSource(type: DamageType, entity: Entity, indirectSource: Entity,
                                      fn: IndirectEntityDamageSourceBuilder.() -> Unit = {}): IndirectEntityDamageSource =
        IndirectEntityDamageSourceBuilder().type(type).entity(entity).proxySource(indirectSource).apply(fn).build()

/**
 * Constructs a new [FallingBlockDamageSource] with the given [DamageType], [FallingBlock] and builder function.
 */
inline fun FallingBlockDamageSource(type: DamageType, fallingBlock: FallingBlock,
                                    fn: FallingBlockDamageSourceBuilder.() -> Unit = {}): FallingBlockDamageSource =
        FallingBlockDamageSourceBuilder().type(type).entity(fallingBlock).apply(fn).build()

/**
 * Constructs a new [DamageSourceBuilder].
 */
inline fun DamageSourceBuilder(): DamageSourceBuilder = DamageSource.builder()

/**
 * Constructs a new [BlockDamageSourceBuilder].
 */
inline fun BlockDamageSourceBuilder(): BlockDamageSourceBuilder = BlockDamageSource.builder()

/**
 * Constructs a new [EntityDamageSourceBuilder].
 */
inline fun EntityDamageSourceBuilder(): EntityDamageSourceBuilder = EntityDamageSource.builder()

/**
 * Constructs a new [IndirectEntityDamageSourceBuilder].
 */
inline fun IndirectEntityDamageSourceBuilder(): IndirectEntityDamageSourceBuilder = IndirectEntityDamageSource.builder()

/**
 * Constructs a new [FallingBlockDamageSourceBuilder].
 */
inline fun FallingBlockDamageSourceBuilder(): FallingBlockDamageSourceBuilder = FallingBlockDamageSource.builder()
