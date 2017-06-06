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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.util.collect.Collections3;
import org.lanternpowered.server.util.copy.Copyable;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.value.ValueContainer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This object holds additional {@link ValueContainer}s that may
 * be provided by {@link AdditionalContainerHolder}.
 *
 * @param <C> The value container type
 */
@SuppressWarnings("unchecked")
public final class AdditionalContainerCollection<C extends ValueContainer<?>>
        implements Copyable<AdditionalContainerCollection<C>> {

    /**
     * Creates a {@link AdditionalContainerCollection} that
     * permits concurrent modifications.
     */
    public static <C extends ValueContainer<?>> AdditionalContainerCollection<C> createConcurrent() {
        return new AdditionalContainerCollection<>(new ConcurrentHashMap<>());
    }

    /**
     * Creates a normal {@link AdditionalContainerCollection}.
     */
    public static <C extends ValueContainer<?>> AdditionalContainerCollection<C> create() {
        return new AdditionalContainerCollection<>(new HashMap<>());
    }

    private final Map<Class<?>, C> containers;
    private final Collection<C> unmodifiableContainers;

    private AdditionalContainerCollection(Map<Class<?>, C> containers) {
        this.unmodifiableContainers = Collections.unmodifiableCollection(containers.values());
        this.containers = containers;
    }

    /**
     * Gets a {@link ValueContainer} for the given container class, if present.
     *
     * @param containerClass The container class
     * @return The container, if present
     */
    public <T extends C> Optional<T> get(Class<T> containerClass) {
        for (C container : this.containers.values()) {
            if (containerClass.isInstance(container)) {
                return Optional.of((T) (container instanceof DataManipulator ?
                        ((DataManipulator) container).copy() : container));
            }
        }
        return Optional.empty();
    }

    /**
     * Offers a {@link ValueContainer} and optionally replaces the
     * previous {@link ValueContainer} of the same type.
     *
     * @param container The value container
     * @return The previous container, if present
     */
    public <T extends C> Optional<T> offer(T container) {
        checkNotNull(container, "container");
        final C old = this.containers.put(container.getClass(), container);
        return Optional.ofNullable((T) old);
    }

    /**
     * Gets a {@link Collection} with all the {@link ValueContainer}s.
     *
     * @return The value containers
     */
    public Collection<C> getAll() {
        return this.unmodifiableContainers;
    }

    /**
     * Removes a {@link ValueContainer} with the given container class.
     *
     * @param containerClass The container class
     * @return The removed container, if present
     */
    public Optional<C> remove(Class<? extends C> containerClass) {
        checkNotNull(containerClass, "containerClass");
        return Optional.ofNullable(this.containers.remove(containerClass));
    }

    /**
     * Creates a copy of this {@link AdditionalContainerCollection}.
     *
     * @return The copy
     */
    @Override
    public AdditionalContainerCollection<C> copy() {
        return map(c -> (C) c.copy());
    }

    /**
     * Creates a copy of this {@link AdditionalContainerCollection}.
     *
     * @return The copy
     */
    public AdditionalContainerCollection<C> copyAsConcurrent() {
        return map(() -> new ConcurrentHashMap<>(), c -> (C) c.copy()); // No direct reference here, thanks intellij...
    }

    /**
     * Creates a copy of this {@link AdditionalContainerCollection}.
     *
     * @return The copy
     */
    public AdditionalContainerCollection<C> copyAsNormal() {
        return map(() -> new HashMap<>(), c -> (C) c.copy()); // No direct reference here, thanks intellij...
    }

    /**
     * Creates a copy of this {@link AdditionalContainerCollection}.
     *
     * @return The copy
     */
    public <R extends ValueContainer<?>> AdditionalContainerCollection<R> mapAndAsConcurrent(Function<C, R> function) {
        return map(() -> new ConcurrentHashMap<>(), function); // No direct reference here, thanks intellij...
    }

    /**
     * Creates a copy of this {@link AdditionalContainerCollection}.
     *
     * @return The copy
     */
    public <R extends ValueContainer<?>> AdditionalContainerCollection<R> mapAndAsNormal(Function<C, R> function) {
        return map(() -> new HashMap<>(), function); // No direct reference here, thanks intellij...
    }

    /**
     * Maps this {@link AdditionalContainerCollection}.
     *
     * @param function The mapping function
     * @return The new container collection
     */
    public <R extends ValueContainer<?>> AdditionalContainerCollection<R> map(Function<C, R> function) {
        return map(() -> {
            if (this.containers instanceof ConcurrentHashMap) {
                return new ConcurrentHashMap<>();
            } else {
                return new HashMap<>();
            }
        }, function);
    }

    private <R extends ValueContainer<?>> AdditionalContainerCollection<R> map(
            Supplier<Map<Class<?>, R>> supplier, Function<C, R> function) {
        final Map<Class<?>, R> map = supplier.get();
        for (Map.Entry<Class<?>, C> entry : this.containers.entrySet()) {
            map.put(entry.getKey(), function.apply(entry.getValue()));
        }
        return new AdditionalContainerCollection<>(map);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("containers", Collections3.toString(this.containers.values()))
                .add("type", this.containers instanceof ConcurrentHashMap ? "concurrent" : "normal")
                .toString();
    }
}
