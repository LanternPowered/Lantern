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

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.value.ValueContainer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class MutableToImmutableManipulatorCollection implements AdditionalContainerCollection<ImmutableDataManipulator<?,?>> {

    private final AdditionalContainerCollection<DataManipulator<?,?>> collection;
    private final Map<Class<? extends ImmutableDataManipulator<?,?>>, Optional<ImmutableDataManipulator<?,?>>> cached;
    private final Map<Class<? extends ImmutableDataManipulator<?,?>>, Optional<ImmutableDataManipulator<?,?>>> unmodifiableCached;

    public MutableToImmutableManipulatorCollection(AdditionalContainerCollection<DataManipulator<?, ?>> collection) {
        this.cached = new ConcurrentHashMap<>();
        this.unmodifiableCached = Collections.unmodifiableMap(this.cached);
        this.collection = collection;
    }

    private static Class<? extends DataManipulator<?,?>> getMutableClass(Class<? extends ImmutableDataManipulator> containerClass) {
        return Lantern.getGame().getDataManager().get(containerClass)
                .orElseThrow(() -> new IllegalArgumentException("The container " + containerClass.getName() + " isn't registered."))
                .getManipulatorClass();
    }

    private static Class<? extends ImmutableDataManipulator<?,?>> getImmutableClass(Class<? extends DataManipulator> containerClass) {
        return Lantern.getGame().getDataManager().get(containerClass)
                .orElseThrow(() -> new IllegalArgumentException("The container " + containerClass.getName() + " isn't registered."))
                .getImmutableManipulatorClass();
    }

    @Override
    public <T extends ImmutableDataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        return (Optional) this.cached.computeIfAbsent(containerClass,
                c -> this.collection.get(getMutableClass(c)).map(DataManipulator::asImmutable));
    }

    @Override
    public Map<Class<? extends ImmutableDataManipulator<?, ?>>, ? extends ImmutableDataManipulator<?, ?>> getMap() {
        this.collection.getAll().forEach(manipulator -> {
            final Class<? extends ImmutableDataManipulator<?, ?>> immutableClass = getImmutableClass(manipulator.getClass());
            this.cached.computeIfAbsent(immutableClass, c -> Optional.of(manipulator.asImmutable()));
        });
        return (Map) this.unmodifiableCached;
    }

    @Override
    public <T extends ImmutableDataManipulator<?, ?>> Optional<T> offer(T container) {
        throw new UnsupportedOperationException(); // Offering isn't supported
    }

    @Override
    public Collection<? extends ImmutableDataManipulator<?, ?>> getAll() {
        return getMap().values();
    }

    @Override
    public Optional<ImmutableDataManipulator<?, ?>> remove(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        throw new UnsupportedOperationException(); // Removing isn't supported
    }

    @Override
    public AdditionalContainerCollection<ImmutableDataManipulator<?, ?>> copy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AdditionalContainerCollection<ImmutableDataManipulator<?, ?>> copyAsConcurrent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AdditionalContainerCollection<ImmutableDataManipulator<?, ?>> copyAsNormal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R extends ValueContainer<?>> AdditionalContainerCollection<R> mapAndAsConcurrent(Function<ImmutableDataManipulator<?, ?>, R> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R extends ValueContainer<?>> AdditionalContainerCollection<R> mapAndAsNormal(Function<ImmutableDataManipulator<?, ?>, R> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R extends ValueContainer<?>> AdditionalContainerCollection<R> map(Function<ImmutableDataManipulator<?, ?>, R> function) {
        throw new UnsupportedOperationException();
    }

}
