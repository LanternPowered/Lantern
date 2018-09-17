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

package org.lanternpowered.api.ext

import com.flowpowered.math.vector.Vector3d
import org.lanternpowered.api.block.entity.BlockEntity
import org.lanternpowered.api.entity.Transform
import org.lanternpowered.api.world.Location
import org.lanternpowered.api.world.World
import org.lanternpowered.api.x.world.XWorld
import org.lanternpowered.api.x.world.extent.XEntityUniverse
import org.lanternpowered.api.x.world.extent.XExtent
import org.lanternpowered.api.x.world.weather.XWeatherUniverse
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.util.AABB
import org.spongepowered.api.world.extent.EntityUniverse
import org.spongepowered.api.world.extent.Extent

/**
 * The weather universe of the world, if supported.
 */
inline val World.weatherUniverse: XWeatherUniverse? get() = (this as XWorld).weatherUniverse

inline fun EntityUniverse.createEntity(type: EntityType, position: Vector3d, noinline fn: Entity.() -> Unit): Entity
        = (this as XEntityUniverse).createEntity(type, position, fn)

inline fun EntityUniverse.createEntityNaturally(type: EntityType, position: Vector3d, noinline fn: Entity.() -> Unit): Entity
        = (this as XEntityUniverse).createEntityNaturally(type, position, fn)

inline fun <E : Extent> Location<E>.toTransform(rotation: Vector3d = Vector3d.ZERO, scale: Vector3d = Vector3d.ONE)
        = Transform(this, rotation, scale)

inline val Location<*>.blockEntity: BlockEntity? get() = !this.tileEntity

/**
 * Gets the collision [AABB]s of the block at the given coordinates.
 *
 * @param x The x coordinate
 * @param y The y coordinate
 * @param z The z coordinate
 * @return The collision boxes, or empty if none were found
 */
fun Extent.getBlockCollisionBoxes(x: Int, y: Int, z: Int)
        = (this as XExtent).getBlockCollisionBoxes(x, y, z)

/**
 * Gets whether there is at least one entity intersecting
 * with the [AABB].
 *
 * @param box The box to check collisions for
 */
fun Extent.hasIntersectingEntities(box: AABB)
        = (this as XExtent).hasIntersectingEntities(box)

/**
 * Gets whether there is at least one entity intersecting
 * with the [AABB] that matches the given filter.
 *
 * @param box The box to check collisions for
 * @param filter The entity filter
 */
fun Extent.hasIntersectingEntities(box: AABB, filter: (Entity) -> Boolean)
        = (this as XExtent).hasIntersectingEntities(box, filter)
