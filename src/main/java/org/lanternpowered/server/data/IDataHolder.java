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

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistration;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistry;
import org.lanternpowered.server.data.property.IStorePropertyHolder;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unchecked")
public interface IDataHolder extends DataHolder, ICompositeValueStore<DataHolder, DataManipulator>, IStorePropertyHolder {

    @Override
    default DataHolder copy() {
        return this;
    }

    @Override
    default int getContentVersion() {
        return 1;
    }

    @Override
    default void setRawData(DataView dataView) throws InvalidDataException {
        DataHelper.deserializeRawData(dataView, this);
    }

    @Override
    default DataContainer toContainer() {
        final DataContainer dataContainer = DataContainer.createNew();
        DataHelper.serializeRawData(dataContainer, this);
        return dataContainer;
    }

    // TODO: Support event? Would require special handling to restore the container
    @Override
    default boolean removeFast(Class<? extends DataManipulator> containerClass) {
        checkNotNull(containerClass, "containerClass");
        // You cannot remove default data manipulators?
        final Optional optRegistration = DataManipulatorRegistry.get().getBy(containerClass);
        return !optRegistration.isPresent() && ICompositeValueStore.super.removeFast(containerClass);
    }

    // TODO: Support event? Would require special handling to restore the container
    @Override
    default DataTransactionResult remove(Class<? extends DataManipulator> containerClass) {
        checkNotNull(containerClass, "containerClass");
        // You cannot remove default data manipulators?
        final Optional optRegistration = DataManipulatorRegistry.get().getBy(containerClass);
        if (optRegistration.isPresent()) {
            return DataTransactionResult.failNoData();
        }
        return ICompositeValueStore.super.remove(containerClass);
    }

    @Override
    default boolean supports(Class<? extends DataManipulator> containerClass) {
        checkNotNull(containerClass, "containerClass");
        // Offer all the default key values as long if they are supported
        final Optional<DataManipulatorRegistration> optRegistration = DataManipulatorRegistry.get().getBy(containerClass);
        if (optRegistration.isPresent()) {
            final DataManipulatorRegistration registration = optRegistration.get();
            for (Key key : (Set<Key>) registration.getRequiredKeys()) {
                if (!supports(key)) {
                    return false;
                }
            }
            return true;
        }
        return ICompositeValueStore.super.supports(containerClass);
    }

    @Override
    default <T extends DataManipulator> Optional<T> get(Class<T> containerClass) {
        checkNotNull(containerClass, "containerClass");
        // Check default registrations
        final Optional<DataManipulatorRegistration> optRegistration = DataManipulatorRegistry.get().getBy(containerClass);
        if (optRegistration.isPresent()) {
            final DataManipulator manipulator = DataHelper.create(this, optRegistration.get());
            return manipulator == null ? Optional.empty() : Optional.of(
                    (T) (ImmutableDataManipulator.class.isAssignableFrom(containerClass) ? manipulator.asImmutable() : manipulator));
        }
        return ICompositeValueStore.super.get(containerClass);
    }

    @Override
    default Collection<DataManipulator> getContainers() {
        final ImmutableList.Builder<DataManipulator> builder = ImmutableList.builder();
        for (DataManipulatorRegistration registration : DataManipulatorRegistry.get().getAll()) {
            final DataManipulator manipulator = DataHelper.create(this, registration);
            if (manipulator != null) {
                builder.add(manipulator);
            }
        }
        builder.addAll(ICompositeValueStore.super.getContainers());
        return builder.build();
    }
}
