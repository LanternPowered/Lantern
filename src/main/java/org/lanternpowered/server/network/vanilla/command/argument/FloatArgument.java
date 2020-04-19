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

public class FloatArgument extends Argument {

    @Nullable private final Float min;
    @Nullable private final Float max;

    public FloatArgument(@Nullable Float min, @Nullable Float max) {
        this.min = min;
        this.max = max;
    }

    @Nullable
    public Float getMin() {
        return this.min;
    }

    @Nullable
    public Float getMax() {
        return this.max;
    }
}
