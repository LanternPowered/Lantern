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

public final class RelativeDouble {

    public static final RelativeDouble ZERO_RELATIVE = new RelativeDouble(0, true);
    public static final RelativeDouble ZERO_ABSOLUTE = new RelativeDouble(0, false);

    private final double value;
    private final boolean relative;

    public RelativeDouble(double value, boolean relative) {
        this.relative = relative;
        this.value = value;
    }

    /**
     * Gets the value.
     *
     * @return The value
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Whether the value is relative.
     *
     * @return Is relative
     */
    public boolean isRelative() {
        return this.relative;
    }

    /**
     * Applies this {@link RelativeDouble} to the value.
     *
     * @param value The value
     * @return The result value
     */
    public double applyToValue(double value) {
        return this.relative ? value + this.value : this.value;
    }
}
