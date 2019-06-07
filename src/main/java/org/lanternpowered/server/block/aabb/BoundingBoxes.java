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
package org.lanternpowered.server.block.aabb;

import org.lanternpowered.server.block.entity.vanilla.LanternChest;
import org.lanternpowered.server.data.type.LanternRailDirection;
import org.lanternpowered.server.data.type.LanternSlabPortion;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;
import org.spongepowered.math.vector.Vector3d;

public class BoundingBoxes {

    public static final AABB DEFAULT = new AABB(Vector3d.ZERO, Vector3d.ONE);

    public static final AABB NULL = null;

    private final static class Farmland {

        private static final AABB DEFAULT = new AABB(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);
    }

    public static AABB farmland() {
        return Farmland.DEFAULT;
    }

    private final static class Carpet {

        private static final AABB DEFAULT = new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);
    }

    public static AABB carpet() {
        return Carpet.DEFAULT;
    }

    private final static class PressurePlate {

        private static final AABB PRESSED = new AABB(0.0625, 0.0, 0.0625, 0.9375, 0.03125, 0.9375);
        private static final AABB UNPRESSED = new AABB(0.0625, 0.0, 0.0625, 0.9375, 0.0625, 0.9375);
        private static final AABB PRESSURE = new AABB(0.125, 0.0D, 0.125, 0.875, 0.25, 0.875);
    }

    public static AABB pressurePlate(BlockState blockState) {
        return blockState.get(Keys.POWERED).orElse(false) || blockState.get(Keys.POWER).orElse(0) > 0 ?
                PressurePlate.PRESSED : PressurePlate.UNPRESSED;
    }

    /**
     * Gets the {@link AABB} that is used for pressure detection for entities.
     *
     * @return The pressure detection bounding box
     */
    public static AABB pressurePlatePressure() {
        return PressurePlate.PRESSURE;
    }

    private final static class Torch {

        private static final AABB UP = new AABB(0.4, 0.0, 0.4, 0.6, 0.6, 0.6);
        private static final AABB NORTH = new AABB(0.35, 0.2, 0.7, 0.65, 0.8, 1.0);
        private static final AABB EAST = new AABB(0.0, 0.2, 0.35, 0.3, 0.8, 0.65);
        private static final AABB SOUTH = new AABB(0.35, 0.2, 0.0, 0.65, 0.8, 0.3);
        private static final AABB WEST = new AABB(0.7, 0.2, 0.35, 1.0, 0.8, 0.65);
    }

    public static AABB torch() {
        return Torch.UP;
    }


    public static AABB wallTorch(BlockState blockState) {
        final Direction direction = blockState.get(Keys.DIRECTION).get();
        switch (direction) {
            case NORTH:
                return Torch.NORTH;
            case EAST:
                return Torch.EAST;
            case SOUTH:
                return Torch.SOUTH;
            case WEST:
                return Torch.WEST;
            default:
                throw new IllegalArgumentException();
        }
    }

    private final static class Rail {

        private static final AABB FLAT = new AABB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
    }

    public static AABB rail(BlockState blockState) {
        final LanternRailDirection direction = (LanternRailDirection) blockState.get(Keys.RAIL_DIRECTION).get();
        switch (direction) {
            case ASCENDING_EAST:
            case ASCENDING_WEST:
            case ASCENDING_NORTH:
            case ASCENDING_SOUTH:
                return DEFAULT;
            default:
                return Rail.FLAT;
        }
    }

    private final static class Slab {

        private static final AABB TOP = new AABB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0);
        private static final AABB BOTTOM = new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
    }

    public static AABB slab(BlockState blockState) {
        final LanternSlabPortion portionType = (LanternSlabPortion) blockState.get(Keys.SLAB_PORTION).get();
        switch (portionType) {
            case BOTTOM:
                return Slab.BOTTOM;
            case TOP:
                return Slab.TOP;
            default:
                return DEFAULT;
        }
    }

    private final static class Bush {

        private static final AABB DEFAULT = new AABB(0.3, 0.0, 0.3, 0.7, 0.6, 0.7);
    }

    public static AABB bush() {
        return Bush.DEFAULT;
    }

    private final static class Chest {

        private static final AABB DEFAULT = new AABB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);

        private static final AABB CONNECTED_NORTH = new AABB(0.0625, 0.0, 0.0, 0.9375, 0.875, 0.9375);
        private static final AABB CONNECTED_EAST = new AABB(0.0625, 0.0, 0.0625, 1.0, 0.875, 0.9375);
        private static final AABB CONNECTED_SOUTH = new AABB(0.0625, 0.0D, 0.0625, 0.9375, 0.875, 1.0);
        private static final AABB CONNECTED_WEST = new AABB(0.0, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
    }

    public static AABB chest() {
        return Chest.DEFAULT;
    }

    public static AABB doubleChest(BlockState blockState) {
        switch (LanternChest.getConnectedDirection(blockState)) {
            case SOUTH:
                return Chest.CONNECTED_SOUTH;
            case NORTH:
                return Chest.CONNECTED_NORTH;
            case WEST:
                return Chest.CONNECTED_WEST;
            case EAST:
                return Chest.CONNECTED_EAST;
            case NONE:
                return Chest.DEFAULT;
            default:
                throw new IllegalStateException();
        }
    }

    private BoundingBoxes() {
    }
}
