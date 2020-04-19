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
package org.lanternpowered.server.network.entity.parameter;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

public final class ParameterTypeCollection {

    private final List<ParameterType<?>> parameterTypes;

    public ParameterTypeCollection() {
        this(new ArrayList<>());
    }

    private ParameterTypeCollection(List<ParameterType<?>> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    /**
     * Copies this {@link ParameterTypeCollection}.
     */
    public ParameterTypeCollection copy() {
        return new ParameterTypeCollection(new ArrayList<>(this.parameterTypes));
    }

    /**
     * Creates a new {@link ParameterType}.
     *
     * @param valueType The parameter value type
     * @param <T> The value type
     * @return The parameter type
     */
    public <T> ParameterType<T> newParameterType(ParameterValueType<T> valueType) {
        final ParameterType<T> parameterType = new ParameterType<>(
                this.parameterTypes.size(), checkNotNull(valueType, "valueType"));
        this.parameterTypes.add(parameterType);
        return parameterType;
    }
}
