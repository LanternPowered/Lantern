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
import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.registry.builderOf
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.entity.FallingBlock
import org.spongepowered.api.world.Location

typealias DamageSource = org.spongepowered.api.event.cause.entity.damage.source.DamageSource
typealias BlockDamageSource = org.spongepowered.api.event.cause.entity.damage.source.BlockDamageSource
typealias EntityDamageSource = org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource
typealias FallingBlockDamageSource = org.spongepowered.api.event.cause.entity.damage.source.FallingBlockDamageSource
typealias IndirectEntityDamageSource = org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource

private typealias DamageSourceBuilder = org.spongepowered.api.event.cause.entity.damage.source.DamageSource.Builder
private typealias BlockDamageSourceBuilder = org.spongepowered.api.event.cause.entity.damage.source.BlockDamageSource.Builder
private typealias EntityDamageSourceBuilder = org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource.Builder
private typealias IndirectEntityDamageSourceBuilder = org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource.Builder
private typealias FallingBlockDamageSourceBuilder = org.spongepowered.api.event.cause.entity.damage.source.FallingBlockDamageSource.Builder

/**
 * Constructs a new [DamageSource] with the given [DamageType].
 */
inline fun damageSourceOf(type: DamageType): DamageSource =
        builderOf<DamageSourceBuilder>().type(type).build()

/**
 * Constructs a new [BlockDamageSource] with the given [DamageType] and [Location].
 */
inline fun blockDamageSourceOf(type: DamageType, location: Location): BlockDamageSource =
        builderOf<BlockDamageSourceBuilder>().type(type).block(location).build()

/**
 * Constructs a new [BlockDamageSource] with the given [DamageType] and [BlockSnapshot].
 */
inline fun blockDamageSourceOf(type: DamageType, snapshot: BlockSnapshot): BlockDamageSource =
        builderOf<BlockDamageSourceBuilder>().type(type).block(snapshot).build()

/**
 * Constructs a new [EntityDamageSource] with the given [DamageType] and [Entity].
 */
inline fun entityDamageSourceOf(type: DamageType, entity: Entity): EntityDamageSource =
        builderOf<EntityDamageSourceBuilder>().type(type).entity(entity).build()

/**
 * Constructs a new [FallingBlockDamageSource] with the given [DamageType], [FallingBlock] and builder function.
 */
inline fun entityDamageSourceOf(type: DamageType, fallingBlock: FallingBlock): FallingBlockDamageSource =
        builderOf<FallingBlockDamageSourceBuilder>().type(type).entity(fallingBlock).build()

fun EntityDamageSource.indirectBy(entity: Entity): IndirectEntityDamageSource =
        builderOf<IndirectEntityDamageSourceBuilder>().apply {
            val source = this@indirectBy
            if (source.isAbsolute)
                absolute()
            if (source.isBypassingArmor)
                bypassesArmor()
            if (source.isExplosive)
                explosion()
            if (source.isFire)
                fire()
            if (source.isMagic)
                magical()
        }.build()
