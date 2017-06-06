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
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unchecked")
public interface IDataHolder extends DataHolder, ICompositeValueStore<DataHolder, DataManipulator<?, ?>>, AbstractPropertyHolder {

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

    @SuppressWarnings("unchecked")
    @Override
    default DataTransactionResult copyFrom(DataHolder that, MergeFunction function) {
        final Collection<DataManipulator<?, ?>> containers = that.getContainers();
        final DataTransactionResult.Builder builder = DataTransactionResult.builder();
        boolean success = false;
        for (DataManipulator<?, ?> thatContainer : containers) {
            final DataManipulator<?, ?> thisContainer = get(thatContainer.getClass()).orElse(null);
            final DataManipulator<?, ?> merged = function.merge(thisContainer, thatContainer);
            final DataTransactionResult result = offer(merged);
            builder.absorbResult(result);
            if (!result.getSuccessfulData().isEmpty()) {
                success = true;
            }
        }
        return builder.result(success ? DataTransactionResult.Type.SUCCESS : DataTransactionResult.Type.FAILURE).build();
    }

    @Override
    default boolean removeFast(Class<? extends DataManipulator<?,?>> containerClass) {
        checkNotNull(containerClass, "containerClass");
        // You cannot remove default data manipulators?
        final Optional optRegistration = DataManipulatorRegistry.get().getBy(containerClass);
        if (optRegistration.isPresent()) {
            return false;
        }

        if (this instanceof AdditionalContainerHolder) {
            final AdditionalContainerCollection<DataManipulator<?,?>> containers =
                    ((AdditionalContainerHolder<DataManipulator<?,?>>) this).getAdditionalContainers();
            final Optional<DataManipulator<?,?>> old = containers.remove(containerClass);
            if (old.isPresent()) {
                return true;
            }
        }

        return false;
    }

    @Override
    default DataTransactionResult remove(Class<? extends DataManipulator<?,?>> containerClass) {
        checkNotNull(containerClass, "containerClass");
        // You cannot remove default data manipulators?
        final Optional optRegistration = DataManipulatorRegistry.get().getBy(containerClass);
        if (optRegistration.isPresent()) {
            return DataTransactionResult.failNoData();
        }

        if (this instanceof AdditionalContainerHolder) {
            final AdditionalContainerCollection<DataManipulator<?,?>> containers =
                    ((AdditionalContainerHolder<DataManipulator<?,?>>) this).getAdditionalContainers();
            final Optional<DataManipulator<?,?>> old = containers.remove(containerClass);
            if (old.isPresent()) {
                return DataTransactionResult.successRemove(old.get().getValues());
            }
        }

        return DataTransactionResult.failNoData();
    }

    @Override
    default boolean supports(Class<? extends DataManipulator<?,?>> containerClass) {
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

        // Support all the additional manipulators
        return this instanceof AdditionalContainerHolder;
    }

    @Override
    default <T extends DataManipulator<?,?>> Optional<T> get(Class<T> containerClass) {
        checkNotNull(containerClass, "containerClass");

        // Check default registrations
        final Optional<DataManipulatorRegistration> optRegistration = DataManipulatorRegistry.get().getBy(containerClass);
        if (optRegistration.isPresent()) {
            final DataManipulator manipulator = DataHelper.create(this, optRegistration.get());
            return manipulator == null ? Optional.empty() : Optional.of(
                    (T) (ImmutableDataManipulator.class.isAssignableFrom(containerClass) ? manipulator.asImmutable() : manipulator));
        }

        // Try the additional containers if they are supported
        if (this instanceof AdditionalContainerHolder) {
            final AdditionalContainerCollection<DataManipulator<?,?>> containers =
                    ((AdditionalContainerHolder<DataManipulator<?,?>>) this).getAdditionalContainers();
            return containers.get(containerClass);
        }

        return Optional.empty();
    }

    @Override
    default Collection<DataManipulator<?, ?>> getContainers() {
        final ImmutableList.Builder<DataManipulator<?, ?>> builder = ImmutableList.builder();
        for (DataManipulatorRegistration registration : DataManipulatorRegistry.get().getAll()) {
            final DataManipulator manipulator = DataHelper.create(this, registration);
            if (manipulator != null) {
                builder.add(manipulator);
            }
        }

        // Try the additional manipulators if they are supported
        if (this instanceof AdditionalContainerHolder) {
            final AdditionalContainerCollection<DataManipulator<?,?>> containers =
                    ((AdditionalContainerHolder<DataManipulator<?,?>>) this).getAdditionalContainers();
            containers.getAll().forEach(manipulator -> builder.add(manipulator.copy()));
        }

        return builder.build();
    }
}
