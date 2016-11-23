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

import com.google.common.base.MoreObjects;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public final class DataManipulatorRegistration<M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> {

    private final Class<M> manipulatorType;
    private final Supplier<M> manipulatorSupplier;
    private final Function<M, M> manipulatorCopyFunction;
    private final Function<I, M> immutableToMutableFunction;
    private final Class<I> immutableManipulatorType;
    private final Supplier<I> immutableManipulatorSupplier;
    private final Function<M, I> mutableToImmutableFunction;
    private final Set<Key<?>> requiredKeys;

    DataManipulatorRegistration(
            Class<M> manipulatorType, Supplier<M> manipulatorSupplier, Function<M, M> manipulatorCopyFunction,
            Function<I, M> immutableToMutableFunction,
            Class<I> immutableManipulatorType, Supplier<I> immutableManipulatorSupplier, Function<M, I> mutableToImmutableFunction,
            Set<Key<?>> requiredKeys) {
        this.manipulatorType = manipulatorType;
        this.manipulatorSupplier = manipulatorSupplier;
        this.manipulatorCopyFunction = manipulatorCopyFunction;
        this.immutableToMutableFunction = immutableToMutableFunction;
        this.immutableManipulatorType = immutableManipulatorType;
        this.immutableManipulatorSupplier = immutableManipulatorSupplier;
        this.mutableToImmutableFunction = mutableToImmutableFunction;
        this.requiredKeys = requiredKeys;
    }

    public Class<M> getManipulatorType() {
        return this.manipulatorType;
    }

    public Supplier<M> getManipulatorSupplier() {
        return this.manipulatorSupplier;
    }

    public Class<I> getImmutableManipulatorType() {
        return this.immutableManipulatorType;
    }

    public Supplier<I> getImmutableManipulatorSupplier() {
        return this.immutableManipulatorSupplier;
    }

    public Function<M, M> getManipulatorCopyFunction() {
        return this.manipulatorCopyFunction;
    }

    public Function<I, M> getImmutableToMutableFunction() {
        return this.immutableToMutableFunction;
    }

    public Function<M, I> getMutableToImmutableFunction() {
        return this.mutableToImmutableFunction;
    }

    public Set<Key<?>> getRequiredKeys() {
        return this.requiredKeys;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("manipulatorType", this.manipulatorType.getName())
                .add("immutableManipulatorType", this.immutableManipulatorType.getName())
                .toString();
    }
}
