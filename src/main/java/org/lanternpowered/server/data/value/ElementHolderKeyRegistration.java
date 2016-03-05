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
import org.spongepowered.api.data.value.BaseValue;

import java.util.function.Consumer;

public interface ElementHolderKeyRegistration<V extends BaseValue<E>, E> extends KeyRegistration<V, E>, ElementHolder<E> {

    @Override
    ElementHolderKeyRegistration<V, E> addValueProcessor(ValueProcessor<V, E> valueProcessor);

    @Override
    ElementHolderKeyRegistration<V, E> applyValueProcessor(Consumer<ValueProcessor.Builder<V, E>> builderConsumer);

    @Override
    ElementHolderKeyRegistration<V, E> addElementChangeListener(ElementHolderChangeListener listener);

    /**
     * Creates a new {@link ValueProcessor} by creating a {@link ValueProcessor.Builder} and then applying
     * the consumer to it, after all the settings are applied is the build processor attached to the registration.
     *
     * @param builderConsumer The builder consumer
     * @return The key registration for chaining
     */
    ElementHolderKeyRegistration<V, E> applyAttachedValueProcessor(Consumer<ValueProcessor.AttachedElementBuilder<V, E>> builderConsumer);

    /**
     * Uses the default {@link ValueProcessor} for {@link ElementHolder}s but does not allow the
     * elements to be removed.
     *
     * @return The key registration for chaining
     */
    default ElementHolderKeyRegistration<V, E> nonRemovableAttachedValueProcessor() {
        return this.addValueProcessor(ValueProcessor.getNonRemovableDefaultAttachedValueProcessor());
    }

}
