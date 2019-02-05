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
import org.spongepowered.api.data.ImmutableDataHolder;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public interface IImmutableDataHolderBase<H extends ImmutableDataHolder<H>> extends ImmutableDataHolder<H>, IImmutableValueHolder {

    @Override
    default int getContentVersion() {
        return 1;
    }

    @Override
    default H copy() {
        return (H) this;
    }

    @Override
    default <E, V extends Value<E>> Optional<V> getValue(Key<V> key) {
        return IImmutableValueHolder.super.getValueFor(key);
    }

    @Override
    default boolean supports(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        // Offer all the default key values as long if they are supported
        // Support all the additional manipulators
        return DataManipulatorRegistry.get().getBy(containerClass)
                .map(registration -> DataHelper.supports(this, registration))
                .orElse(this instanceof AdditionalContainerHolder);
    }

    @Override
    default <T extends ImmutableDataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        final ImmutableContainerCache cache = getContainerCache();
        if (cache != null) {
            if (cache.manipulators == null) {
                cache.manipulators = new HashMap<>();
            }
            final Object container = cache.manipulators.get(containerClass);
            if (container != null) {
                return container == ImmutableContainerCache.NONE ? Optional.empty() : Optional.of((T) container);
            }
        }

        // Check default registrations
        final Optional<DataManipulatorRegistration> optRegistration = DataManipulatorRegistry.get().getByImmutable((Class) containerClass);
        if (optRegistration.isPresent()) {
            return ImmutableContainerCache.get(this, optRegistration.get(), containerClass);
        }

        if (this instanceof AdditionalContainerHolder) {
            // Try the additional manipulators if they are supported
            final AdditionalContainerCollection<ImmutableDataManipulator<?, ?>> manipulators =
                    ((AdditionalContainerHolder<ImmutableDataManipulator<?, ?>>) this).getAdditionalContainers();
            for (ImmutableDataManipulator<?, ?> manipulator : manipulators.getAll()) {
                if (containerClass.isInstance(manipulator)) {
                    if (cache != null) {
                        cache.manipulators.put(containerClass, manipulator);
                    }
                    return Optional.of((T) manipulator);
                }
            }
            if (cache != null) {
                cache.manipulators.put(containerClass, ImmutableContainerCache.NONE);
            }
        }

        return Optional.empty();
    }

    @Override
    default <T extends ImmutableDataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        return get(containerClass);
    }

    @Override
    default List<ImmutableDataManipulator<?, ?>> getManipulators() {
        return getContainers();
    }

    @Override
    default List<ImmutableDataManipulator<?, ?>> getContainers() {
        final ImmutableList.Builder<ImmutableDataManipulator<?, ?>> builder = ImmutableList.builder();
        for (DataManipulatorRegistration registration : DataManipulatorRegistry.get().getAll()) {
            final ImmutableDataManipulator manipulator = (ImmutableDataManipulator) ImmutableContainerCache.get(
                    this, registration, registration.getImmutableManipulatorClass()).orElse(null);
            if (manipulator != null) {
                builder.add(manipulator);
            }
        }

        if (this instanceof AdditionalContainerHolder) {
            final AdditionalContainerCollection<ImmutableDataManipulator<?, ?>> manipulators =
                    ((AdditionalContainerHolder<ImmutableDataManipulator<?, ?>>) this).getAdditionalContainers();
            manipulators.getAll().forEach(builder::add);
        }

        return builder.build();
    }

    @Override
    default Optional<H> with(Value<?> value) {
        return with((Key) value.getKey(), value.get());
    }

    @Override
    default H merge(H that) {
        return merge(that, MergeFunction.IGNORE_ALL);
    }
}
