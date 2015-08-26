package org.lanternpowered.server.util;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;

public final class VecHelper {

    public static boolean inBounds(int x, int y, Vector2i min, Vector2i max) {
        return x >= min.getX() && x <= max.getX() && y >= min.getY() && y <= max.getY();
    }

    public static boolean inBounds(int x, int y, int z, Vector3i min, Vector3i max) {
        return x >= min.getX() && x <= max.getX() && y >= min.getY() && y <= max.getY() && z >= min.getZ() && z <= max.getZ();
    }

    public static boolean inBounds(double x, double y, double z, Vector3i min, Vector3i max) {
        return x >= min.getX() && x <= max.getX() && y >= min.getY() && y <= max.getY() && z >= min.getZ() && z <= max.getZ();
    }
}
