/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.util;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;

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

    private Quaternions() {
    }
}
