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
package org.lanternpowered.server.data;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.Value;

import java.util.Optional;

@SuppressWarnings("unchecked")
public interface IValueHolder {

    /**
     * Gets a raw (newly) created {@link Value.Mutable} for the given {@link Key}. This
     * bypasses possible caching mechanics (see {@link IImmutableValueHolder}).
     *
     * @param key The key to get the value for
     * @param <E> The element type
     * @param <V> The value type
     * @return The value, if present
     */
    <E, V extends Value<E>> Optional<V> getRawMutableValueFor(Key<V> key);

    /**
     * Gets a raw (newly) created {@link Value.Immutable} instance. This
     * bypasses possible caching mechanics (see {@link IImmutableValueHolder}).
     *
     * @param key The key to get the value for
     * @param <E> The element type
     * @param <V> The value type
     * @return The value, if present
     */
    default <E, V extends Value<E>> Optional<V> getRawImmutableValueFor(Key<V> key) {
        return getRawMutableValueFor(key).map(value -> (V) value.asImmutable());
    }

    /**
     * Gets a {@link Value.Mutable} instance. This bypasses
     * possible caching mechanics (see {@link IImmutableValueHolder}).
     *
     * @param key The key to get the value for
     * @param <E> The element type
     * @param <V> The value type
     * @return The value, if present
     */
    default <E, V extends Value<E>> Optional<V> getValueFor(Key<V> key) {
        return getRawMutableValueFor(key);
    }

    @SuppressWarnings("unchecked")
    default <E, V extends Value<E>> V tryGetValueFor(Key<V> key) {
        return getValueFor(key).orElseThrow(() -> new IllegalArgumentException("The key " + key + " isn't present!"));
    }
}
