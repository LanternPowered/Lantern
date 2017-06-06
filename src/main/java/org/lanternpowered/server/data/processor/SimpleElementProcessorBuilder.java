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
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeValueKey;

import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.data.value.LanternValueFactory;
import org.lanternpowered.server.data.value.ValueHelper;
import org.lanternpowered.server.util.copy.Copyable;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

@SuppressWarnings("unchecked")
final class SimpleElementProcessorBuilder<V extends BaseValue<E>, E> implements ElementProcessorBuilder<V, E> {

    private static final ValueBuilderFunction<BaseValue<Object>, Object> DEFAULT_VALUE_BUILDER =
            (container, element, object) -> LanternValueFactory.get().createValueForKey(element.getKey(), object);

    /**
     * Creates a new {@link ElementProcessorBuilder}.
     *
     * @param key The key to create the processor for
     * @param <V> The value type
     * @param <E> The element type
     * @return The builder instance
     */
    static <V extends BaseValue<E>, E> ElementProcessorBuilder<V, E> create(Key<? extends V> key) {
        return new SimpleElementProcessorBuilder<>(key);
    }

    static <V extends BaseValue<E>, E> ElementProcessor<V, E> createDefault(Key<? extends V> key) {
        return createCopy(key, DEFAULT);
    }

    static <V extends BaseValue<E>, E> ElementProcessor<V, E> createNonRemovableDefault(Key<? extends V> key) {
        return createCopy(key, NON_REMOVABLE);
    }

    private static <V extends BaseValue<E>, E> ElementProcessor<V, E> createCopy(Key<? extends V> key,
            SimpleElementProcessor processor) {
        return new SimpleElementProcessor(key,
                processor.applicableTester,
                processor.removeHandler,
                processor.fastRemoveHandler,
                processor.valueOfferHandler,
                processor.fastValueOfferHandler,
                processor.offerHandler,
                processor.fastOfferHandler,
                processor.valueRetrieveHandler,
                processor.retrieveHandler,
                processor.valueBuilder);
    }

    private static final SimpleElementProcessor DEFAULT;
    private static final SimpleElementProcessor NON_REMOVABLE;

    private static boolean initialized;

    static {
        final Key<Value<Boolean>> key = makeValueKey(TypeToken.of(Boolean.class), DataQuery.of("Dummy"), "lantern:dummy");

        DEFAULT = (SimpleElementProcessor) new SimpleElementProcessorBuilder(key).build();
        NON_REMOVABLE = (SimpleElementProcessor) new SimpleElementProcessorBuilder(key).failAlwaysRemoveHandler().build();

        initialized = true;
    }

    private RetrieveFunction<E> retrieveHandler;
    private ValueRetrieveFunction<V, E> valueRetrieveHandler;
    private OfferFunction<E> offerHandler;
    private FastOfferFunction<E> fastOfferHandler;
    private ValueOfferFunction<V, E> valueOfferHandler;
    private FastValueOfferFunction<V, E> fastValueOfferHandler;
    private RemoveFunction<E> removeHandler;
    private FastRemoveFunction<E> fastRemoveHandler;
    private ApplicablePredicate<E> applicableTester;
    private ValueBuilderFunction<V, E> valueBuilder;

    private boolean synchronize;
    private final Key<? extends V> key;

    SimpleElementProcessorBuilder(Key<? extends V> key) {
        this.key = key;
    }

    @Override
    public ElementProcessorBuilder<V, E> applicableTester(ApplicablePredicate<E> tester) {
        checkNotNull(tester, "tester");
        this.applicableTester = tester;
        return this;
    }

    @Override
    public ElementProcessorBuilder<V, E> retrieveHandler(RetrieveFunction<E> handler) {
        checkNotNull(handler, "handler");
        this.retrieveHandler = handler;
        return this;
    }

    @Override
    public ElementProcessorBuilder<V, E> valueRetrieveHandler(ValueRetrieveFunction<V, E> handler) {
        checkNotNull(handler, "handler");
        this.valueRetrieveHandler = handler;
        return this;
    }

    @Override
    public ElementProcessorBuilder<V, E> offerHandler(OfferFunction<E> handler) {
        checkNotNull(handler, "handler");
        this.offerHandler = handler;
        return this;
    }

    @Override
    public ElementProcessorBuilder<V, E> fastOfferHandler(FastOfferFunction<E> handler) {
        checkNotNull(handler, "handler");
        this.fastOfferHandler = handler;
        return this;
    }

    @Override
    public ElementProcessorBuilder<V, E> valueOfferHandler(ValueOfferFunction<V, E> handler) {
        checkNotNull(handler, "handler");
        this.valueOfferHandler = handler;
        return this;
    }

    @Override
    public ElementProcessorBuilder<V, E> fastValueOfferHandler(FastValueOfferFunction<V, E> handler) {
        checkNotNull(handler, "handler");
        this.fastValueOfferHandler = handler;
        return this;
    }

    @Override
    public ElementProcessorBuilder<V, E> removeHandler(RemoveFunction<E> handler) {
        checkNotNull(handler, "handler");
        this.removeHandler = handler;
        return this;
    }

    @Override
    public ElementProcessorBuilder<V, E> fastRemoveHandler(FastRemoveFunction<E> handler) {
        checkNotNull(handler, "handler");
        this.fastRemoveHandler = handler;
        return this;
    }

    @Override
    public ElementProcessorBuilder<V, E> valueBuilder(ValueBuilderFunction<V, E> builder) {
        checkNotNull(builder, "builder");
        this.valueBuilder = builder;
        return this;
    }

    @Override
    public ElementProcessorBuilder<V, E> synchronize() {
        this.synchronize = true;
        return this;
    }

    @Override
    public ElementProcessorBuilder<V, E> from(ElementProcessor<V, E> value) {
        checkNotNull(value, "value");
        if (value instanceof SimpleElementProcessor) {
            final SimpleElementProcessor<V, E> valueProcessor = (SimpleElementProcessor<V, E>) value;
            this.removeHandler = valueProcessor.removeHandler;
            this.fastRemoveHandler = valueProcessor.fastRemoveHandler;
            this.valueOfferHandler = valueProcessor.valueOfferHandler;
            this.fastValueOfferHandler = valueProcessor.fastValueOfferHandler;
            this.valueRetrieveHandler = valueProcessor.valueRetrieveHandler;
            this.retrieveHandler = valueProcessor.retrieveHandler;
            this.offerHandler = valueProcessor.offerHandler;
            this.fastOfferHandler = valueProcessor.fastOfferHandler;
            this.applicableTester = valueProcessor.applicableTester;
            this.valueBuilder = valueProcessor.valueBuilder;
            this.synchronize = value instanceof SynchronizedElementProcessor;
        } else {
            throw new IllegalStateException("Can only use from on SimpleElementProcessors, these are built through builders.");
        }
        return this;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public ElementProcessorBuilder<V, E> reset() {
        this.valueBuilder = null;
        this.valueRetrieveHandler = null;
        this.retrieveHandler = null;
        this.offerHandler = null;
        this.fastOfferHandler = null;
        this.valueOfferHandler = null;
        this.fastValueOfferHandler = null;
        this.removeHandler = null;
        this.fastRemoveHandler = null;
        this.applicableTester = null;
        return this;
    }

    @Override
    public ElementProcessor<V, E> build() {
        if (initialized && this.applicableTester == null && this.offerHandler == null && this.valueOfferHandler == null &&
                this.retrieveHandler == null && this.valueRetrieveHandler == null && this.valueBuilder == null) {
            if (this.removeHandler == null) {
                return createCopy(this.key, DEFAULT);
            } else if (this.removeHandler.equals(ElementProcessorHandlers.Remove.failAlways())) {
                return createCopy(this.key, NON_REMOVABLE);
            }
        }
        ValueBuilderFunction<V, E> valueBuilder = this.valueBuilder;
        if (valueBuilder == null) {
            valueBuilder = (ValueBuilderFunction) DEFAULT_VALUE_BUILDER;
        }
        final SimpleElementProcessor<V, E> valueProcessor;
        if (this.synchronize) {
            valueProcessor = new SynchronizedElementProcessor<>(this.key,
                    this.applicableTester,
                    this.removeHandler,
                    this.fastRemoveHandler,
                    this.valueOfferHandler,
                    this.fastValueOfferHandler,
                    this.offerHandler,
                    this.fastOfferHandler,
                    this.valueRetrieveHandler,
                    this.retrieveHandler,
                    valueBuilder);
        } else {
            valueProcessor = new SimpleElementProcessor(this.key,
                    this.applicableTester,
                    this.removeHandler,
                    this.fastRemoveHandler,
                    this.valueOfferHandler,
                    this.fastValueOfferHandler,
                    this.offerHandler,
                    this.fastOfferHandler,
                    this.valueRetrieveHandler,
                    this.retrieveHandler,
                    valueBuilder);
        }
        if (this.applicableTester == null) {
            valueProcessor.applicableTester = (container, element) -> true;
        }
        ///////////////////////
        // Retrieve Handlers //
        ///////////////////////
        if (this.retrieveHandler == null) {
            if (this.valueRetrieveHandler == null) {
                valueProcessor.retrieveHandler = (valueContainer, element) -> Optional.ofNullable(Copyable.copyOrSelf(element.get()));
            } else {
                valueProcessor.retrieveHandler = (valueContainer, element) -> valueProcessor.valueRetrieveHandler
                        .get(valueContainer, element).flatMap(value -> Optional.of(value.get()));
            }
        }
        if (this.valueRetrieveHandler == null) {
            if (this.retrieveHandler == null) {
                valueProcessor.valueRetrieveHandler = (valueContainer, element) -> {
                    if (element == null) {
                        return Optional.empty();
                    }
                    final E e = element.get();
                    return e == null ? Optional.empty() : Optional.of(valueProcessor.valueBuilder.get(
                            valueContainer, element, Copyable.copyOrSelf(e)));
                };
            } else {
                valueProcessor.valueRetrieveHandler = (valueContainer, element) ->
                        valueProcessor.retrieveHandler.get(valueContainer, element).flatMap(
                                value -> Optional.of(valueProcessor.valueBuilder.get(valueContainer, element, value)));
            }
        }
        ////////////////////
        // Offer Handlers //
        ////////////////////
        if (this.fastOfferHandler == null) {
            if (this.offerHandler != null) {
                valueProcessor.fastOfferHandler = (valueContainer, elementHolder, element) ->
                        valueProcessor.offerHandler.offer(valueContainer, elementHolder, element).isSuccessful();
            } else if (this.fastValueOfferHandler != null) {
                valueProcessor.fastOfferHandler = (valueContainer, elementHolder, element) ->
                        valueProcessor.fastValueOfferHandler.offer(valueContainer, elementHolder,
                                valueProcessor.valueBuilder.get(valueContainer, elementHolder, element));
            } else if (this.valueOfferHandler != null) {
                valueProcessor.fastOfferHandler = (valueContainer, elementHolder, element) ->
                        valueProcessor.valueOfferHandler.offer(valueContainer, elementHolder,
                                valueProcessor.valueBuilder.get(valueContainer, elementHolder, element)).isSuccessful();
            } else {
                valueProcessor.fastOfferHandler = (valueContainer, elementHolder, element) -> {
                    elementHolder.set(element);
                    return true;
                };
            }
        }
        if (this.offerHandler == null) {
            if (this.fastOfferHandler != null) {
                valueProcessor.offerHandler = (valueContainer, element, e) -> {
                    final ImmutableValue<E> oldValue = valueProcessor.valueRetrieveHandler
                            .get(valueContainer, element).map(ValueHelper::toImmutable).orElse(null);
                    final ImmutableValue<E> newValue = ValueHelper.toImmutable(valueProcessor.valueBuilder
                            .get(valueContainer, element, e));
                    if (!valueProcessor.fastOfferHandler.offer(valueContainer, element, e)) {
                        return DataTransactionResult.failResult(newValue);
                    }
                    if (oldValue != null) {
                        return DataTransactionResult.successReplaceResult(newValue, oldValue);
                    }
                    return DataTransactionResult.successResult(newValue);
                };
            } else if (this.fastValueOfferHandler != null) {
                valueProcessor.offerHandler = (valueContainer, element, e) -> {
                    final ImmutableValue<E> oldValue = valueProcessor.valueRetrieveHandler
                            .get(valueContainer, element).map(ValueHelper::toImmutable).orElse(null);
                    final V value = valueProcessor.valueBuilder.get(valueContainer, element, e);
                    if (!valueProcessor.fastValueOfferHandler.offer(valueContainer, element, value)) {
                        return DataTransactionResult.failResult(ValueHelper.toImmutable(value));
                    }
                    final ImmutableValue<E> newValue = ValueHelper.toImmutable(value);
                    if (oldValue != null) {
                        return DataTransactionResult.successReplaceResult(newValue, oldValue);
                    }
                    return DataTransactionResult.successResult(newValue);
                };
            } else if (this.valueOfferHandler != null) {
                valueProcessor.offerHandler = (valueContainer, element, e) -> {
                    final V newValue = valueProcessor.valueBuilder.get(valueContainer, element, e);
                    return valueProcessor.valueOfferHandler.offer(valueContainer, element, newValue);
                };
            } else {
                valueProcessor.offerHandler = (valueContainer, elementHolder, element) -> {
                    final ImmutableValue<E> oldValue = valueProcessor.valueRetrieveHandler.get(valueContainer, elementHolder)
                            .map(ValueHelper::toImmutable).orElse(null);
                    final ImmutableValue<E> newValue = ValueHelper.toImmutable(
                            valueProcessor.valueBuilder.get(valueContainer, elementHolder, element));
                    elementHolder.set(element);
                    if (oldValue != null) {
                        return DataTransactionResult.successReplaceResult(newValue, oldValue);
                    }
                    return DataTransactionResult.successResult(newValue);
                };
            }
        }
        if (this.fastValueOfferHandler == null) {
            if (this.fastOfferHandler != null) {
                valueProcessor.fastValueOfferHandler = (valueContainer, elementHolder, value) ->
                        valueProcessor.fastOfferHandler.offer(valueContainer, elementHolder, value.get());
            } else if (this.offerHandler != null) {
                valueProcessor.fastValueOfferHandler = (valueContainer, elementHolder, value) ->
                        valueProcessor.offerHandler.offer(valueContainer, elementHolder, value.get()).isSuccessful();
            } else if (this.valueOfferHandler != null) {
                valueProcessor.fastValueOfferHandler = (valueContainer, elementHolder, value) ->
                        valueProcessor.valueOfferHandler.offer(valueContainer, elementHolder, value).isSuccessful();
            } else {
                valueProcessor.fastValueOfferHandler = (valueContainer, elementHolder, value) -> {
                    elementHolder.set(value.get());
                    return true;
                };
            }
        }
        if (this.valueOfferHandler == null) {
            if (this.fastValueOfferHandler != null) {
                valueProcessor.valueOfferHandler = (valueContainer, elementHolder, value) -> {
                    final ImmutableValue<E> oldValue = valueProcessor.valueRetrieveHandler.get(valueContainer, elementHolder)
                            .map(ValueHelper::toImmutable).orElse(null);
                    if (!valueProcessor.fastValueOfferHandler.offer(valueContainer, elementHolder, value)) {
                        return DataTransactionResult.failResult(ValueHelper.toImmutable(value));
                    }
                    final ImmutableValue<E> newValue = ValueHelper.toImmutable(value);
                    if (oldValue != null) {
                        return DataTransactionResult.successReplaceResult(newValue, oldValue);
                    }
                    return DataTransactionResult.successResult(newValue);
                };
            } else if (this.offerHandler != null) {
                valueProcessor.valueOfferHandler = (valueContainer, elementHolder, value) ->
                        valueProcessor.offerHandler.offer(valueContainer, elementHolder, value.get());
            } else if (this.fastOfferHandler != null) {
                valueProcessor.valueOfferHandler = (valueContainer, elementHolder, value) -> {
                    final ImmutableValue<E> oldValue = valueProcessor.valueRetrieveHandler.get(valueContainer, elementHolder)
                            .map(ValueHelper::toImmutable).orElse(null);
                    if (!valueProcessor.fastOfferHandler.offer(valueContainer, elementHolder, value.get())) {
                        return DataTransactionResult.failResult(ValueHelper.toImmutable(value));
                    }
                    final ImmutableValue<E> newValue = ValueHelper.toImmutable(value);
                    if (oldValue != null) {
                        return DataTransactionResult.successReplaceResult(newValue, oldValue);
                    }
                    return DataTransactionResult.successResult(newValue);
                };
            }
        }
        /////////////////////
        // Remove Handlers //
        /////////////////////
        if (this.removeHandler == null) {
            if (this.fastRemoveHandler != null) {
                valueProcessor.removeHandler = (valueContainer, elementHolder) -> {
                    final ImmutableValue<E> oldValue = valueProcessor.valueRetrieveHandler.get(valueContainer, elementHolder)
                            .map(ValueHelper::toImmutable).orElse(null);
                    if (oldValue == null) {
                        return DataTransactionResult.failNoData();
                    }
                    if (!valueProcessor.fastRemoveHandler.remove(valueContainer, elementHolder)) {
                        return DataTransactionResult.failNoData();
                    }
                    return DataTransactionResult.successRemove(oldValue);
                };
            } else {
                valueProcessor.removeHandler = (valueContainer, elementHolder) -> {
                    final ImmutableValue<E> oldValue = valueProcessor.valueRetrieveHandler.get(valueContainer, elementHolder)
                            .map(ValueHelper::toImmutable).orElse(null);
                    elementHolder.set(null);
                    if (oldValue != null) {
                        return DataTransactionResult.successRemove(oldValue);
                    }
                    return DataTransactionResult.failNoData();
                };
            }
        }
        if (this.fastRemoveHandler == null) {
            if (this.removeHandler != null) {
                valueProcessor.fastRemoveHandler = (valueContainer, elementHolder) ->
                        valueProcessor.removeHandler.remove(valueContainer, elementHolder).isSuccessful();
            } else {
                valueProcessor.fastRemoveHandler = (valueContainer, elementHolder) -> elementHolder.set(null) != null;
            }
        }
        return valueProcessor;
    }
}
