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
package org.lanternpowered.server.data.processor;

import org.lanternpowered.server.data.KeyRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;

import java.util.List;
import java.util.function.Consumer;

public interface ValueProcessorKeyRegistration<V extends BaseValue<E>, E> extends KeyRegistration<V, E> {

    /**
     * Creates a {@link ValueProcessorKeyRegistration} for the
     * specified {@link Key}.
     *
     * @param key The key
     * @param <V> The value type
     * @param <E> The element type
     * @return The processor key registration
     */
    static <V extends BaseValue<E>, E> ValueProcessorKeyRegistration<V, E> create(Key<? extends V> key) {
        return new SimpleProcessorKeyRegistration<>(key);
    }

    /**
     * Adds a {@link Processor}.
     *
     * @param processor The value processor
     */
    ValueProcessorKeyRegistration<V, E> add(ValueProcessor<V, E> processor);

    /**
     * Adds a {@link Processor}.
     *
     * @param consumer The value processor builder consumer
     */
    ValueProcessorKeyRegistration<V, E> add(Consumer<ValueProcessorBuilder<V, E>> consumer);

    /**
     * Gets all the {@link Processor}s.
     *
     * @return The value processors
     */
    List<ValueProcessor<V, E>> getAll();
}
