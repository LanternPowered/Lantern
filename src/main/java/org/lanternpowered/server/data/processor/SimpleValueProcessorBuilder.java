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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.data.value.LanternValueFactory;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

@SuppressWarnings("unchecked")
final class SimpleValueProcessorBuilder<V extends BaseValue<E>, E> implements ValueProcessorBuilder<V, E> {

    private static final ValueBuilderFunction<BaseValue<Object>, Object> DEFAULT_VALUE_BUILDER =
            (key, valueContainer, object) -> (Value) LanternValueFactory.get().createValueForKey((Key) key, object);

    final Key<? extends V> key;

    private ApplicablePredicate<V, E> applicableTester;
    private RemoveFunction<V, E> removeHandler;
    private ValueOfferFunction<V, E> valueOfferHandler;
    private OfferFunction<V, E> offerHandler;
    private ValueRetrieveFunction<V, E> valueRetrieveHandler;
    private RetrieveFunction<V, E> retrieveHandler;
    private ValueBuilderFunction<V, E> valueBuilder;

    SimpleValueProcessorBuilder(Key<? extends V> key) {
        this.key = key;
    }

    @Override
    public ValueProcessorBuilder<V, E> applicableTester(ApplicablePredicate<V, E> tester) {
        checkNotNull(tester, "tester");
        this.applicableTester = tester;
        return this;
    }

    @Override
    public ValueProcessorBuilder<V, E> removeHandler(RemoveFunction<V, E> handler) {
        checkNotNull(handler, "handler");
        this.removeHandler = handler;
        return this;
    }

    @Override
    public ValueProcessorBuilder<V, E> valueOfferHandler(ValueOfferFunction<V, E> handler) {
        checkNotNull(handler, "handler");
        this.valueOfferHandler = handler;
        return this;
    }

    @Override
    public ValueProcessorBuilder<V, E> offerHandler(OfferFunction<V, E> handler) {
        checkNotNull(handler, "handler");
        this.offerHandler = handler;
        return this;
    }

    @Override
    public ValueProcessorBuilder<V, E> retrieveHandler(RetrieveFunction<V, E> handler) {
        checkNotNull(handler, "handler");
        this.retrieveHandler = handler;
        return this;
    }

    @Override
    public ValueProcessorBuilder<V, E> valueRetrieveHandler(ValueRetrieveFunction<V, E> handler) {
        checkNotNull(handler, "handler");
        this.valueRetrieveHandler = handler;
        return this;
    }

    @Override
    public ValueProcessorBuilder<V, E> valueBuilder(ValueBuilderFunction<V, E> builder) {
        checkNotNull(builder, "builder");
        this.valueBuilder = builder;
        return this;
    }

    @Override
    public ValueProcessor<V, E> build() {
        checkState(this.offerHandler != null || this.valueOfferHandler != null, "The offer handler must be set.");
        checkState(this.retrieveHandler != null || this.valueRetrieveHandler != null, "The retrieve handler must be set.");

        final SimpleValueProcessor<V, E> valueProcessor = new SimpleValueProcessor(this.key,
                this.applicableTester == null ? (k, c) -> true : this.applicableTester,
                this.removeHandler == null ? ValueProcessorHandlers.Remove.failAlways() : this.removeHandler,
                this.valueOfferHandler,
                this.offerHandler,
                this.valueRetrieveHandler,
                this.retrieveHandler,
                this.valueBuilder == null ? DEFAULT_VALUE_BUILDER : this.valueBuilder);
        if (this.offerHandler == null) {
            valueProcessor.offerHandler = (valueContainer, key, element) -> valueProcessor.valueOfferHandler
                    .offer(valueContainer, valueProcessor.valueBuilder.get(valueContainer, key, element));
        } else if (this.valueOfferHandler == null) {
            valueProcessor.valueOfferHandler = (valueContainer, value) -> valueProcessor.offerHandler
                    .offer(valueContainer, (Key) value.getKey(), value.get());
        }
        if (this.retrieveHandler == null) {
            valueProcessor.retrieveHandler = (key, valueContainer) -> valueProcessor.valueRetrieveHandler
                    .get(key, valueContainer).flatMap(value -> Optional.of(value.get()));
        } else if (this.valueRetrieveHandler == null) {
            valueProcessor.valueRetrieveHandler = (valueContainer, key) -> valueProcessor.retrieveHandler.get(valueContainer, key)
                    .flatMap(element -> Optional.of(valueProcessor.valueBuilder.get(valueContainer, key, element)));
        }
        return valueProcessor;
    }

    @Override
    public ValueProcessorBuilder<V, E> from(ValueProcessor<V, E> value) {
        checkNotNull(value, "value");
        if (value instanceof SimpleValueProcessor) {
            final SimpleValueProcessor<V, E> valueProcessor = (SimpleValueProcessor<V, E>) value;
            this.removeHandler = valueProcessor.removeHandler;
            this.valueOfferHandler = valueProcessor.valueOfferHandler;
            this.valueRetrieveHandler = valueProcessor.valueRetrieveHandler;
            this.retrieveHandler = valueProcessor.retrieveHandler;
            this.offerHandler = valueProcessor.offerHandler;
            this.applicableTester = valueProcessor.applicableTester;
            this.valueBuilder = valueProcessor.valueBuilder;
        } else {
            throw new IllegalStateException("Can only use from on SimpleValueProcessors, these are built through builders.");
        }
        return this;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public ValueProcessorBuilder<V, E> reset() {
        this.valueBuilder = null;
        this.valueRetrieveHandler = null;
        this.retrieveHandler = null;
        this.offerHandler = null;
        this.valueOfferHandler = null;
        this.removeHandler = null;
        this.applicableTester = null;
        return this;
    }
}
