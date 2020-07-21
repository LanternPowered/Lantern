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
package org.lanternpowered.server.network.vanilla.packet.codec.play;

import static com.google.common.base.Preconditions.checkArgument;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.spongepowered.api.util.Direction;
import org.spongepowered.math.GenericMath;

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
