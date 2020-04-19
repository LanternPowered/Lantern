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

public class IntArgument extends Argument {

    @Nullable private final Integer min;
    @Nullable private final Integer max;

    public IntArgument(@Nullable Integer min, @Nullable Integer max) {
        this.min = min;
        this.max = max;
    }

    @Nullable
    public Integer getMin() {
        return this.min;
    }

    @Nullable
    public Integer getMax() {
        return this.max;
    }
}
