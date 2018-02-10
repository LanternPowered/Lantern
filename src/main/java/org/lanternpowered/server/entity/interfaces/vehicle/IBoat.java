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
package org.lanternpowered.server.entity.interfaces.vehicle;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.interfaces.IEntity;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.vehicle.Boat;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public interface IBoat extends IEntity, Boat {

    double DEFAULT_MAX_SPEED = 0.4;
    double DEFAULT_OCCUPIED_DECELERATION = 0.0;
    double DEFAULT_UNOCCUPIED_DECELERATION = 0.8;
    boolean DEFAULT_CAN_MOVE_ON_LAND = false;

    @Override
    default boolean isInWater() {
        final Location<World> loc = getLocation();
        if (loc.getBlock().getType() != BlockTypes.WATER) {
            return false;
        }
        AABB aabb = getBoundingBox().orElse(null);
        if (aabb == null) {
            return false;
        }
        final Vector3d pos = loc.getBlockPosition().toDouble();
        aabb = aabb.expand(0, -0.1, 0);
        final Vector3d min = pos.add(0.05, 0.05, 0.05);
        final Vector3d max = pos.add(0.95, 0.95, 0.95);
        aabb = new AABB(aabb.getMin().max(min), aabb.getMax().min(max));
        return !loc.getExtent().getIntersectingBlockCollisionBoxes(aabb).isEmpty();
    }

    @Override
    default double getMaxSpeed() {
        return get(LanternKeys.MAX_SPEED).orElse(DEFAULT_MAX_SPEED);
    }

    @Override
    default void setMaxSpeed(double maxSpeed) {
        offer(LanternKeys.MAX_SPEED, maxSpeed);
    }

    @Override
    default boolean canMoveOnLand() {
        return get(LanternKeys.CAN_MOVE_ON_LAND).orElse(DEFAULT_CAN_MOVE_ON_LAND);
    }

    @Override
    default void setMoveOnLand(boolean moveOnLand) {
        offer(LanternKeys.CAN_MOVE_ON_LAND, moveOnLand);
    }

    @Override
    default double getOccupiedDeceleration() {
        return get(LanternKeys.OCCUPIED_DECELERATION).orElse(DEFAULT_OCCUPIED_DECELERATION);
    }

    @Override
    default void setOccupiedDeceleration(double occupiedDeceleration) {
        offer(LanternKeys.OCCUPIED_DECELERATION, occupiedDeceleration);
    }

    @Override
    default double getUnoccupiedDeceleration() {
        return get(LanternKeys.UNOCCUPIED_DECELERATION).orElse(DEFAULT_UNOCCUPIED_DECELERATION);
    }

    @Override
    default void setUnoccupiedDeceleration(double unoccupiedDeceleration) {
        offer(LanternKeys.UNOCCUPIED_DECELERATION, unoccupiedDeceleration);
    }
}
