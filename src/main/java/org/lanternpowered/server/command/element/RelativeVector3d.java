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
