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
import static com.google.common.base.Preconditions.checkState;

public final class LanternAttributeModifierBuilder {

    private Double value;
    private LanternOperation operation;

    /**
     * How can you check whether the value scales between the min and max value
     * if there is no way to access the attribute?
     */
    public LanternAttributeModifierBuilder value(double value) throws IllegalArgumentException {
        this.value = value;
        return this;
    }

    public LanternAttributeModifierBuilder operation(LanternOperation operation) {
        this.operation = checkNotNull(operation, "operation");
        return this;
    }

    public LanternAttributeModifier build() {
        checkState(this.value != null, "value is not set");
        checkState(this.operation != null, "operation is not set");
        return new LanternAttributeModifier(this.operation, this.value);
    }

    public LanternAttributeModifierBuilder reset() {
        this.value = null;
        this.operation = null;
        return this;
    }
}
