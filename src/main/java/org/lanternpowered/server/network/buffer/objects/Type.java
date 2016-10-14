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
package org.lanternpowered.server.network.buffer.objects;

import com.google.common.reflect.TypeToken;

public final class Type<V> {

    /**
     * Tries to create a new {@link Type} with the specified {@link TypeToken} as value type.
     *
     * @param typeToken The value type token
     * @param <V> The value type
     * @return The type
     */
    public static <V> Type<V> create(TypeToken<V> typeToken, ValueSerializer<V> serializer) {
        return new Type<>(typeToken, serializer);
    }

    /**
     * Tries to create a new {@link Type<V>} with the specified class.
     *
     * @param clazz The value class
     * @param <V> The value type
     * @return the type
     */
    public static <V> Type<V> create(Class<V> clazz, ValueSerializer<V> serializer) {
        return new Type<>(TypeToken.of(clazz), serializer);
    }

    private final TypeToken<V> valueType;
    private final ValueSerializer<V> serializer;

    private Type(TypeToken<V> valueType, ValueSerializer<V> serializer) {
        this.serializer = serializer;
        this.valueType = valueType;
    }

    /**
     * Gets the value type of this type.
     *
     * @return the type class
     */
    public TypeToken<V> getValueType() {
        return this.valueType;
    }

    /**
     * Gets the value serializer.
     *
     * @return The value serializer
     */
    public ValueSerializer<V> getSerializer() {
        return this.serializer;
    }

}
