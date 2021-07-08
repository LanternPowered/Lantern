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
package org.lanternpowered.server.entity.interfaces.vehicle

import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.entity.interfaces.IEntity
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.entity.vehicle.Boat
import org.spongepowered.api.util.AABB

interface IBoat : IEntity, Boat {

    override fun isInWater(): Boolean {
        val loc = location
        if (loc.block.type != BlockTypes.WATER) {
            return false
        }
        var aabb: AABB? = boundingBox.orElse(null) ?: return false
        val pos = loc.blockPosition.toDouble()
        aabb = aabb!!.expand(0.0, -0.1, 0.0)
        val min = pos.add(0.05, 0.05, 0.05)
        val max = pos.add(0.95, 0.95, 0.95)
        aabb = AABB(aabb!!.min.max(min), aabb.max.min(max))
        return !loc.extent.getIntersectingBlockCollisionBoxes(aabb).isEmpty()
    }

    override fun getMaxSpeed(): Double = get(LanternKeys.MAX_SPEED).orElse(DEFAULT_MAX_SPEED)
    override fun setMaxSpeed(maxSpeed: Double) { offer(LanternKeys.MAX_SPEED, maxSpeed) }

    override fun canMoveOnLand(): Boolean = get(LanternKeys.CAN_MOVE_ON_LAND).orElse(DEFAULT_CAN_MOVE_ON_LAND)
    override fun setMoveOnLand(moveOnLand: Boolean) { offer(LanternKeys.CAN_MOVE_ON_LAND, moveOnLand) }

    override fun getOccupiedDeceleration(): Double = get(LanternKeys.OCCUPIED_DECELERATION).orElse(DEFAULT_OCCUPIED_DECELERATION)
    override fun setOccupiedDeceleration(occupiedDeceleration: Double) { offer(LanternKeys.OCCUPIED_DECELERATION, occupiedDeceleration) }

    override fun getUnoccupiedDeceleration(): Double = get(LanternKeys.UNOCCUPIED_DECELERATION).orElse(DEFAULT_UNOCCUPIED_DECELERATION)
    override fun setUnoccupiedDeceleration(unoccupiedDeceleration: Double) { offer(LanternKeys.UNOCCUPIED_DECELERATION, unoccupiedDeceleration) }

    companion object {

        const val DEFAULT_MAX_SPEED = 0.4
        const val DEFAULT_OCCUPIED_DECELERATION = 0.0
        const val DEFAULT_UNOCCUPIED_DECELERATION = 0.8
        const val DEFAULT_CAN_MOVE_ON_LAND = false
    }
}
