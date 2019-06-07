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

import org.lanternpowered.server.data.IValueContainer;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.Value;

import java.util.Optional;

class SimpleValueProcessor<V extends Value<E>, E> extends AbstractProcessor<V, E> implements ValueProcessor<V, E> {

    ValueProcessorBuilder.ApplicablePredicate<V, E> applicableTester;
    ValueProcessorBuilder.RemoveFunction<V, E> removeHandler;
    ValueProcessorBuilder.ValueOfferFunction<V, E> valueOfferHandler;
    ValueProcessorBuilder.OfferFunction<V, E> offerHandler;
    ValueProcessorBuilder.ValueRetrieveFunction<V, E> valueRetrieveHandler;
    ValueProcessorBuilder.RetrieveFunction<V, E> retrieveHandler;
    ValueProcessorBuilder.ValueBuilderFunction<V, E> valueBuilder;

    SimpleValueProcessor(Key<? extends V> key,
            ValueProcessorBuilder.ApplicablePredicate<V, E> applicableTester,
            ValueProcessorBuilder.RemoveFunction<V, E> removeHandler,
            ValueProcessorBuilder.ValueOfferFunction<V, E> valueOfferHandler,
            ValueProcessorBuilder.OfferFunction<V, E> offerHandler,
            ValueProcessorBuilder.ValueRetrieveFunction<V, E> valueRetrieveHandler,
            ValueProcessorBuilder.RetrieveFunction<V, E> retrieveHandler,
            ValueProcessorBuilder.ValueBuilderFunction<V, E> valueBuilder) {
        super(key);
        this.applicableTester = applicableTester;
        this.removeHandler = removeHandler;
        this.valueOfferHandler = valueOfferHandler;
        this.offerHandler = offerHandler;
        this.valueRetrieveHandler = valueRetrieveHandler;
        this.retrieveHandler = retrieveHandler;
        this.valueBuilder = valueBuilder;
    }

    @Override
    public boolean isApplicableTo(IValueContainer<?> valueContainer) {
        return this.applicableTester.test(valueContainer, getKey());
    }

    @Override
    public DataTransactionResult removeFrom(IValueContainer<?> valueContainer) {
        return isApplicableTo(valueContainer) ? this.removeHandler.remove(valueContainer, getKey()) : DataTransactionResult.failNoData();
    }

    @Override
    public DataTransactionResult offerTo(IValueContainer<?> valueContainer, E element) {
        if (!isApplicableTo(valueContainer)) {
            return DataTransactionResult.failNoData();
        }
        return isApplicableTo(valueContainer) ? this.offerHandler.offer(valueContainer, getKey(), element) : DataTransactionResult.failNoData();
    }

    @Override
    public DataTransactionResult offerTo(IValueContainer<?> valueContainer, V value) {
        return isApplicableTo(valueContainer)? this.valueOfferHandler.offer(valueContainer, value) : DataTransactionResult.failNoData();
    }

    @Override
    public Optional<E> getFrom(IValueContainer<?> valueContainer) {
        return isApplicableTo(valueContainer) ? this.retrieveHandler.get(valueContainer, getKey()) : Optional.empty();
    }

    @Override
    public Optional<V> getValueFrom(IValueContainer<?> valueContainer) {
        return isApplicableTo(valueContainer) ? this.valueRetrieveHandler.get(valueContainer, getKey()) : Optional.empty();
    }

    @Override
    public V createValueFor(IValueContainer<?> valueContainer, E element) {
        return this.valueBuilder.get(valueContainer, getKey(), element);
    }
}
