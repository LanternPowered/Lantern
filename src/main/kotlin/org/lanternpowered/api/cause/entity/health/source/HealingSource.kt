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
package org.lanternpowered.api.cause.entity.health.source

import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.event.cause.entity.health.HealingType
import org.spongepowered.api.event.cause.entity.health.source.EntityHealingSource
import org.spongepowered.api.event.cause.entity.health.source.HealingSource
import org.spongepowered.api.world.Location

typealias BlockHealingSource = org.spongepowered.api.event.cause.entity.health.source.BlockHealingSource
typealias BlockHealingSourceBuilder = org.spongepowered.api.event.cause.entity.health.source.BlockHealingSource.Builder
typealias EntityHealingSource = org.spongepowered.api.event.cause.entity.health.source.EntityHealingSource
typealias EntityHealingSourceBuilder = org.spongepowered.api.event.cause.entity.health.source.EntityHealingSource.Builder
typealias HealingSource = org.spongepowered.api.event.cause.entity.health.source.HealingSource
typealias HealingSourceBuilder = org.spongepowered.api.event.cause.entity.health.source.HealingSource.Builder
typealias HealingSources = org.spongepowered.api.event.cause.entity.health.source.HealingSources
typealias IndirectEntityHealingSource = org.spongepowered.api.event.cause.entity.health.source.IndirectEntityHealingSource
typealias IndirectEntityHealingSourceBuilder = org.spongepowered.api.event.cause.entity.health.source.IndirectEntityHealingSource.Builder

inline fun HealingSource(type: HealingType, fn: HealingSourceBuilder.() -> Unit = {}): HealingSource =
        HealingSource.builder().type(type).apply(fn).build()

inline fun BlockHealingSource(type: HealingType, location: Location,
                             fn: BlockHealingSourceBuilder.() -> Unit = {}): BlockHealingSource =
        BlockHealingSource.builder().type(type).block(location).apply(fn).build()

inline fun BlockHealingSource(type: HealingType, snapshot: BlockSnapshot,
                              fn: BlockHealingSourceBuilder.() -> Unit = {}): BlockHealingSource =
        BlockHealingSource.builder().type(type).block(snapshot).apply(fn).build()

inline fun EntityHealingSource(type: HealingType, entity: Entity,
                               fn: EntityHealingSourceBuilder.() -> Unit = {}): EntityHealingSource =
        EntityHealingSource.builder().type(type).entity(entity).apply(fn).build()

inline fun IndirectEntityHealingSource(type: HealingType, entity: Entity, indirectSource: Entity,
                                       fn: IndirectEntityHealingSourceBuilder.() -> Unit = {}): IndirectEntityHealingSource =
        IndirectEntityHealingSource.builder().type(type).entity(entity).indirectEntity(indirectSource).apply(fn).build()
