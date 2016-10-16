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

import org.lanternpowered.server.data.value.ElementHolder;
import org.lanternpowered.server.data.value.IValueContainer;
import org.lanternpowered.server.util.functions.QuadFunction;
import org.lanternpowered.server.util.functions.TriFunction;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.ResettableBuilder;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface ValueProcessor<V extends BaseValue<E>, E> {

    /**
     * Get the {@link Predicate} that will check whether the target
     * {@link IValueContainer} is supported.
     *
     * @return The applicable tester
     */
    BiPredicate<Key<? extends V>, IValueContainer<?>> getApplicableTester();

    /**
     * Gets the handler that will be used to handle removed {@link Key}s.
     *
     * @return The remove handler
     */
    BiFunction<Key<? extends V>, IValueContainer<?>, DataTransactionResult> getRemoveHandler();

    /**
     * Gets the handler that will be used to handle offered {@link Key}s with a element.
     *
     * @return The offer handler
     */
    TriFunction<Key<? extends V>, IValueContainer<?>, E, DataTransactionResult> getOfferHandler();

    /**
     * Gets the handler that will be used to handle retrieved {@link Key}s.
     *
     * @return The retrieve handler
     */
    BiFunction<Key<? extends V>, IValueContainer<?>, Optional<E>> getRetrieveHandler();


    /**
     * Gets the handler that will be used to handle retrieved {@link BaseValue}s.
     *
     * @return The value retrieve handler
     */
    BiFunction<Key<? extends V>, IValueContainer<?>, Optional<V>> getValueRetrieveHandler();

    /**
     * Gets the handler that will be used to handle offered {@link BaseValue}s.
     *
     * @return The offer handler
     */
    BiFunction<IValueContainer<?>, V, DataTransactionResult> getValueOfferHandler();

    /**
     * Gets the builder that will be used to create {@link BaseValue}s.
     *
     * @return The value builder
     */
    TriFunction<Key<? extends V>, IValueContainer<?>, E, V> getValueBuilder();

    /**
     * Gets the default attached element {@link ValueProcessor}.
     *
     * @param <V> The value type
     * @param <E> The element type
     * @return The value processor
     */
    @SuppressWarnings("unchecked")
    static <V extends BaseValue<E>, E> ValueProcessor<V, E> getDefaultAttachedValueProcessor() {
        return SimpleValueProcessor.AttachedElementBuilder.DEFAULT;
    }

    /**
     * Gets the default attached element {@link ValueProcessor} that doesn't allow the removal of elements.
     *
     * @param <V> The value type
     * @param <E> The element type
     * @return The value processor
     */
    @SuppressWarnings("unchecked")
    static <V extends BaseValue<E>, E> ValueProcessor<V, E> getNonRemovableDefaultAttachedValueProcessor() {
        return SimpleValueProcessor.AttachedElementBuilder.NON_REMOVABLE;
    }

    static <V extends BaseValue<E>, E> Builder<V, E> builder() {
        return new SimpleValueProcessor.Builder<>();
    }

    static <V extends BaseValue<E>, E> AttachedElementBuilder<V, E> attachedElementBuilder() {
        return new SimpleValueProcessor.AttachedElementBuilder<>();
    }

    interface BuilderBase<V extends BaseValue<E>, E, B extends BuilderBase<V, E, B>> extends ResettableBuilder<ValueProcessor<V, E>, B> {

        /**
         * Set the {@link BiPredicate} that will check whether the target
         * {@link Key} is supported for {@link IValueContainer}.
         *
         * @param tester The applicable tester
         * @return The builder for chaining
         */
        B applicableTester(BiPredicate<Key<? extends V>, IValueContainer<?>> tester);

        /**
         * Set the {@link Predicate} that will check whether the target
         * {@link IValueContainer} is supported.
         *
         * @param tester The applicable tester
         * @return The builder for chaining
         */
        default B applicableTester(Predicate<IValueContainer<?>> tester) {
            return this.applicableTester((key, valueContainer) -> tester.test(valueContainer));
        }

        /**
         * Sets the remove handler of this value processor, it will be called when someone attempts
         * to remove a value with a specific {@link Key} from the {@link IValueContainer}.
         *
         * @param handler The remove handler
         * @return The builder for chaining
         */
        B removeHandler(BiFunction<Key<? extends V>, IValueContainer<?>, DataTransactionResult> handler);

        /**
         * Sets the remove handler of this value processor that will always fail.
         *
         * @return The builder for chaining
         */
        default B failAlwaysRemoveHandler() {
            return this.removeHandler(RemoveHandlers.failAlways());
        }

        /**
         * Sets the offer handler of this value processor that will pass a {@link Value} through, it will be called when someone attempts
         * to add or update a value for a specific {@link Key} to the {@link IValueContainer}.
         *
         * @param handler The offer handler
         * @return The value processor for chaining
         */
        B valueOfferHandler(BiFunction<IValueContainer<?>, V, DataTransactionResult> handler);

        /**
         * Sets the offer handler of this value processor, it will be called when someone attempts
         * to add or update a value for a specific {@link Key} to the {@link IValueContainer}.
         *
         * @param handler The offer handler
         * @return The value processor for chaining
         */
        B offerHandler(TriFunction<Key<? extends V>, IValueContainer<?>, E, DataTransactionResult> handler);

        /**
         * Sets the retrieve handler of this value processor, it will be called when someone attempts
         * to get a element for a specific {@link Key} from the {@link IValueContainer}.
         *
         * @param handler The retrieve handler
         * @return The value processor for chaining
         */
        B retrieveHandler(BiFunction<Key<? extends V>, IValueContainer<?>, Optional<E>> handler);

        /**
         * Sets the retrieve retrieve handler of this value processor, it will be called when someone attempts
         * to get a {@link BaseValue} for a specific {@link Key} from the {@link IValueContainer}.
         *
         * @param handler The retrieve handler
         * @return The value processor for chaining
         */
        B valueRetrieveHandler(BiFunction<Key<? extends V>, IValueContainer<?>, Optional<V>> handler);

        /**
         * Sets the builder that should be used to create the {@link Value}s.
         *
         * @param builder The value builder
         * @return The value processor for chaining
         */
        B valueBuilder(TriFunction<Key<? extends V>, IValueContainer<?>, E, V> builder);

        /**
         * Builds a {@link ValueProcessor}.
         *
         * @return The value processor
         */
        ValueProcessor<V, E> build();

    }

    interface Builder<V extends BaseValue<E>, E> extends BuilderBase<V, E, Builder<V, E>> {

    }

    /**
     * A {@link ValueProcessor.Builder} that expects that there is a {@link ElementHolder} attached
     * to the target key.
     *
     * @param <V> The value type
     * @param <E> The element type
     */
    interface AttachedElementBuilder<V extends BaseValue<E>, E> extends BuilderBase<V, E, AttachedElementBuilder<V, E>> {

        /**
         * Sets the offer handler.
         *
         * @param handler The offer handler
         * @return The value processor for chaining
         */
        default AttachedElementBuilder<V, E> offerHandler(QuadFunction<Key<? extends V>, IValueContainer<?>, ElementHolder<E>, E,
                DataTransactionResult> handler) {
            return this.offerHandler(((key, valueContainer, e) -> handler.apply(key, valueContainer, checkNotNull(valueContainer.getElementHolder(key)), e)));
        }

        /**
         * Sets the remove handler.
         *
         * @param handler The remove handler
         * @return The value processor for chaining
         */
        default AttachedElementBuilder<V, E> removeHandler(TriFunction<Key<? extends V>, IValueContainer<?>, ElementHolder<E>,
                DataTransactionResult> handler) {
            return this.removeHandler(((key, valueContainer) -> handler.apply(key, valueContainer, checkNotNull(valueContainer.getElementHolder(key)))));
        }
    }
}
