/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.entity.parameter;

import java.util.Optional;

import javax.annotation.Nullable;

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
