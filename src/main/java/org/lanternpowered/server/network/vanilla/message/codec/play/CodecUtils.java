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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import static com.google.common.base.Preconditions.checkArgument;

import com.flowpowered.math.GenericMath;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.spongepowered.api.util.Direction;

public final class CodecUtils {

    private static final Direction[] directions = {
            Direction.DOWN,
            Direction.UP,
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST,
    };
    private static final Object2IntMap<Direction> directionToValue;

    static {
        final Object2IntMap<Direction> map = new Object2IntOpenHashMap<>();
        map.defaultReturnValue(-1);
        for (int i = 0; i < directions.length; i++) {
            map.put(directions[i], i);
        }
        directionToValue = Object2IntMaps.unmodifiable(map);
    }

    /**
     * Decodes the integer into a {@link Direction}.
     *
     * @param dir The direction value
     * @return The direction
     */
    public static Direction decodeDirection(int dir) {
        checkArgument(dir >= 0 && dir < directions.length, "Unknown direction value: %s", dir);
        return directions[dir];
    }

    /**
     * Decodes the {@link Direction} value into a integer.
     *
     * @param dir The direction
     * @return The integer direction
     */
    public static int encodeDirection(Direction dir) {
        final int value = directionToValue.getInt(dir);
        checkArgument(value != -1, "Unsupported direction: " + dir);
        return value;
    }

    /**
     * Wraps the double angle into a byte.
     * 
     * @param angle The angle
     * @return The byte value
     */
    public static byte wrapAngle(double angle) {
        while (angle >= 180.0) {
            angle -= 360.0;
        }
        while (angle < -180.0) {
            angle += 360.0;
        }
        return (byte) GenericMath.floor(angle / 360.0 * 256.0);
    }

    private CodecUtils() {
    }
}
