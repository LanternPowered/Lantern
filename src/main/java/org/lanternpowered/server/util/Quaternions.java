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
package org.lanternpowered.server.util;

import org.spongepowered.math.imaginary.Quaterniond;
import org.spongepowered.math.imaginary.Quaternionf;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3f;

public final class Quaternions {

    /**
     * Creates a new quaternion from the float angles in degrees around the x, y and z axes.
     *
     * @param vector3d The rotation vector
     * @return The quaternion defined by the rotations around the axes
     */
    public static Quaterniond fromAxesAnglesDeg(Vector3d vector3d) {
        return Quaterniond.fromAxesAnglesDeg(vector3d.getX(), vector3d.getY(), vector3d.getZ());
    }

    /**
     * Creates a new quaternion from the float angles in degrees around the x, y and z axes.
     *
     * @param pitch The rotation around x
     * @param yaw The rotation around y
     * @param roll The rotation around z
     * @return The quaternion defined by the rotations around the axes
     */
    public static Quaterniond fromAxesAnglesDeg(double pitch, double yaw, double roll) {
        return Quaterniond.fromAxesAnglesDeg(pitch, yaw, roll);
    }

    /**
     * Creates a new quaternion from the float angles in degrees around the x, y and z axes.
     *
     * @param vector3f The rotation vector
     * @return The quaternion defined by the rotations around the axes
     */
    public static Quaternionf fromAxesAnglesDeg(Vector3f vector3f) {
        return Quaternionf.fromAxesAnglesDeg(vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }

    private Quaternions() {
    }
}
