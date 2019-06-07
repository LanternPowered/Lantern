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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import org.lanternpowered.api.util.ToStringHelper;
import org.lanternpowered.server.data.key.LanternKey;
import org.lanternpowered.server.data.processor.Processor;
import org.lanternpowered.server.data.processor.ValueProcessorKeyRegistration;
import org.lanternpowered.server.data.value.LanternImmutableValue;
import org.lanternpowered.server.data.value.LanternMutableValue;
import org.lanternpowered.server.data.value.LanternValueFactory;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.OptionalValue;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.data.value.ValueContainer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unchecked")
public interface IValueContainer<C extends ValueContainer<C>> extends ValueContainer<C>, IValueHolder {

    /**
     * Converts the {@link Value}s of the {@link ValueContainer} into a nicely
     * formatted {@code String}.
     *
     * @param valueContainer The value container
     * @return The string
     */
    static String valuesToString(ValueContainer<?> valueContainer) {
        return valuesToString(valueContainer.getValues());
    }

    /**
     * Converts the {@link Value}s into a nicely
     * formatted {@code String}.
     *
     * @param values The values
     * @return The string
     */
    static String valuesToString(Iterable<? extends Value<?>> values) {
        return Arrays.toString(Streams.stream(values)
                .map(e -> new ToStringHelper()
                        .add("key", e.getKey().getKey())
                        .add("value", e.get())
                        .toString())
                .toArray());
    }

    /**
     * Matches the contents of the two {@link ValueContainer}s.
     *
     * @param valueContainerA The first value container
     * @param valueContainerB The second value container
     * @return Whether the contents match
     */
    static boolean matchContents(IValueContainer<?> valueContainerA, IValueContainer<?> valueContainerB) {
        final boolean additional = valueContainerA instanceof IAdditionalCompositeValueStore;
        if (additional != valueContainerB instanceof IAdditionalCompositeValueStore) {
            return false;
        }

        final ValueCollection valueCollectionA = valueContainerA.getValueCollection();
        final ValueCollection valueCollectionB = valueContainerB.getValueCollection();

        final Collection<Key<?>> keysA = valueCollectionA.getKeys();
        final Collection<Key<?>> keysB = valueCollectionB.getKeys();

        // The same keys have to be present in both of the containers
        if (keysA.size() != keysB.size() || !keysA.containsAll(keysB)) {
            return false;
        }

        for (KeyRegistration<?,?> registration1 : valueCollectionA.getAll()) {
            final KeyRegistration registration2 = (KeyRegistration) valueCollectionB.get((Key) registration1.getKey()).get();
            // Get the values from both of the containers and match them
            final Object value1 = ((Processor) registration1).getFrom(valueContainerA).orElse(null);
            final Object value2 = ((Processor) registration2).getFrom(valueContainerB).orElse(null);
            if (!Objects.equals(value1, value2)) {
                return false;
            }
        }

        // Match additional containers
        if (additional) {
            final Map<Class<?>, ValueContainer<?>> mapA =
                    ((IAdditionalCompositeValueStore) valueContainerA).getAdditionalContainers().getMap();
            final Map<Class<?>, ValueContainer<?>> mapB =
                    ((IAdditionalCompositeValueStore) valueContainerB).getAdditionalContainers().getMap();
            if (mapA.size() != mapB.size() || !mapA.keySet().containsAll(mapB.keySet())) {
                return false;
            }
            for (Map.Entry<Class<?>, ValueContainer<?>> entry : mapA.entrySet()) {
                final ValueContainer<?> containerA = entry.getValue();
                final ValueContainer<?> containerB = mapB.get(entry.getKey());
                if (!Objects.equals(containerA, containerB)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    default <E, V extends Value<E>> Optional<V> getValue(Key<V> key) {
        return IValueHolder.super.getValueFor(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    default boolean supports(Key<?> key) {
        checkNotNull(key, "key");

        // Optional unwrapped key handling
        final LanternKey optionalWrappedKey = ((LanternKey) key).getOptionalWrappedKey();
        if (optionalWrappedKey != null) {
            return supports(optionalWrappedKey);
        }

        // Check the local key registration
        final KeyRegistration<?, ?> localKeyRegistration = (KeyRegistration<?, ?>) getValueCollection().get((Key) key).orElse(null);
        if (localKeyRegistration != null) {
            return ((Processor<Value<?>, ?>) localKeyRegistration).isApplicableTo(this);
        }

        // Check for a global registration
        final Optional<ValueProcessorKeyRegistration> globalRegistration = LanternValueFactory.get().getKeyRegistration((Key) key);
        if (globalRegistration.isPresent()) {
            return ((Processor<Value<?>, ?>) globalRegistration.get()).isApplicableTo(this);
        }

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            // Check for the custom value containers
            final AdditionalContainerCollection<?> containers = ((AdditionalContainerHolder<?>) this).getAdditionalContainers();
            for (ValueContainer<?> valueContainer : containers.getAll()) {
                if (valueContainer.supports(key)) {
                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    default <E> Optional<E> get(Key<? extends Value<E>> key) {
        checkNotNull(key, "key");

        // Optional unwrapped key handling
        final LanternKey optionalWrappedKey = ((LanternKey) key).getOptionalWrappedKey();
        if (optionalWrappedKey != null) {
            return (Optional<E>) get(optionalWrappedKey).get();
        }

        // Check the local key registration
        final KeyRegistration<Value<E>, E> localKeyRegistration = getValueCollection().get(key).orElse(null);
        if (localKeyRegistration != null) {
            return ((Processor<Value<E>, E>) localKeyRegistration).getFrom(this);
        }

        // Check for a global registration
        final Optional<ValueProcessorKeyRegistration<Value<E>, E>> globalRegistration = LanternValueFactory.get().getKeyRegistration(key);
        if (globalRegistration.isPresent()) {
            return ((Processor<Value<E>, E>) globalRegistration.get()).getFrom(this);
        }

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            // Check for the custom value containers
            final AdditionalContainerCollection<?> containers = ((AdditionalContainerHolder<?>) this).getAdditionalContainers();
            for (ValueContainer<?> valueContainer : containers.getAll()) {
                if (valueContainer.supports(key)) {
                    return valueContainer.get(key);
                }
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    default <E, V extends Value<E>> Optional<V> getRawMutableValueFor(Key<V> key) {
        // Optional unwrapped key handling
        final LanternKey optionalWrappedKey = ((LanternKey) key).getOptionalWrappedKey();
        if (optionalWrappedKey != null) {
            final Optional<Value> optOptionalValue = getRawMutableValueFor(optionalWrappedKey);
            if (!optOptionalValue.isPresent()) {
                return Optional.empty();
            }
            final Value baseValue = optOptionalValue.get();
            final Optional optElement = (Optional) baseValue.get();
            if (!optElement.isPresent()) {
                return Optional.empty();
            }
            final Object element = optElement.get();
            if (baseValue instanceof OptionalValue.Mutable) {
                return Optional.of((V) new LanternMutableValue(key, element));
            } else {
                return Optional.of((V) LanternImmutableValue.cachedOf((Key) key, element));
            }
        }

        // Check the local key registration
        final KeyRegistration<Value<E>, E> localKeyRegistration = getValueCollection().get(key).orElse(null);
        if (localKeyRegistration != null) {
            return ((Processor<V, E>) localKeyRegistration).getValueFrom(this);
        }

        // Check for a global registration
        final Optional<ValueProcessorKeyRegistration<V, E>> globalRegistration = LanternValueFactory.get().getKeyRegistration(key);
        if (globalRegistration.isPresent()) {
            return ((Processor<V, E>) globalRegistration.get()).getValueFrom(this);
        }

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            // Check for the custom value containers
            final AdditionalContainerCollection<?> containers = ((AdditionalContainerHolder<?>) this).getAdditionalContainers();
            for (ValueContainer<?> valueContainer : containers.getAll()) {
                if (valueContainer.supports(key)) {
                    return valueContainer.getValue(key);
                }
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    default Set<Key<?>> getKeys() {
        final ImmutableSet.Builder<Key<?>> keys = ImmutableSet.builder();

        // Check local registrations
        keys.addAll(getValueCollection().getKeys());

        // Check for global registrations
        LanternValueFactory.get().getKeyRegistrations().stream()
                .filter(registration -> ((Processor<Value<?>, ?>) registration).isApplicableTo(this))
                .forEach(registration -> keys.add(registration.getKey()));

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            final AdditionalContainerCollection<?> containers = ((AdditionalContainerHolder<?>) this).getAdditionalContainers();
            containers.getAll().forEach(manipulator -> keys.addAll(manipulator.getKeys()));
        }

        return keys.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    default Set<Value.Immutable<?>> getValues() {
        final ImmutableSet.Builder<Value.Immutable<?>> values = ImmutableSet.builder();

        // Check local registrations
        for (KeyRegistration<?,?> entry : getValueCollection().getAll()) {
            final Key key = entry.getKey();
            final Optional<Value> optValue = getValue(key);
            optValue.ifPresent(baseValue -> values.add(baseValue.asImmutable()));
        }

        // Check for global registrations
        for (ValueProcessorKeyRegistration<?,?> registration : LanternValueFactory.get().getKeyRegistrations()) {
            final Optional<Value> optValue = ((Processor) registration).getValueFrom(this);
            optValue.ifPresent(baseValue -> values.add(baseValue.asImmutable()));
        }

        // Check if custom data is supported by this container
        if (this instanceof AdditionalContainerHolder) {
            final AdditionalContainerCollection<?> containers = ((AdditionalContainerHolder<?>) this).getAdditionalContainers();
            containers.getAll().forEach(manipulator -> values.addAll(manipulator.getValues()));
        }

        return values.build();
    }

    /**
     * Gets the {@link ValueCollection}.
     *
     * @return The value collection
     */
    ValueCollection getValueCollection();
}
