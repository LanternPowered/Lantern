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
package org.lanternpowered.server.data.value;

import org.lanternpowered.server.data.value.processor.ValueProcessor;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.BoundedValue;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.mutable.Value;

import javax.annotation.Nullable;

public interface IValueContainer<C extends ValueContainer<C>> extends ValueContainer<C> {

    /**
     * Gets the {@link ElementHolder} of the specified {@link Key}.
     *
     * @param key The key
     * @param <E> The element type
     * @return The element holder
     */
    @Nullable
    <E> ElementHolder<E> getElementHolder(Key<? extends BaseValue<E>> key);

    @Nullable
    <V extends BaseValue<E>, E> KeyRegistration<V, E> getKeyRegistration(Key<? extends BaseValue<E>> key);

    /**
     * Registers a {@link Key} with a specific default value to this {@link IValueContainer}. This
     * must be done for all the {@link Key}s that will use a {@link ValueProcessor}, this is
     * usually for default {@link Key}s.
     *
     * Using a {@code null} as default value will be treated as if the value
     * isn't present, but may be attached in the future.
     *
     * @param key the key to register
     * @param defaultValue the default value, may be null
     * @param <V> the value type
     * @param <E> the element type
     */
    <V extends BaseValue<E>, E> ElementHolderKeyRegistration<V, E> registerKey(Key<? extends V> key, @Nullable E defaultValue);

    <V extends BaseValue<E>, E> ElementHolderKeyRegistration<V, E> registerKey(Key<? extends V> key);

    // Bounded value register methods

    <V extends BoundedValue<E>, E extends Comparable<E>> ElementHolderKeyRegistration<V, E> registerKey(Key<? extends V> key,
            E defaultValue, E minimum, E maximum);

    <V extends BoundedValue<E>, E extends Comparable<E>> ElementHolderKeyRegistration<V, E> registerKey(Key<? extends V> key,
            E defaultValue, Key<? extends BaseValue<E>> minimum, Key<? extends BaseValue<E>> maximum);

    <V extends BoundedValue<E>, E extends Comparable<E>> ElementHolderKeyRegistration<V, E> registerKey(Key<? extends V> key,
            E defaultValue, E minimum, Key<? extends BaseValue<E>> maximum);

    <V extends BoundedValue<E>, E extends Comparable<E>> ElementHolderKeyRegistration<V, E> registerKey(Key<? extends V> key,
            E defaultValue, Key<? extends BaseValue<E>> minimum, E maximum);

    // Keys that don't use a direct internal element

    /**
     * Registers a {@link Key} without a {@link Value} attached to it. This means that there
     * won't be any data attached to the {@link Key}, but it will use a {@link ValueProcessor} to
     * retrieve the data depending on other {@link Key}s.
     *
     * <p>For example: {@link Keys#BODY_ROTATIONS} which will use a {@link ValueProcessor}
     * to retrieve all the body parts data from the {@link IValueContainer} to build the {@link Value}.</p>
     *
     * @param key the key to register
     * @param <V> the value type
     * @param <E> the element type
     */
    <V extends BaseValue<E>, E> KeyRegistration<V, E> registerProcessorKey(Key<? extends V> key);

    /**
     * All the {@link Key}s and {@link ElementHolderChangeListener}s should be registered inside this method.
     */
    default void registerKeys() {
    }

}
