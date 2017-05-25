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
import org.lanternpowered.server.data.manipulator.immutable.IImmutableDataManipulator;
import org.lanternpowered.server.data.value.immutable.AbstractImmutableValueStore;
import org.spongepowered.api.data.ImmutableDataHolder;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface AbstractImmutableDataHolder<H extends ImmutableDataHolder<H>> extends
        AbstractImmutableValueStore<H, ImmutableDataManipulator<?, ?>>, ImmutableDataHolder<H> {

    @SuppressWarnings("unchecked")
    @Override
    default <T extends ImmutableDataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        // Check default registrations
        final Optional<DataManipulatorRegistration> optRegistration = DataManipulatorRegistry.get().getByImmutable((Class) containerClass);
        if (optRegistration.isPresent()) {
            final DataManipulatorRegistration registration = optRegistration.get();
            final DataManipulator manipulator = (DataManipulator) optRegistration.get().getManipulatorSupplier().get();
            for (Key key : (Set<Key>) registration.getRequiredKeys()) {
                final Optional value = getValue(key);
                if (!value.isPresent()) {
                    return Optional.empty();
                }
                manipulator.set(key, value.get());
            }
            return Optional.of((T) manipulator.asImmutable());
        }

        // Try the additional manipulators if they are supported
        final Map<Class<?>, ImmutableDataManipulator<?, ?>> manipulators = getRawAdditionalContainers();
        if (manipulators != null) {
            for (ImmutableDataManipulator<?, ?> manipulator : manipulators.values()) {
                if (containerClass.isInstance(manipulator)) {
                    return Optional.of((T) manipulator);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    default <T extends ImmutableDataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        return get(containerClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    default boolean supports(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        if (containerClass.isAssignableFrom(IImmutableDataManipulator.class)) {
            // Offer all the default key values as long if they are supported
            final Optional<DataManipulatorRegistration> optRegistration = DataManipulatorRegistry.get().getByImmutable((Class) containerClass);
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

    @SuppressWarnings("unchecked")
    @Override
    default List<ImmutableDataManipulator<?, ?>> getManipulators() {
        final ImmutableList.Builder<ImmutableDataManipulator<?, ?>> builder = ImmutableList.builder();
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
                builder.add(manipulator.asImmutable());
            }
        }

        // Try the additional manipulators if they are supported
        final Map<Class<?>, ImmutableDataManipulator<?, ?>> manipulators = getRawAdditionalContainers();
        if (manipulators != null) {
            manipulators.values().forEach(builder::add);
        }

        return builder.build();
    }

    @Override
    default List<ImmutableDataManipulator<?, ?>> getContainers() {
        return getManipulators();
    }
}
