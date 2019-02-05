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
import org.lanternpowered.server.data.element.Element;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.util.CopyableBuilder;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface ElementProcessorBuilder<V extends Value<E>, E>
        extends CopyableBuilder<ElementProcessor<V, E>, ElementProcessorBuilder<V, E>> {

    /**
     * Creates the default {@link ElementProcessor} for the specified {@link Key}.
     *
     * @param key The key
     * @param <V> The value type
     * @param <E> The element type
     * @return The element processor
     */
    static <V extends Value<E>, E> ElementProcessor<V, E> createDefault(Key<? extends V> key) {
        return SimpleElementProcessorBuilder.createDefault(key);
    }

    /**
     * Creates the default {@link ElementProcessor} that doesn't allow removal
     * for the specified {@link Key}.
     *
     * @param key The key
     * @param <V> The value type
     * @param <E> The element type
     * @return The element processor
     */
    static <V extends Value<E>, E> ElementProcessor<V, E> createNonRemovable(Key<? extends V> key) {
        return SimpleElementProcessorBuilder.createNonRemovableDefault(key);
    }

    /**
     * Creates a new {@link ElementProcessorBuilder}.
     *
     * @param key The key to create the processor for
     * @param <V> The value type
     * @param <E> The element type
     * @return The builder instance
     */
    static <V extends Value<E>, E> ElementProcessorBuilder<V, E> create(Key<? extends V> key) {
        return new SimpleElementProcessorBuilder<>(key);
    }

    /**
     * Set the {@link BiPredicate} that will check whether the target
     * {@link Key} is supported for {@link IValueContainer}.
     *
     * @param tester The applicable tester
     * @return This builder, for chaining
     */
    ElementProcessorBuilder<V, E> applicableTester(ApplicablePredicate<E> tester);

    /**
     * Set the {@link Predicate} that will check whether the target
     * {@link IValueContainer} is supported.
     *
     * @param tester The applicable tester
     * @return This builder, for chaining
     */
    default ElementProcessorBuilder<V, E> applicableTester(Predicate<IValueContainer<?>> tester) {
        return applicableTester((valueContainer, key) -> tester.test(valueContainer));
    }

    /**
     * Sets the retrieve handler of this value processor, it will be called when someone attempts
     * to get a element from the {@link IValueContainer}.
     *
     * @param handler The retrieve handler
     * @return This builder, for chaining
     */
    ElementProcessorBuilder<V, E> retrieveHandler(RetrieveFunction<E> handler);

    /**
     * Sets the retrieve retrieve handler of this value processor, it will be called when someone attempts
     * to get a {@link Value} from the {@link IValueContainer}.
     *
     * @param handler The retrieve handler
     * @return This builder, for chaining
     */
    ElementProcessorBuilder<V, E> valueRetrieveHandler(ValueRetrieveFunction<V, E> handler);

    /**
     * Sets the offer handler of this value processor, it will be called when someone attempts
     * to add or update a value to the {@link IValueContainer}.
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    ElementProcessorBuilder<V, E> offerHandler(OfferFunction<E> handler);

    /**
     * Sets the offer handler of this value processor, it will be called when someone attempts
     * to add or update a value to the {@link IValueContainer}.
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    ElementProcessorBuilder<V, E> fastOfferHandler(FastOfferFunction<E> handler);

    /**
     * Sets the offer handler of this value processor that will pass a {@link Value.Mutable} through, it
     * will be called when someone attempts to add or update a value to the {@link IValueContainer}.
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    ElementProcessorBuilder<V, E> valueOfferHandler(ValueOfferFunction<V, E> handler);

    /**
     * Sets the offer handler of this value processor that will pass a {@link Value.Mutable} through, it
     * will be called when someone attempts to add or update a value to the {@link IValueContainer}.
     *
     * @param handler The offer handler
     * @return This builder, for chaining
     */
    ElementProcessorBuilder<V, E> fastValueOfferHandler(FastValueOfferFunction<V, E> handler);

    /**
     * Sets the remove handler of this value processor, it will be called when someone attempts
     * to remove a value from the {@link IValueContainer}.
     *
     * @param handler The remove handler
     * @return This builder, for chaining
     */
    ElementProcessorBuilder<V, E> removeHandler(RemoveFunction<E> handler);

    /**
     * Sets the remove handler of this value processor, it will be called when someone attempts
     * to remove a value from the {@link IValueContainer}.
     *
     * @param handler The remove handler
     * @return This builder, for chaining
     */
    ElementProcessorBuilder<V, E> fastRemoveHandler(FastRemoveFunction<E> handler);

    /**
     * Sets the remove handler of this value processor that will always fail.
     *
     * @return This builder, for chaining
     */
    default ElementProcessorBuilder<V, E> failAlwaysRemoveHandler() {
        return fastRemoveHandler(ElementProcessorHandlers.Remove.failAlways());
    }

    /**
     * Sets the builder that should be used to create the {@link Value.Mutable}s.
     *
     * @param builder The value builder
     * @return This builder, for chaining
     */
    ElementProcessorBuilder<V, E> valueBuilder(ValueBuilderFunction<V, E> builder);

    /**
     * Sets that the {@link ElementProcessor} should be synchronized.
     *
     * @return This builder, for chaining
     */
    ElementProcessorBuilder<V, E> synchronize();

    /**
     * Builds a {@link ElementProcessor}.
     *
     * @return The element processor
     */
    ElementProcessor<V, E> build();

    @FunctionalInterface
    interface ValueBuilderFunction<V extends Value<E>, E> {

        V get(IValueContainer<?> valueContainer, Element<E> elementHolder, E element);
    }

    @FunctionalInterface
    interface FastRemoveFunction<E> {

        boolean remove(IValueContainer<?> valueContainer, Element<E> elementHolder);
    }

    @FunctionalInterface
    interface RemoveFunction<E> {

        DataTransactionResult remove(IValueContainer<?> valueContainer, Element<E> elementHolder);
    }

    @FunctionalInterface
    interface ApplicablePredicate<E> {

        boolean test(IValueContainer<?> valueContainer, Element<E> elementHolder);
    }

    @FunctionalInterface
    interface RetrieveFunction<E> {

        Optional<E> get(IValueContainer<?> valueContainer, Element<E> elementHolder);
    }

    @FunctionalInterface
    interface ValueRetrieveFunction<V extends Value<E>, E> {

        Optional<V> get(IValueContainer<?> valueContainer, Element<E> elementHolder);
    }

    @FunctionalInterface
    interface ValueOfferFunction<V extends Value<E>, E> {

        DataTransactionResult offer(IValueContainer<?> valueContainer, Element<E> elementHolder, V value);
    }

    @FunctionalInterface
    interface FastValueOfferFunction<V extends Value<E>, E> {

        boolean offer(IValueContainer<?> valueContainer, Element<E> elementHolder, V value);
    }

    @FunctionalInterface
    interface OfferFunction<E> {

        DataTransactionResult offer(IValueContainer<?> valueContainer, Element<E> elementHolder, E element);
    }

    @FunctionalInterface
    interface FastOfferFunction<E> {

        boolean offer(IValueContainer<?> valueContainer, Element<E> elementHolder, E element);
    }
}
