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
package org.lanternpowered.server.network.vanilla.command.argument;

import org.checkerframework.checker.nullness.qual.Nullable;

public class DoubleArgument extends Argument {

    @Nullable private final Double min;
    @Nullable private final Double max;

    public DoubleArgument(@Nullable Double min, @Nullable Double max) {
        this.min = min;
        this.max = max;
    }

    @Nullable
    public Double getMin() {
        return this.min;
    }

    @Nullable
    public Double getMax() {
        return this.max;
    }
}
