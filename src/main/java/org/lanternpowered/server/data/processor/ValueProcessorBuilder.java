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
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.ResettableBuilder;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

// TODO: Add fast method handlers
public interface ValueProcessorBuilder<V extends BaseValue<E>, E> extends
        ResettableBuilder<ValueProcessor<V, E>, ValueProcessorBuilder<V, E>> {

    /**
     * Creates a new {@link ValueProcessorBuilder}.
     *
     * @param key The key to create the processor for
     * @param <V> The value type
     * @param <E> The element type
     * @return The builder instance
     */
    static <V extends BaseValue<E>, E> ValueProcessorBuilder<V, E> create(Key<? extends V> key) {
        return new SimpleValueProcessorBuilder<>(key);
    }

    /**
     * Set the {@link BiPredicate} that will check whether the target
     * {@link Key} is supported for {@link IValueContainer}.
     *
     * @param tester The applicable tester
     * @return This builder, for chaining
     */
    ValueProcessorBuilder<V, E> applicableTester(ApplicablePredicate<V, E> tester);

    /**
     * Set the {@link Predicate} that will check whether the target
     * {@link IValueContainer} is supported.
     *
     * @param tester The applicable tester
     * @return This builder, for chaining
     */
    default ValueProcessorBuilder<V, E> applicableTester(Predicate<IValueContainer<?>> tester) {
        return applicableTester((valueContainer, key) -> tester.test(valueContainer));
    }

    /**
     * Sets the remove handler of this value processor, it will be called when someone attempts
     * to remove a value from the {@link IValueContainer}.
     *
     * @param handler The remove handler
     * @return This builder, for chaining
     */
    ValueProcessorBuilder<V, E> removeHandler(RemoveFunction<V, E> handler);

    /**
     * Sets the remove handler of this value processor that will always fail.
     *
     * @return This builder, for chaining
     */
    default ValueProcessorBuilder<V, E> failAlwaysRemoveHandler() {
        return removeHandler(ValueProcessorHandlers.Remove.failAlways());
    }

    /**
     * Sets the offer handler of this value processor that will pass a {@link Value} through, it
     * will be called when someone attempts to add or update a value to the {@link IValueContainer}.
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    ValueProcessorBuilder<V, E> valueOfferHandler(ValueOfferFunction<V, E> handler);

    /**
     * Sets the offer handler of this value processor, it will be called when someone attempts
     * to add or update a value to the {@link IValueContainer}.
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    ValueProcessorBuilder<V, E> offerHandler(OfferFunction<V, E> handler);

    /**
     * Sets the retrieve handler of this value processor, it will be called when someone attempts
     * to get a element from the {@link IValueContainer}.
     *
     * @param handler The retrieve handler
     * @return This builder, for chaining
     */
    ValueProcessorBuilder<V, E> retrieveHandler(RetrieveFunction<V, E> handler);

    /**
     * Sets the retrieve retrieve handler of this value processor, it will be called when someone attempts
     * to get a {@link BaseValue} from the {@link IValueContainer}.
     *
     * @param handler The retrieve handler
     * @return This builder, for chaining
     */
    ValueProcessorBuilder<V, E> valueRetrieveHandler(ValueRetrieveFunction<V, E> handler);

    /**
     * Sets the builder that should be used to create the {@link Value}s.
     *
     * @param builder The value builder
     * @return This builder, for chaining
     */
    ValueProcessorBuilder<V, E> valueBuilder(ValueBuilderFunction<V, E> builder);

    /**
     * Builds a {@link ValueProcessor}.
     *
     * @return The value processor
     */
    ValueProcessor<V, E> build();

    @FunctionalInterface
    interface ValueBuilderFunction<V extends BaseValue<E>, E> {

        V get(IValueContainer<?> valueContainer, Key<? extends V> key, E element);
    }

    @FunctionalInterface
    interface ApplicablePredicate<V extends BaseValue<E>, E> {

        boolean test(IValueContainer<?> valueContainer, Key<? extends V> key);
    }

    @FunctionalInterface
    interface RemoveFunction<V extends BaseValue<E>, E> {

        DataTransactionResult remove(IValueContainer<?> valueContainer, Key<? extends V> key);
    }

    @FunctionalInterface
    interface RetrieveFunction<V extends BaseValue<E>, E> {

        Optional<E> get(IValueContainer<?> valueContainer, Key<? extends V> key);
    }

    @FunctionalInterface
    interface ValueRetrieveFunction<V extends BaseValue<E>, E> {

        Optional<V> get(IValueContainer<?> valueContainer, Key<? extends V> key);
    }

    @FunctionalInterface
    interface ValueOfferFunction<V extends BaseValue<E>, E> {

        DataTransactionResult offer(IValueContainer<?> valueContainer, V value);
    }

    @FunctionalInterface
    interface OfferFunction<V extends BaseValue<E>, E> {

        DataTransactionResult offer(IValueContainer<?> valueContainer, Key<? extends V> key, E element);
    }
}
