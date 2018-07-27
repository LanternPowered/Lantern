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
package org.lanternpowered.server.data;

import org.lanternpowered.server.util.copy.Copyable;
import org.spongepowered.api.data.value.ValueContainer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * This object holds additional {@link ValueContainer}s that may
 * be provided by {@link AdditionalContainerHolder}.
 *
 * @param <C> The value container type
 */
public interface AdditionalContainerCollection<C extends ValueContainer<?>> extends Copyable<AdditionalContainerCollection<C>> {

    /**
     * Creates a {@link AdditionalContainerCollection} that
     * permits concurrent modifications.
     *
     * @param <C> The value container type
     * @return The additional container collection
     */
    static <C extends ValueContainer<?>> AdditionalContainerCollection<C> createConcurrent() {
        return new LanternAdditionalContainerCollection<>(new ConcurrentHashMap<>());
    }

    /**
     * Creates a normal {@link AdditionalContainerCollection}.
     *
     * @param <C> The value container type
     * @return The additional container collection
     */
    static <C extends ValueContainer<?>> AdditionalContainerCollection<C> create() {
        return new LanternAdditionalContainerCollection<>(new HashMap<>());
    }

    /**
     * Gets a {@link AdditionalContainerCollection}, will not
     * store any data that is offered to it.
     *
     * @param <C> The value container type
     * @return The additional container collection
     */
    @SuppressWarnings("unchecked")
    static <C extends ValueContainer<?>> AdditionalContainerCollection<C> empty() {
        return EmptyAdditionalContainerCollection.INSTANCE;
    }

    /**
     * Gets a {@link ValueContainer} for the given container class, if present.
     *
     * @param containerClass The container class
     * @return The container, if present
     */
    <T extends C> Optional<T> get(Class<T> containerClass);

    /**
     * Offers a {@link ValueContainer} and optionally replaces the
     * previous {@link ValueContainer} of the same type.
     *
     * @param container The value container
     * @return The previous container, if present
     */
    <T extends C> Optional<T> offer(T container);

    /**
     * Gets a {@link Map} with all the {@link ValueContainer}s.
     *
     * @return The value containers
     */
    Map<Class<? extends C>, ? extends C> getMap();

    /**
     * Gets a {@link Collection} with all the {@link ValueContainer}s.
     *
     * @return The value containers
     */
    Collection<? extends C> getAll();

    /**
     * Removes a {@link ValueContainer} with the given container class.
     *
     * @param containerClass The container class
     * @return The removed container, if present
     */
    Optional<C> remove(Class<? extends C> containerClass);

    /**
     * Creates a copy of this {@link AdditionalContainerCollection}.
     *
     * @return The copy
     */
    @Override
    AdditionalContainerCollection<C> copy();

    /**
     * Creates a copy of this {@link AdditionalContainerCollection}.
     *
     * @return The copy
     */
    AdditionalContainerCollection<C> copyAsConcurrent();

    /**
     * Creates a copy of this {@link AdditionalContainerCollection}.
     *
     * @return The copy
     */
    AdditionalContainerCollection<C> copyAsNormal();

    /**
     * Creates a copy of this {@link AdditionalContainerCollection}.
     *
     * @return The copy
     */
    <R extends ValueContainer<?>> AdditionalContainerCollection<R> mapAndAsConcurrent(Function<C, R> function);

    /**
     * Creates a copy of this {@link AdditionalContainerCollection}.
     *
     * @return The copy
     */
    <R extends ValueContainer<?>> AdditionalContainerCollection<R> mapAndAsNormal(Function<C, R> function);

    /**
     * Maps this {@link AdditionalContainerCollection}.
     *
     * @param function The mapping function
     * @return The new container collection
     */
    <R extends ValueContainer<?>> AdditionalContainerCollection<R> map(Function<C, R> function);
}
