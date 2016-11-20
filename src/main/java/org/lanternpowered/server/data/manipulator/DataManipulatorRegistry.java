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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.data.manipulator.immutable.IImmutableDataManipulator;
import org.lanternpowered.server.data.manipulator.immutable.LanternImmutableColoredData;
import org.lanternpowered.server.data.manipulator.immutable.LanternImmutableCommandData;
import org.lanternpowered.server.data.manipulator.mutable.IDataManipulator;
import org.lanternpowered.server.data.manipulator.mutable.LanternColoredData;
import org.lanternpowered.server.data.manipulator.mutable.LanternCommandData;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.immutable.ImmutableColoredData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableCommandData;
import org.spongepowered.api.data.manipulator.mutable.ColoredData;
import org.spongepowered.api.data.manipulator.mutable.CommandData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class DataManipulatorRegistry {

    {
        register(ColoredData.class, LanternColoredData::new, ImmutableColoredData.class, LanternImmutableColoredData::new);
        register(CommandData.class, LanternCommandData::new, ImmutableCommandData.class, LanternImmutableCommandData::new);
    }

    private final Map<Class, DataManipulatorRegistration> registrationByClass = new HashMap<>();

    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> DataManipulatorRegistration<M, I> register(
            Class<M> manipulatorType, Supplier<M> manipulatorSupplier,
            Class<I> immutableManipulatorType, Supplier<I> immutableManipulatorSupplier) {
        checkNotNull(manipulatorType, "manipulatorType");
        checkNotNull(manipulatorSupplier, "manipulatorSupplier");
        checkNotNull(immutableManipulatorType, "immutableManipulatorType");
        checkNotNull(immutableManipulatorSupplier, "immutableManipulatorSupplier");
        final M manipulator = manipulatorSupplier.get();
        checkArgument(manipulator instanceof AbstractData,
                "The mutable manipulator implementation must implement IDataManipulator.");
        //noinspection unchecked
        final Class<M> manipulatorType1 = ((IDataManipulator<M, I>) manipulator).getMutableType();
        checkArgument(manipulatorType1 == manipulatorType,
                "The mutable data manipulator returns a different manipulator type, expected %s, but got %s",
                manipulatorType, manipulatorType1);
        final I immutableManipulator = immutableManipulatorSupplier.get();
        checkArgument(immutableManipulator instanceof IImmutableDataManipulator,
                "The immutable manipulator implementation must implement IImmutableData.");
        //noinspection unchecked
        final Class<I> immutableManipulatorType1 = ((IImmutableDataManipulator<I, M>) immutableManipulator).getImmutableType();
        checkArgument(immutableManipulatorType1 == immutableManipulatorType,
                "The mutable data manipulator returns a different manipulator type, expected %s, but got %s",
                immutableManipulatorType, immutableManipulatorType1);
        final DataManipulatorRegistration<M, I> registration = new DataManipulatorRegistration<>(
                manipulatorType, manipulatorSupplier, immutableManipulatorType, immutableManipulatorSupplier);
        this.registrationByClass.put(manipulatorType, registration);
        this.registrationByClass.put(immutableManipulatorType, registration);
        return registration;
    }

    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> Optional<DataManipulatorRegistration<M, I>> getByMutable(
            Class<M> manipulatorType) {
        //noinspection unchecked
        return Optional.ofNullable(this.registrationByClass.get(checkNotNull(manipulatorType, "manipulatorType")));
    }

    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> Optional<DataManipulatorRegistration<M, I>> getByImmutable(
            Class<I> immutableManipulatorType) {
        //noinspection unchecked
        return Optional.ofNullable(this.registrationByClass.get(checkNotNull(immutableManipulatorType, "immutableManipulatorType")));
    }
}
