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
package org.lanternpowered.server.inventory.client;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.reflect.TypeToken;

public final class ContainerProperty<T> {

    private final TypeToken<T> valueType;

    /**
     * Constructs a new {@link ContainerProperty} with
     * the given value type.
     *
     * @param valueType The value type
     */
    public ContainerProperty(TypeToken<T> valueType) {
        this.valueType = checkNotNull(valueType, "valueType");
    }

    /**
     * Gets the value type of this {@link ContainerProperty}.
     *
     * @return The value type
     */
    public TypeToken<T> getValueType() {
        return this.valueType;
    }
}
