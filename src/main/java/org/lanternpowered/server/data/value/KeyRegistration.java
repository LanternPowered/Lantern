/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.server.data.value;

import org.lanternpowered.server.data.value.processor.ValueProcessor;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;

import java.util.List;
import java.util.function.Consumer;

public interface KeyRegistration<V extends BaseValue<E>, E> {

    /**
     * Gets the {@link Key} this registration is applicable for.
     *
     * @return The key
     */
    Key<? extends V> getKey();

    /**
     * Gets the {@link ValueProcessor}s of this registration.
     *
     * @return The value processors
     */
    List<ValueProcessor<V, E>> getValueProcessors();

    /**
     * Adds a {@link ValueProcessor} to this key registration.
     *
     * <p>Depending one the implementation will only one {@link ValueProcessor} be allowed or multiple
     * ones. Mainly for the global registrations will be multiple processors supported and for local
     * key registrations inside {@link IValueContainer}s.</p>
     *
     * @param valueProcessor The value processor
     * @return The key registration for chaining
     */
    KeyRegistration<V, E> addValueProcessor(ValueProcessor<V, E> valueProcessor);

    /**
     * Creates a new {@link ValueProcessor} by creating a {@link ValueProcessor.Builder} and then applying
     * the consumer to it, after all the settings are applied is the build processor attached to the registration.
     *
     * @param builderConsumer The builder consumer
     * @return The key registration for chaining
     */
    KeyRegistration<V, E> applyValueProcessor(Consumer<ValueProcessor.Builder<V, E>> builderConsumer);

    /**
     * Gets a list with all the {@link ElementHolderChangeListener}s.
     *
     * @return The element holder change listeners
     */
    List<ElementHolderChangeListener> getElementChangeListeners();

    /**
     * Adds a {@link ElementHolderChangeListener} that will be notified every time
     * this value changes.
     *
     * @param listener The element holder change listener
     */
    KeyRegistration<V, E> addElementChangeListener(ElementHolderChangeListener listener);
}
