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
package org.lanternpowered.server.command.element;

import static java.util.Objects.requireNonNull;

import org.spongepowered.math.vector.Vector3d;

public final class RelativeVector3d {

    private final RelativeDouble x;
    private final RelativeDouble y;
    private final RelativeDouble z;

    public RelativeVector3d(RelativeDouble x, RelativeDouble y, RelativeDouble z) {
        this.x = requireNonNull(x, "x");
        this.y = requireNonNull(y, "y");
        this.z = requireNonNull(z, "z");
    }

    /**
     * Gets the x component.
     *
     * @return The x component
     */
    public RelativeDouble getX() {
        return this.x;
    }

    /**
     * Gets the y component.
     *
     * @return The y component
     */
    public RelativeDouble getY() {
        return this.y;
    }

    /**
     * Gets the z component.
     *
     * @return The z component
     */
    public RelativeDouble getZ() {
        return this.z;
    }

    /**
     * Applies this {@link RelativeDouble} to the {@link Vector3d}.
     *
     * @param value The value
     * @return The result value
     */
    public Vector3d applyToValue(Vector3d value) {
        double x = this.x.getValue();
        double y = this.y.getValue();
        double z = this.z.getValue();

        if (this.x.isRelative()) {
            x += value.getX();
        }
        if (this.y.isRelative()) {
            y += value.getY();
        }
        if (this.z.isRelative()) {
            z += value.getZ();
        }

        return new Vector3d(x, y, z);
    }
}
