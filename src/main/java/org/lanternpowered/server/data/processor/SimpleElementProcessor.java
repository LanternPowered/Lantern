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

import org.lanternpowered.server.data.IValueContainer;
import org.lanternpowered.server.data.element.ElementKeyRegistration;
import org.lanternpowered.server.data.element.ElementListener;
import org.lanternpowered.server.util.copy.Copyable;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

class SimpleElementProcessor<V extends Value<E>, E> extends AbstractProcessor<V, E>
        implements ElementProcessor<V, E>, Copyable<SimpleElementProcessor<V, E>>, ElementKeyRegistration<V, E> {

    ElementProcessorBuilder.RetrieveFunction<E> retrieveHandler;
    ElementProcessorBuilder.ValueRetrieveFunction<V, E> valueRetrieveHandler;
    ElementProcessorBuilder.OfferFunction<E> offerHandler;
    ElementProcessorBuilder.FastOfferFunction<E> fastOfferHandler;
    ElementProcessorBuilder.ValueOfferFunction<V, E> valueOfferHandler;
    ElementProcessorBuilder.FastValueOfferFunction<V, E> fastValueOfferHandler;
    ElementProcessorBuilder.RemoveFunction<E> removeHandler;
    ElementProcessorBuilder.FastRemoveFunction<E> fastRemoveHandler;
    ElementProcessorBuilder.ApplicablePredicate<E> applicableTester;
    ElementProcessorBuilder.ValueBuilderFunction<V, E> valueBuilder;

    @Nullable E element;
    @Nullable List<ElementListener<E>> listeners;

    SimpleElementProcessor(Key<? extends V> key,
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
        super(key);
        this.retrieveHandler = retrieveHandler;
        this.valueRetrieveHandler = valueRetrieveHandler;
        this.offerHandler = offerHandler;
        this.fastOfferHandler = fastOfferHandler;
        this.valueOfferHandler = valueOfferHandler;
        this.fastValueOfferHandler = fastValueOfferHandler;
        this.removeHandler = removeHandler;
        this.fastRemoveHandler = fastRemoveHandler;
        this.applicableTester = applicableTester;
        this.valueBuilder = valueBuilder;
    }

    @Override
    public SimpleElementProcessor<V, E> copy() {
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

    @Override
    public E set(E element) {
        final E oldElement = this.element;
        this.element = element;
        if (this.listeners != null && !Objects.equals(oldElement, element)) {
            this.listeners.forEach(listener -> listener.accept(oldElement, element));
        }
        return oldElement;
    }

    @Override
    public E get() {
        return this.element;
    }

    @Override
    public void addListener(ElementListener<E> listener) {
        checkNotNull(listener, "listener");
        if (this.listeners == null) {
            this.listeners = new ArrayList<>();
        }
        this.listeners.add(listener);
    }

    @Override
    public boolean isApplicableTo(IValueContainer<?> valueContainer) {
        return this.applicableTester.test(valueContainer, this);
    }

    @Override
    public DataTransactionResult removeFrom(IValueContainer<?> valueContainer) {
        return isApplicableTo(valueContainer) ? this.removeHandler.remove(valueContainer, this) : DataTransactionResult.failNoData();
    }

    @Override
    public boolean removeFastFrom(IValueContainer<?> valueContainer) {
        return isApplicableTo(valueContainer) && this.fastRemoveHandler.remove(valueContainer, this);
    }

    @Override
    public DataTransactionResult offerTo(IValueContainer<?> valueContainer, E element) {
        return isApplicableTo(valueContainer) ? this.offerHandler.offer(valueContainer, this, element) : DataTransactionResult.failNoData();
    }

    @Override
    public boolean offerFastTo(IValueContainer<?> valueContainer, E element) {
        return isApplicableTo(valueContainer) && this.fastOfferHandler.offer(valueContainer, this, element);
    }

    @Override
    public DataTransactionResult offerTo(IValueContainer<?> valueContainer, V value) {
        return isApplicableTo(valueContainer) ? this.valueOfferHandler.offer(valueContainer, this, value) : DataTransactionResult.failNoData();
    }

    @Override
    public boolean offerFastTo(IValueContainer<?> valueContainer, V value) {
        return isApplicableTo(valueContainer) && this.fastValueOfferHandler.offer(valueContainer, this, value);
    }

    @Override
    public Optional<E> getFrom(IValueContainer<?> valueContainer) {
        return isApplicableTo(valueContainer) ? this.retrieveHandler.get(valueContainer, this) : Optional.empty();
    }

    @Override
    public Optional<V> getValueFrom(IValueContainer<?> valueContainer) {
        return isApplicableTo(valueContainer) ? this.valueRetrieveHandler.get(valueContainer, this) : Optional.empty();
    }

    @Override
    public V createValueFor(IValueContainer<?> valueContainer, E element) {
        return this.valueBuilder.get(valueContainer, this, element);
    }
}
