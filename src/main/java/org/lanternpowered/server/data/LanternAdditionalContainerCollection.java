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
import com.google.common.collect.Iterables;
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

@SuppressWarnings({"unchecked", "Convert2MethodRef"})
class LanternAdditionalContainerCollection<C extends ValueContainer<?>> implements AdditionalContainerCollection<C> {

    private final Map<Class<?>, C> containers;
    private final Map<Class<?>, C> unmodifiableContainers;

    LanternAdditionalContainerCollection(Map<Class<?>, C> containers) {
        this.unmodifiableContainers = Collections.unmodifiableMap(containers);
        this.containers = containers;
    }

    @Override
    public <T extends C> Optional<T> get(Class<T> containerClass) {
        for (C container : this.containers.values()) {
            if (containerClass.isInstance(container)) {
                return Optional.of((T) (container instanceof DataManipulator ?
                        ((DataManipulator) container).copy() : container));
            }
        }
        return Optional.empty();
    }

    @Override
    public <T extends C> Optional<T> offer(T container) {
        checkNotNull(container, "container");
        final C old = this.containers.put(container.getClass(), container);
        return Optional.ofNullable((T) old);
    }

    @Override
    public Map<Class<? extends C>, ? extends C> getMap() {
        return (Map) this.unmodifiableContainers;
    }

    @Override
    public Collection<? extends C> getAll() {
        return this.unmodifiableContainers.values();
    }

    @Override
    public Optional<C> remove(Class<? extends C> containerClass) {
        checkNotNull(containerClass, "containerClass");
        return Optional.ofNullable(this.containers.remove(containerClass));
    }

    @Override
    public LanternAdditionalContainerCollection<C> copy() {
        return map(c -> (C) c.copy());
    }

    @Override
    public LanternAdditionalContainerCollection<C> copyAsConcurrent() {
        return map(() -> new ConcurrentHashMap<>(), c -> (C) c.copy()); // No direct reference here, thanks intellij...
    }

    @Override
    public LanternAdditionalContainerCollection<C> copyAsNormal() {
        return map(() -> new HashMap<>(), c -> (C) c.copy()); // No direct reference here, thanks intellij...
    }

    @Override
    public <R extends ValueContainer<?>> LanternAdditionalContainerCollection<R> mapAndAsConcurrent(Function<C, R> function) {
        return map(() -> new ConcurrentHashMap<>(), function); // No direct reference here, thanks intellij...
    }

    @Override
    public <R extends ValueContainer<?>> LanternAdditionalContainerCollection<R> mapAndAsNormal(Function<C, R> function) {
        return map(() -> new HashMap<>(), function); // No direct reference here, thanks intellij...
    }

    @Override
    public <R extends ValueContainer<?>> LanternAdditionalContainerCollection<R> map(Function<C, R> function) {
        return map(() -> {
            if (this.containers instanceof ConcurrentHashMap) {
                return new ConcurrentHashMap<>();
            } else {
                return new HashMap<>();
            }
        }, function);
    }

    private <R extends ValueContainer<?>> LanternAdditionalContainerCollection<R> map(
            Supplier<Map<Class<?>, R>> supplier, Function<C, R> function) {
        final Map<Class<?>, R> map = supplier.get();
        for (Map.Entry<Class<?>, C> entry : this.containers.entrySet()) {
            map.put(entry.getKey(), function.apply(entry.getValue()));
        }
        return new LanternAdditionalContainerCollection<>(map);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("containers", Iterables.toString(this.containers.values()))
                .add("type", this.containers instanceof ConcurrentHashMap ? "concurrent" : "normal")
                .toString();
    }
}
