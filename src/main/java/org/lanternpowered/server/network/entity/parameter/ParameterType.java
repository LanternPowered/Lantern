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

public final class ParameterType<T> {

    private final ParameterValueType<T> valueType;
    final byte index;

    ParameterType(int index, ParameterValueType<T> valueType) {
        this.valueType = valueType;
        this.index = (byte) index;
    }

    public ParameterValueType<T> getValueType() {
        return this.valueType;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.index);
    }
}
