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

import org.lanternpowered.server.data.element.ElementListener;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.Value;

import java.util.ArrayList;

final class SynchronizedElementProcessor<V extends Value<E>, E> extends SimpleElementProcessor<V, E> {

    SynchronizedElementProcessor(Key<? extends V> key,
            ElementProcessorBuilder.ApplicablePredicate<E> applicableTester,
            ElementProcessorBuilder.RemoveFunction<E> removeHandler,
            ElementProcessorBuilder.FastRemoveFunction<E> fastRemoveHandler,
            ElementProcessorBuilder.ValueOfferFunction<V, E> valueOfferHandler,
            ElementProcessorBuilder.FastValueOfferFunction<V, E> fastValueOfferHandler,
            ElementProcessorBuilder.OfferFunction<E> offerHandler,
            ElementProcessorBuilder.FastOfferFunction<E> fastOfferHandler,
            ElementProcessorBuilder.ValueRetrieveFunction<V, E> valueRetrieveHandler,
            ElementProcessorBuilder.RetrieveFunction<E> retrieveHandler,
            ElementProcessorBuilder.ValueBuilderFunction<V, E> valueBuilder) {
        super(key, applicableTester, removeHandler, fastRemoveHandler, valueOfferHandler, fastValueOfferHandler, offerHandler, fastOfferHandler,
                valueRetrieveHandler, retrieveHandler, valueBuilder);
    }

    @Override
    public synchronized E set(E element) {
        return super.set(element);
    }

    @Override
    public synchronized E get() {
        return super.get();
    }

    @Override
    public synchronized void addListener(ElementListener<E> listener) {
        super.addListener(listener);
    }

    @Override
    public synchronized SimpleElementProcessor<V, E> copy() {
        final SimpleElementProcessor<V, E> copy = new SimpleElementProcessor<>(getKey(),
                this.applicableTester,
                this.removeHandler,
                this.fastRemoveHandler,
                this.valueOfferHandler,
                this.fastValueOfferHandler,
                this.offerHandler,
                this.fastOfferHandler,
                this.valueRetrieveHandler,
                this.retrieveHandler,
                this.valueBuilder);
        copy.element = this.element;
        if (this.listeners != null) {
            copy.listeners = new ArrayList<>(this.listeners);
        }
        return copy;
    }
}
