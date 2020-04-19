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
package org.lanternpowered.server.util.rotation;

public final class RotationHelper {

    /**
     * Wraps the rotation between 0 and 360 degrees.
     *
     * @param rotation The rotation
     * @return The wrapped rotation
     */
    public static double wrapDegRotation(double rotation) {
        while (rotation < 0) {
            rotation += 360.0;
        }
        while (rotation >= 360.0) {
            rotation -= 360.0;
        }
        return rotation;
    }
}
