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
package org.lanternpowered.server.data.value.processor;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.data.value.ElementHolder;
import org.lanternpowered.server.data.value.IValueContainer;
import org.lanternpowered.server.data.value.ValueHelper;
import org.lanternpowered.server.util.functions.TriFunction;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import javax.annotation.Nullable;

public class SimpleValueProcessor<V extends BaseValue<E>, E> implements ValueProcessor<V, E> {

    private BiFunction<Key<? extends V>, IValueContainer<?>, DataTransactionResult> removeHandler;
    private TriFunction<Key<? extends V>, IValueContainer<?>, E, DataTransactionResult> offerHandler;
    private BiFunction<IValueContainer<?>, V, DataTransactionResult> valueOfferHandler;
    private BiFunction<Key<? extends V>, IValueContainer<?>, Optional<E>> retrieveHandler;
    private BiFunction<Key<? extends V>, IValueContainer<?>, Optional<V>> valueRetrieveHandler;
    private final TriFunction<Key<? extends V>, IValueContainer<?>, E, V> valueBuilder;
    private BiPredicate<Key<? extends V>, IValueContainer<?>> applicableTester;

    public SimpleValueProcessor(
            BiFunction<Key<? extends V>, IValueContainer<?>, DataTransactionResult> removeHandler,
            TriFunction<Key<? extends V>, IValueContainer<?>, E, DataTransactionResult> offerHandler,
            BiFunction<Key<? extends V>, IValueContainer<?>, Optional<E>> retrieveHandler,
            BiFunction<Key<? extends V>, IValueContainer<?>, Optional<V>> valueRetrieveHandler,
            TriFunction<Key<? extends V>, IValueContainer<?>, E, V> valueBuilder,
            BiPredicate<Key<? extends V>, IValueContainer<?>> applicableTester) {
        this.removeHandler = removeHandler;
        this.offerHandler = offerHandler;
        this.valueBuilder = valueBuilder;
        this.applicableTester = applicableTester;
        this.retrieveHandler = retrieveHandler;
        this.valueRetrieveHandler = valueRetrieveHandler;
    }

    @Override
    public BiPredicate<Key<? extends V>, IValueContainer<?>> getApplicableTester() {
        return this.applicableTester;
    }

    @Override
    public BiFunction<Key<? extends V>, IValueContainer<?>, DataTransactionResult> getRemoveHandler() {
        return this.removeHandler;
    }

    @Override
    public TriFunction<Key<? extends V>, IValueContainer<?>, E, DataTransactionResult> getOfferHandler() {
        return this.offerHandler;
    }

    @Override
    public BiFunction<Key<? extends V>, IValueContainer<?>, Optional<E>> getRetrieveHandler() {
        return this.retrieveHandler;
    }

    @Override
    public BiFunction<Key<? extends V>, IValueContainer<?>, Optional<V>> getValueRetrieveHandler() {
        return this.valueRetrieveHandler;
    }

    @Override
    public BiFunction<IValueContainer<?>, V, DataTransactionResult> getValueOfferHandler() {
        return this.valueOfferHandler;
    }

    @Override
    public TriFunction<Key<? extends V>, IValueContainer<?>, E, V> getValueBuilder() {
        return this.valueBuilder;
    }

    @SuppressWarnings("unchecked")
    public static class BuilderBase<V extends BaseValue<E>, E, B extends ValueProcessor.BuilderBase<V, E, B>>
            implements ValueProcessor.BuilderBase<V, E, B> {

        @Nullable protected BiFunction<Key<? extends V>, IValueContainer<?>, DataTransactionResult> removeHandler;
        @Nullable protected TriFunction<Key<? extends V>, IValueContainer<?>, E, DataTransactionResult> offerHandler;
        @Nullable protected BiFunction<IValueContainer<?>, V, DataTransactionResult> valueOfferHandler;
        @Nullable protected TriFunction<Key<? extends V>, IValueContainer<?>, E, V> valueBuilder;
        @Nullable protected BiFunction<Key<? extends V>, IValueContainer<?>, Optional<E>> retrieveHandler;
        @Nullable protected BiFunction<Key<? extends V>, IValueContainer<?>, Optional<V>> valueRetrieveHandler;
        @Nullable protected BiPredicate<Key<? extends V>, IValueContainer<?>> applicableTester;

        BuilderBase() {
            this.reset();
        }

        @Override
        public B applicableTester(BiPredicate<Key<? extends V>, IValueContainer<?>> tester) {
            this.applicableTester = checkNotNull(tester, "tester");
            return (B) this;
        }

        @Override
        public B removeHandler(BiFunction<Key<? extends V>, IValueContainer<?>, DataTransactionResult> handler) {
            this.removeHandler = checkNotNull(handler, "handler");
            return (B) this;
        }

        @Override
        public B valueOfferHandler(BiFunction<IValueContainer<?>, V, DataTransactionResult> handler) {
            this.valueOfferHandler = checkNotNull(handler, "handler");
            return (B) this;
        }

        @Override
        public B offerHandler(TriFunction<Key<? extends V>, IValueContainer<?>, E, DataTransactionResult> handler) {
            this.offerHandler = checkNotNull(handler, "handler");
            return (B) this;
        }

        @Override
        public B retrieveHandler(BiFunction<Key<? extends V>, IValueContainer<?>, Optional<E>> handler) {
            this.retrieveHandler = checkNotNull(handler, "handler");
            return (B) this;
        }

        @Override
        public B valueRetrieveHandler(BiFunction<Key<? extends V>, IValueContainer<?>, Optional<V>> handler) {
            this.valueRetrieveHandler = checkNotNull(handler, "handler");
            return (B) this;
        }

        @Override
        public B valueBuilder(TriFunction<Key<? extends V>, IValueContainer<?>, E, V> builder) {
            this.valueBuilder = checkNotNull(builder, "builder");
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public SimpleValueProcessor<V, E> build() {
            checkState(this.offerHandler != null || this.valueOfferHandler != null, "The offer handler must be set.");
            checkState(this.retrieveHandler != null || this.valueRetrieveHandler != null, "The retrieve handler must be set.");
            SimpleValueProcessor<V, E> valueProcessor = new SimpleValueProcessor<>(this.removeHandler == null ? RemoveHandlers.failAlways() :
                    this.removeHandler, this.offerHandler, this.retrieveHandler, this.valueRetrieveHandler, this.valueBuilder == null ?
                    ValueBuilders.def() : this.valueBuilder, this.applicableTester == null ? (k, c) -> true : this.applicableTester);
            if (this.offerHandler == null) {
                valueProcessor.offerHandler = (key, valueContainer, element) -> valueProcessor.getValueOfferHandler()
                        .apply(valueContainer, valueProcessor.getValueBuilder().apply(key, valueContainer, element));
            } else if (this.valueOfferHandler == null) {
                valueProcessor.valueOfferHandler = (valueContainer, value) -> valueProcessor.getOfferHandler()
                        .apply((Key) value.getKey(), valueContainer, value.get());
            }
            if (this.retrieveHandler == null) {
                valueProcessor.retrieveHandler = (key, valueContainer) -> valueProcessor.getValueRetrieveHandler()
                        .apply(key, valueContainer).flatMap(value -> Optional.of(value.get()));
            } else if (this.valueRetrieveHandler == null) {
                valueProcessor.valueRetrieveHandler = (key, valueContainer) -> valueProcessor.getRetrieveHandler().apply(key, valueContainer)
                        .flatMap(element -> Optional.of(valueProcessor.getValueBuilder().apply(key, valueContainer, element)));
            }
            return valueProcessor;
        }

        @Override
        public B from(ValueProcessor<V, E> value) {
            this.offerHandler = value.getOfferHandler();
            this.valueOfferHandler = value.getValueOfferHandler();
            this.removeHandler = value.getRemoveHandler();
            this.retrieveHandler = value.getRetrieveHandler();
            this.valueRetrieveHandler = value.getValueRetrieveHandler();
            this.valueBuilder = value.getValueBuilder();
            this.applicableTester = value.getApplicableTester();
            return (B) this;
        }

        @Override
        public B reset() {
            this.offerHandler = null;
            this.valueOfferHandler = null;
            this.removeHandler = null;
            this.retrieveHandler = null;
            this.valueRetrieveHandler = null;
            this.valueBuilder = null;
            this.applicableTester = null;
            return (B) this;
        }
    }

    public static class Builder<V extends BaseValue<E>, E> extends BuilderBase<V, E, ValueProcessor.Builder<V, E>>
            implements ValueProcessor.Builder<V, E> {

        Builder() {
        }
    }

    public static class AttachedElementBuilder<V extends BaseValue<E>, E> extends BuilderBase<V, E, ValueProcessor.AttachedElementBuilder<V, E>>
            implements ValueProcessor.AttachedElementBuilder<V, E> {

        /**
         * The default {@link SimpleValueProcessor} for attached elements.
         */
        static SimpleValueProcessor DEFAULT;

        /**
         * The shared {@link SimpleValueProcessor} that will only make the attached elements unremovable.
         */
        static SimpleValueProcessor NON_REMOVABLE;

        private static boolean initialized = false;

        static {
            DEFAULT = new AttachedElementBuilder().build();
            NON_REMOVABLE = (SimpleValueProcessor) new AttachedElementBuilder().failAlwaysRemoveHandler().build();
            initialized = true;
        }

        AttachedElementBuilder() {
        }

        @Override
        public AttachedElementBuilder<V, E> from(ValueProcessor<V, E> value) {
            if (value == DEFAULT || value == NON_REMOVABLE) {
                this.reset();
                if (value == NON_REMOVABLE) {
                    this.removeHandler = value.getRemoveHandler();
                }
            } else {
                super.from(value);
            }
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public SimpleValueProcessor<V, E> build() {
            // initialized -> Make sure that the cached fields are initialized before we can use them
            // otherwise will the cached fields never be non-null
            if (initialized && this.applicableTester == null && this.offerHandler == null && this.valueOfferHandler == null &&
                    this.retrieveHandler == null && this.valueRetrieveHandler == null && this.valueBuilder == null) {
                if (this.removeHandler == null) {
                    return DEFAULT;
                } else if (this.removeHandler.equals(RemoveHandlers.failAlways())) {
                    return NON_REMOVABLE;
                }
            }
            SimpleValueProcessor<V, E> valueProcessor = new SimpleValueProcessor<>(this.removeHandler, this.offerHandler, this.retrieveHandler,
                    this.valueRetrieveHandler, this.valueBuilder == null ? ValueBuilders.def() : this.valueBuilder, applicableTester);
            if (this.applicableTester == null) {
                valueProcessor.applicableTester = (key, container) -> container.getElementHolder(key) != null;
            } else {
                BiPredicate<Key<? extends V>, IValueContainer<?>> applicableTester = this.applicableTester;
                valueProcessor.applicableTester = (key, container) -> container.getElementHolder(key) != null && applicableTester.test(key, container);
            }
            if (this.retrieveHandler == null) {
                if (this.valueRetrieveHandler == null) {
                    valueProcessor.retrieveHandler = (key, valueContainer) -> {
                        ElementHolder<E> iValue = checkNotNull(valueContainer.getElementHolder(key));
                        return Optional.ofNullable(iValue.get());
                    };
                } else {
                    valueProcessor.retrieveHandler = (key, valueContainer) -> valueProcessor.getValueRetrieveHandler()
                            .apply(key, valueContainer).flatMap(value -> Optional.of(value.get()));
                }
            }
            if (this.valueRetrieveHandler == null) {
                if (this.retrieveHandler == null) {
                    valueProcessor.valueRetrieveHandler = (key, valueContainer) -> {
                        ElementHolder<E> iValue = checkNotNull(valueContainer.getElementHolder(key));
                        E element = iValue.get();
                        return element == null ? Optional.empty() : Optional.of(valueProcessor.getValueBuilder().apply(key, valueContainer, element));
                    };
                } else {
                    valueProcessor.valueRetrieveHandler = (key, valueContainer) -> valueProcessor.getRetrieveHandler().apply(key, valueContainer)
                            .flatMap(element -> Optional.of(valueProcessor.getValueBuilder().apply(key, valueContainer, element)));
                }
            }
            if (this.offerHandler == null) {
                if (this.valueOfferHandler == null) {
                    valueProcessor.offerHandler = (key, valueContainer, element) -> {
                        ElementHolder<E> elementHolder = checkNotNull(valueContainer.getElementHolder(key));
                        E oldElement = elementHolder.set(element);
                        ImmutableValue<E> newValue = ValueHelper.toImmutable(valueProcessor.getValueBuilder().apply(key, valueContainer, element));
                        if (oldElement != null) {
                            ImmutableValue<E> oldValue =
                                    ValueHelper.toImmutable(valueProcessor.getValueBuilder().apply(key, valueContainer, oldElement));
                            return DataTransactionResult.successReplaceResult(newValue, oldValue);
                        }
                        return DataTransactionResult.successResult(newValue);
                    };
                } else {
                    valueProcessor.offerHandler = (key, valueContainer, element) -> valueProcessor.getValueOfferHandler()
                            .apply(valueContainer, valueProcessor.getValueBuilder().apply(key, valueContainer, element));
                }
            }
            if (this.valueOfferHandler == null) {
                if (this.offerHandler == null) {
                    valueProcessor.valueOfferHandler = (valueContainer, value) -> {
                        Key key = value.getKey();
                        E element = value.get();
                        ElementHolder<E> elementHolder = checkNotNull(valueContainer.getElementHolder(key));
                        E oldElement = elementHolder.set(element);
                        ImmutableValue<E> newValue = ValueHelper.toImmutable(value);
                        if (oldElement != null) {
                            ImmutableValue<E> oldValue =
                                    ValueHelper.toImmutable(valueProcessor.getValueBuilder().apply(key, valueContainer, oldElement));
                            return DataTransactionResult.successReplaceResult(newValue, oldValue);
                        }
                        return DataTransactionResult.successResult(newValue);
                    };
                } else {
                    valueProcessor.valueOfferHandler = (valueContainer, value) -> valueProcessor.getOfferHandler()
                            .apply((Key) value.getKey(), valueContainer, value.get());
                }
            }
            if (this.removeHandler == null) {
                valueProcessor.removeHandler = (key, valueContainer) -> {
                    ElementHolder<E> elementHolder = checkNotNull(valueContainer.getElementHolder(key));
                    E oldElement = elementHolder.set(null);
                    if (oldElement != null) {
                        ImmutableValue<E> oldValue = ValueHelper.toImmutable(valueProcessor.getValueBuilder().apply(key, valueContainer, oldElement));
                        return DataTransactionResult.successRemove(oldValue);
                    }
                    return DataTransactionResult.failNoData();
                };
            }
            return valueProcessor;
        }
    }
}
