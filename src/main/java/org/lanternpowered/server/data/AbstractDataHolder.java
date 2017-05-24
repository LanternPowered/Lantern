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

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistration;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistry;
import org.lanternpowered.server.data.manipulator.mutable.IDataManipulator;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.data.value.mutable.AbstractCompositeValueStore;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.event.cause.Cause;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface AbstractDataHolder extends AbstractCompositeValueStore<DataHolder, DataManipulator<?,?>>, DataHolder, AbstractPropertyHolder {

    @SuppressWarnings("unchecked")
    @Override
    default <T extends DataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        // Check default registrations
        final Optional<DataManipulatorRegistration> optRegistration = DataManipulatorRegistry.get().getByMutable((Class) containerClass);
        if (optRegistration.isPresent()) {
            final DataManipulatorRegistration registration = optRegistration.get();
            final T manipulator = (T) optRegistration.get().getManipulatorSupplier().get();
            for (Key key : (Set<Key>) registration.getRequiredKeys()) {
                final Optional value = getValue(key);
                if (!value.isPresent()) {
                    return Optional.empty();
                }
                manipulator.set(key, value.get());
            }
            return Optional.of(manipulator);
        }

        // Try the additional manipulators if they are supported
        final Map<Class<?>, DataManipulator<?, ?>> manipulators = getRawAdditionalContainers();
        if (manipulators != null) {
            for (DataManipulator<?, ?> manipulator : manipulators.values()) {
                if (containerClass.isInstance(manipulator)) {
                    return Optional.of((T) manipulator.copy());
                }
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    default <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        // Offer all the default key values as long if they are supported
        final Optional<DataManipulatorRegistration> optRegistration = DataManipulatorRegistry.get().getByMutable((Class) containerClass);
        if (optRegistration.isPresent()) {
            final DataManipulatorRegistration registration = optRegistration.get();
            final T manipulator = (T) optRegistration.get().getImmutableManipulatorSupplier().get();
            for (Key key : (Set<Key>) registration.getRequiredKeys()) {
                final Optional value = getValue(key);
                if (value.isPresent()) {
                    manipulator.set(key, value.get());
                } else if (!supports(key)) {
                    return Optional.empty();
                }
            }
            return Optional.of(manipulator);
        }

        // Try the additional manipulators if they are supported,
        // we cannot create the additional manipulators through this method
        final Map<Class<?>, DataManipulator<?, ?>> manipulators = getRawAdditionalContainers();
        if (manipulators != null) {
            for (DataManipulator<?, ?> manipulator : manipulators.values()) {
                if (containerClass.isInstance(manipulator)) {
                    return Optional.of((T) manipulator.copy());
                }
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    default boolean supports(Class<? extends DataManipulator<?, ?>> holderClass) {
        if (holderClass.isAssignableFrom(IDataManipulator.class)) {
            // Offer all the default key values as long if they are supported
            final Optional<DataManipulatorRegistration> optRegistration = DataManipulatorRegistry.get().getByMutable((Class) holderClass);
            if (optRegistration.isPresent()) {
                final DataManipulatorRegistration registration = optRegistration.get();
                for (Key key : (Set<Key>) registration.getRequiredKeys()) {
                    if (!supports(key)) {
                        return false;
                    }
                }
                return true;
            }
        }

        // Support all the additional manipulators
        return getRawAdditionalContainers() != null;
    }

    @Override
    default DataTransactionResult offer(DataManipulator<?, ?> valueContainer, MergeFunction function, Cause cause) {
        if (valueContainer instanceof IDataManipulator) {
            // Offer all the default key values as long if they are supported
            final Optional optRegistration = DataManipulatorRegistry.get().getByMutable(((IDataManipulator) valueContainer).getMutableType());
            if (optRegistration.isPresent()) {
                final DataTransactionResult.Builder builder = DataTransactionResult.builder();
                boolean success = false;
                for (ImmutableValue value : valueContainer.getValues()) {
                    final DataTransactionResult result = offer(value);
                    if (result.isSuccessful()) {
                        builder.success(value);
                        builder.replace(result.getReplacedData());
                        success = true;
                    } else {
                        builder.reject(value);
                    }
                }
                if (success) {
                    builder.result(DataTransactionResult.Type.SUCCESS);
                } else {
                    builder.result(DataTransactionResult.Type.FAILURE);
                }
                return builder.build();
            }
        }

        final Map<Class<?>, DataManipulator<?, ?>> manipulators = getRawAdditionalContainers();
        if (manipulators != null) {
            final Class<?> key = valueContainer.getClass();
            final DataManipulator<?, ?> old = manipulators.put(key, valueContainer);

            final DataTransactionResult.Builder builder = DataTransactionResult.builder().result(DataTransactionResult.Type.SUCCESS);
            builder.success(valueContainer.getValues());
            if (old != null) {
                builder.replace(old.getValues());
            }
            return builder.build();
        }

        return DataTransactionResult.failNoData();
    }

    @Override
    default DataTransactionResult remove(Class<? extends DataManipulator<?, ?>> containerClass) {
        if (containerClass.isAssignableFrom(IDataManipulator.class)) {
            // You cannot remove default data manipulators?
            final Optional optRegistration = DataManipulatorRegistry.get().getByMutable((Class) containerClass);
            if (optRegistration.isPresent()) {
                return DataTransactionResult.failNoData();
            }
        }

        final Map<Class<?>, DataManipulator<?, ?>> manipulators = getRawAdditionalContainers();
        if (manipulators != null) {
            final DataManipulator<?, ?> old = manipulators.remove(containerClass);
            if (old != null) {
                return DataTransactionResult.successRemove(old.getValues());
            }
        }

        return DataTransactionResult.failNoData();
    }

    @Override
    default DataTransactionResult copyFrom(DataHolder that, MergeFunction function) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    default Collection<DataManipulator<?, ?>> getContainers() {
        final ImmutableList.Builder<DataManipulator<?, ?>> builder = ImmutableList.builder();
        for (DataManipulatorRegistration registration : DataManipulatorRegistry.get().getAll()) {
            DataManipulator manipulator = (DataManipulator<?, ?>) registration.getImmutableManipulatorSupplier().get();
            for (Key key : (Set<Key>) registration.getRequiredKeys()) {
                final Optional value = getValue(key);
                if (value.isPresent()) {
                    manipulator.set(key, value.get());
                } else if (!supports(key)) {
                    manipulator = null;
                    break;
                }
            }
            if (manipulator != null) {
                builder.add(manipulator);
            }
        }

        // Try the additional manipulators if they are supported
        final Map<Class<?>, DataManipulator<?, ?>> manipulators = getRawAdditionalContainers();
        if (manipulators != null) {
            manipulators.values().forEach(manipulator -> builder.add(manipulator.copy()));
        }

        return builder.build();
    }

    @Override
    default DataContainer toContainer() {
        return DataContainer.createNew();
    }
}
