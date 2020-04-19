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

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ParameterList {

    boolean isEmpty();

    <T> void add(ParameterType<T> type, T value);

    default <T> void addOptional(ParameterType<Optional<T>> type, @Nullable T value) {
        add(type, Optional.ofNullable(value));
    }

    default void add(ParameterType<Byte> type, byte value) {
        add(type, (Byte) value);
    }

    default void add(ParameterType<Integer> type, int value) {
        add(type, (Integer) value);
    }

    default void add(ParameterType<Float> type, float value) {
        add(type, (Float) value);
    }

    default void add(ParameterType<Boolean> type, boolean value) {
        add(type, (Boolean) value);
    }
}
