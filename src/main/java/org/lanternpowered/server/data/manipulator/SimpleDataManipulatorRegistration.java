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
package org.lanternpowered.server.data.manipulator;

import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

final class SimpleDataManipulatorRegistration<M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>>
        extends AbstractDataManipulatorRegistration<M, I> {

    private final Supplier<M> manipulatorSupplier;
    private final Function<M, M> manipulatorCopyFunction;
    private final Function<I, M> immutableToMutableFunction;
    private final Supplier<I> immutableManipulatorSupplier;
    private final Function<M, I> mutableToImmutableFunction;

    SimpleDataManipulatorRegistration(PluginContainer plugin, String id, String name,
            Class<M> manipulatorClass, Supplier<M> manipulatorSupplier, Function<M, M> manipulatorCopyFunction, Function<I, M> immutableToMutableFunction,
            Class<I> immutableClass, Supplier<I> immutableManipulatorSupplier, Function<M, I> mutableToImmutableFunction,
            Set<Key<?>> requiredKeys) {
        super(plugin, id, name, manipulatorClass, immutableClass, requiredKeys);
        this.manipulatorSupplier = manipulatorSupplier;
        this.manipulatorCopyFunction = manipulatorCopyFunction;
        this.immutableToMutableFunction = immutableToMutableFunction;
        this.immutableManipulatorSupplier = immutableManipulatorSupplier;
        this.mutableToImmutableFunction = mutableToImmutableFunction;
        validate();
    }

    @Override
    public M createMutable() {
        return this.manipulatorSupplier.get();
    }

    @Override
    public I createImmutable() {
        return this.immutableManipulatorSupplier.get();
    }

    @Override
    public M copyMutable(M manipulator) {
        return this.manipulatorCopyFunction.apply(manipulator);
    }

    @Override
    public M toMutable(I manipulator) {
        return this.immutableToMutableFunction.apply(manipulator);
    }

    @Override
    public I toImmutable(M manipulator) {
        return this.mutableToImmutableFunction.apply(manipulator);
    }
}
