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

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;

public final class TorchAABB {

    private static final AABB UP = new AABB(0.4, 0.0, 0.4, 0.6, 0.6, 0.6);
    private static final AABB NORTH = new AABB(0.35, 0.2, 0.7, 0.65, 0.8, 1.0);
    private static final AABB EAST = new AABB(0.0, 0.2, 0.35, 0.3, 0.8, 0.65);
    private static final AABB SOUTH = new AABB(0.35, 0.2, 0.0, 0.65, 0.8, 0.3);
    private static final AABB WEST = new AABB(0.7, 0.2, 0.35, 1.0, 0.8, 0.65);

    public static AABB provider(BlockState blockState) {
        final Direction direction = blockState.get(Keys.DIRECTION).get();
        switch (direction) {
            case UP:
                return UP;
            case NORTH:
                return NORTH;
            case EAST:
                return EAST;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            default:
                throw new IllegalArgumentException();
        }
    }
}
