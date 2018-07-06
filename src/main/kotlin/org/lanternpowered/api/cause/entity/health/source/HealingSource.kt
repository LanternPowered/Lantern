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
package org.lanternpowered.api.cause.entity.health.source

import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.event.cause.entity.health.HealingType
import org.spongepowered.api.event.cause.entity.health.source.EntityHealingSource
import org.spongepowered.api.event.cause.entity.health.source.HealingSource
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

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

inline fun BlockHealingSource(type: HealingType, location: Location<World>,
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
