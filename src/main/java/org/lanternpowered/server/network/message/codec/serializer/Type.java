/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.network.message.codec.serializer;

import java.util.concurrent.atomic.AtomicInteger;

public final class Type<V> {

    /**
     * Tries to create a new {@link Type<V>} with the specified class.
     *
     * @param clazz the class
     * @param <V> the clazz type
     * @return the type
     */
    public static <V> Type<V> create(Class<V> clazz) {
        return new Type<>(clazz);
    }

    private static final AtomicInteger indexCounter = new AtomicInteger();

    final int index;
    private final Class<V> valueType;

    private Type(Class<V> valueType) {
        this.index = indexCounter.getAndIncrement();
        this.valueType = valueType;
    }

    /**
     * Gets the value type of this type.
     *
     * @return the type class
     */
    public Class<V> getValueType() {
        return this.valueType;
    }

    @Override
    public int hashCode() {
        return this.index;
    }

    @Override
    public boolean equals(Object o) {
        return !(o == null || o.getClass() != this.getClass()) && ((Type) o).index == this.index;
    }

}
