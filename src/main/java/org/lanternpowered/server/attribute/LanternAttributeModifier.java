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
package org.lanternpowered.server.attribute;

import static com.google.common.base.Preconditions.checkNotNull;

public class LanternAttributeModifier {

    private final LanternOperation operation;
    private final double value;

    public LanternAttributeModifier(LanternOperation operation, double value) {
        this.operation = checkNotNull(operation, "operation");
        this.value = value;
    }

    public LanternOperation getOperation() {
        return this.operation;
    }

    public double getValue() {
        return this.value;
    }
}
