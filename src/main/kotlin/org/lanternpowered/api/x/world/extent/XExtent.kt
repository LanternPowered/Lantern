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
package org.lanternpowered.api.x.world.extent

import org.lanternpowered.api.entity.Entity
import org.spongepowered.api.util.AABB
import org.spongepowered.api.world.extent.Extent

interface XExtent : Extent, XEntityUniverse {

    /**
     * Gets the collision [AABB]s of the block at the given coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     * @return The collision boxes, or empty if none were found
     */
    fun getBlockCollisionBoxes(x: Int, y: Int, z: Int): Collection<AABB>

    /**
     * Gets whether there is at least one entity intersecting
     * with the [AABB].
     *
     * @param box The box to check collisions for
     */
    @JvmDefault
    fun hasIntersectingEntities(box: AABB) = hasIntersectingEntities(box) { true }

    /**
     * Gets whether there is at least one entity intersecting
     * with the [AABB] that matches the given filter.
     *
     * @param box The box to check collisions for
     * @param filter The entity filter
     */
    fun hasIntersectingEntities(box: AABB, filter: (Entity) -> Boolean): Boolean
}
