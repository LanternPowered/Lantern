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

import org.lanternpowered.server.data.manipulator.DataManipulatorRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.value.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public interface IImmutableValueHolder extends IValueHolder {

    @Override
    default <E, V extends Value<E>> Optional<V> getValueFor(Key<V> key) {
        return IValueHolder.super.getValueFor(key);
    }

    /**
     * Attempts to get a {@link Value.Immutable} for the given
     * {@link Key}. An exception will be thrown when it fails.
     *
     * @param key The key
     * @return The immutable value
     */
    default <E, R extends Value.Immutable<E>> R tryGetImmutableValueFor(Key<? extends Value<E>> key) {
        return (R) getImmutableValueFor(key).orElseThrow(() -> new IllegalArgumentException("The key " + key + " isn't present!"));
    }

    /**
     * Attempts to get a {@link Value.Immutable} for the given
     * {@link Key}. {@link Optional#empty()} will be returned
     * when it fails.
     *
     * @param key The key
     * @return The immutable value, if success
     */
    default <E, R extends Value.Immutable<E>> Optional<R> getImmutableValueFor(Key<? extends Value<E>> key) {
        checkNotNull(key, "key");
        final ImmutableContainerCache cache = getContainerCache();
        if (cache != null) {
            Object value = cache.values.get(key);
            if (value != null) {
                return value == ImmutableContainerCache.NONE ? Optional.empty() : Optional.of((R) value);
            }
        }
        final Optional optValue = getRawImmutableValueFor((Key<Value>) key);
        if (cache != null) {
            cache.values.put(key, optValue.orElse(ImmutableContainerCache.NONE));
        }
        return optValue;
    }

    /**
     * A {@link ImmutableContainerCache} may be provided to allow the immutable containers
     * and values of this {@link IImmutableDataHolderBase} to be cached.
     *
     * @return The container cache
     */
    @Nullable
    default ImmutableContainerCache getContainerCache() {
        return null;
    }

    final class ImmutableContainerCache {

        // A object that represents that the container isn't present on the holder,
        // null means that it wasn't being retrieved before
        static final Object NONE = new Object();

        @Nullable Map<Class<?>, Object> manipulators;
        final Map<Key<?>, Object> values = new HashMap<>();

        // Doesn't matter in which sub class it is, just not directly in the interface,
        // these methods shouldn't be exposed

        @SuppressWarnings({"unchecked", "ConstantConditions"})
        static <I extends ImmutableDataManipulator<I, M>, M extends DataManipulator<M, I>> Optional<I> get(
                IImmutableDataHolderBase<?> dataHolder, DataManipulatorRegistration<M, I> registration, Class<?> containerClass) {
            final DataManipulator manipulator = registration.createMutable();
            final ImmutableContainerCache cache = dataHolder.getContainerCache();
            if (cache != null && cache.manipulators == null) {
                cache.manipulators = new HashMap<>();
            }
            for (Key key : registration.getRequiredKeys()) {
                final Optional value = dataHolder.getValue(key);
                if (!value.isPresent()) {
                    if (cache != null) {
                        cache.manipulators.put(containerClass, ImmutableContainerCache.NONE);
                    }
                    return Optional.empty();
                }
                manipulator.set(key, value.get());
            }
            final I immutable = (I) manipulator.asImmutable();
            if (cache != null) {
                cache.manipulators.put(containerClass, immutable);
                // In case they are different, unlikely
                if (registration.getImmutableManipulatorClass() != containerClass) {
                    cache.manipulators.put(registration.getImmutableManipulatorClass(), immutable);
                }
            }
            return Optional.of(immutable);
        }
    }
}
