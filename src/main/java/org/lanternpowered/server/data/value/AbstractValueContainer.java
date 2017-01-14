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
package org.lanternpowered.server.data.value;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.data.value.processor.ValueProcessor;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.BoundedValue;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableBoundedValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public interface AbstractValueContainer<C extends ValueContainer<C>, H extends ValueContainer<?>> extends IValueContainer<C> {

    /**
     * Some {@link ValueProcessor}'s may want to depend on the fact that
     * a specific {@link Key} is applicable or attached to a {@link ValueContainer}.
     *
     * This class will represent a {@link Value} that is attached to a
     * specific {@link ValueContainer}.
     */
    class ElementHolderKeyRegistrationImpl<V extends BaseValue<E>, E> extends SimpleKeyRegistration.SingleProcessor<V, E> implements ElementHolderKeyRegistration<V, E> {

        @Nullable private List<Listener<E>> listeners;
        @Nullable private E value;

        ElementHolderKeyRegistrationImpl(Key<? extends V> key) {
            super(key);
        }

        @Nullable
        @Override
        public synchronized E set(@Nullable E value) {
            final E oldValue;
            synchronized (this) {
                oldValue = this.value;
                this.value = value;
            }
            if (this.listeners != null && !Objects.equals(oldValue, value)) {
                this.listeners.forEach(listener -> listener.accept(oldValue, value));
            }
            return oldValue;
        }

        @Nullable
        @Override
        public synchronized E get() {
            return this.value;
        }

        @Override
        public void addListener(Listener<E> listener) {
            if (this.listeners == null) {
                this.listeners = new ArrayList<>(1);
            }
            this.listeners.add(listener);
        }

        @Override
        public ElementHolderKeyRegistration<V, E> addValueProcessor(ValueProcessor<V, E> valueProcessor) {
            super.addValueProcessor(valueProcessor);
            return this;
        }

        @Override
        public ElementHolderKeyRegistration<V, E> applyValueProcessor(Consumer<ValueProcessor.Builder<V, E>> builderConsumer) {
            super.applyValueProcessor(builderConsumer);
            return this;
        }

        @Override
        public ElementHolderKeyRegistration<V, E> addElementChangeListener(ElementHolderChangeListener listener) {
            super.addElementChangeListener(listener);
            return this;
        }

        @Override
        public ElementHolderKeyRegistration<V, E> applyAttachedValueProcessor(
                Consumer<ValueProcessor.AttachedElementBuilder<V, E>> attachedElementBuilderConsumer) {
            final ValueProcessor.AttachedElementBuilder<V, E> builder = ValueProcessor.attachedElementBuilder();
            checkNotNull(attachedElementBuilderConsumer, "attachedElementBuilderConsumer").accept(builder);
            return addValueProcessor(builder.build());
        }
    }

    /**
     * Whether all the {@link Key}s in this container should be registered
     * before they can be offered/retrieved.
     *
     * @return requires key registration
     */
    default boolean requiresKeyRegistration() {
        return false;
    }

    /**
     * Gets the raw list with all the custom value containers,
     * this may be null if custom data isn't supported.
     *
     * @return The raw value containers
     */
    @Nullable
    default Map<Class<?>, H> getRawAdditionalContainers() {
        return null;
    }

    /**
     * Gets the internal map that is used to hold the value's by their key.
     *
     * @return the value by key map
     */
    Map<Key<?>, KeyRegistration> getRawValueMap();

    @SuppressWarnings("unchecked")
    @Nullable
    default <V extends BaseValue<E>, E> KeyRegistration<V, E> getKeyRegistration(Key<? extends BaseValue<E>> key) {
        return getRawValueMap().get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    default boolean supports(Key<?> key) {
        checkNotNull(key, "key");

        // Check the local key registration
        final KeyRegistration<?, ?> localKeyRegistration = getKeyRegistration((Key) key);
        if (localKeyRegistration == null) {
            if (requiresKeyRegistration()) {
                return false;
            }
        } else if (!localKeyRegistration.getValueProcessors().isEmpty()) {
            return true;
        }

        // Check the global key registrations
        final KeyRegistration<?, ?> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration((Key) key);
        if (keyRegistration != null) {
            for (ValueProcessor<?,?> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return true;
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return true;
        }

        // Check for the custom value containers
        final Map<Class<?>, H> valueContainers = getRawAdditionalContainers();
        // Custom data is supported by this container
        if (valueContainers != null) {
            for (H valueContainer : valueContainers.values()) {
                if (valueContainer.supports(key)) {
                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    default <E> Optional<E> getWith(Key<? extends BaseValue<E>> key, ValueProcessor<BaseValue<E>, E> processor) {
        return (Optional) ((BiFunction) processor.getRetrieveHandler()).apply(key, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    default <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
        checkNotNull(key, "key");

        // Check the local key registration
        final KeyRegistration<BaseValue<E>, E> localKeyRegistration = getKeyRegistration(key);
        if (localKeyRegistration == null) {
            if (requiresKeyRegistration()) {
                return Optional.empty();
            }
        } else {
            final List<ValueProcessor<BaseValue<E>, E>> processors = localKeyRegistration.getValueProcessors();
            if (!processors.isEmpty()) {
                return getWith(key, processors.get(0));
            }
        }

        // Check the global key registrations
        final KeyRegistration<BaseValue<E>, E> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration(key);
        if (keyRegistration != null) {
            for (ValueProcessor<BaseValue<E>, E> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return getWith(key, valueProcessor);
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return getWith(key, ValueProcessor.getDefaultAttachedValueProcessor());
        }

        // Check for the custom value containers
        final Map<Class<?>, H> valueContainers = getRawAdditionalContainers();
        // Custom data is supported by this container
        if (valueContainers != null) {
            for (H valueContainer : valueContainers.values()) {
                if (valueContainer.supports(key)) {
                    return valueContainer.get(key);
                }
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    default <E, V extends BaseValue<E>> Optional<V> getValueWith(Key<? extends BaseValue<E>> key, ValueProcessor<BaseValue<E>, E> processor) {
        return (Optional) ((BiFunction) processor.getValueRetrieveHandler()).apply(key, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    default <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
        checkNotNull(key, "key");

        // Check the local key registration
        final KeyRegistration<BaseValue<E>, E> localKeyRegistration = getKeyRegistration(key);
        if (localKeyRegistration == null) {
            if (requiresKeyRegistration()) {
                return Optional.empty();
            }
        } else {
            final List<ValueProcessor<BaseValue<E>, E>> processors = localKeyRegistration.getValueProcessors();
            if (!processors.isEmpty()) {
                return getValueWith(key, processors.get(0));
            }
        }

        // Check the global key registrations
        final KeyRegistration<BaseValue<E>, E> keyRegistration = LanternValueFactory.getInstance().getKeyRegistration(key);
        if (keyRegistration != null) {
            for (ValueProcessor<BaseValue<E>, E> valueProcessor : keyRegistration.getValueProcessors()) {
                if (valueProcessor.getApplicableTester().test((Key) key, this)) {
                    return getValueWith(key, valueProcessor);
                }
            }
        }

        // Use the global processor
        if (localKeyRegistration != null && localKeyRegistration instanceof ElementHolder) {
            return getValueWith(key, ValueProcessor.getDefaultAttachedValueProcessor());
        }

        // Check for the custom data manipulators
        final Map<Class<?>, H> valueContainers = getRawAdditionalContainers();
        // Custom data is supported by this container
        if (valueContainers != null) {
            for (H valueContainer : valueContainers.values()) {
                if (valueContainer.supports(key)) {
                    return valueContainer.getValue(key);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    default Set<Key<?>> getKeys() {
        final ImmutableSet.Builder<Key<?>> keys = ImmutableSet.builder();
        keys.addAll(getRawValueMap().keySet());
        final Map<Class<?>, ? extends H> manipulators = getRawAdditionalContainers();
        if (manipulators != null) {
            manipulators.values().forEach(manipulator -> keys.addAll(manipulator.getKeys()));
        }
        return keys.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    default Set<ImmutableValue<?>> getValues() {
        final ImmutableSet.Builder<ImmutableValue<?>> values = ImmutableSet.builder();
        for (Map.Entry<Key<?>, KeyRegistration> entry : getRawValueMap().entrySet()) {
            final Key key = entry.getKey();
            final Optional<BaseValue> optValue = getValue(key);
            if (optValue.isPresent()) {
                values.add(ValueHelper.toImmutable(optValue.get()));
            }
        }
        final Map<Class<?>, H> valueContainers = getRawAdditionalContainers();
        // Custom data is supported by this container
        if (valueContainers != null) {
            for (H valueContainer : valueContainers.values()) {
                values.addAll(valueContainer.getValues());
            }
        }
        return values.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    default <E> ElementHolder<E> getElementHolder(Key<? extends BaseValue<E>> key) {
        final Object object = getRawValueMap().get(checkNotNull(key, "key"));
        if (object instanceof ElementHolder) {
            return (ElementHolder<E>) object;
        }
        return null;
    }

    @Override
    default <V extends BaseValue<E>, E> ElementHolderKeyRegistration<V, E> registerKey(Key<? extends V> key, @Nullable E defaultValue) {
        checkNotNull(key, "key");
        final Map<Key<?>, KeyRegistration> map = getRawValueMap();
        checkArgument(!map.containsKey(key), "The specified key (%s) is already registered.", key);
        final ElementHolderKeyRegistrationImpl<V, E> holder = new ElementHolderKeyRegistrationImpl<>(key);
        holder.set(defaultValue);
        map.put(key, holder);
        return holder;
    }

    @Override
    default <V extends BaseValue<E>, E> ElementHolderKeyRegistration<V, E> registerKey(Key<? extends V> key) {
        return registerKey(key, null);
    }

    default <V extends BoundedValue<E>, E extends Comparable<E>> ElementHolderKeyRegistration<V, E> registerKeySupplied(Key<? extends V> key,
            E defaultValue, Supplier<E> minimumSupplier, Supplier<E> maximumSupplier) {
        // TODO: Permit absent bounded values
        final ElementHolderKeyRegistration<V, E> registration = registerKey(key, defaultValue);
        final boolean immutable = key.getValueToken().getRawType().isAssignableFrom(ImmutableValue.class);
        registration.applyValueProcessor(builder -> builder.offerHandler((key1, valueContainer, element) -> {
            final E minimum = minimumSupplier.get();
            final E maximum = maximumSupplier.get();
            final ImmutableBoundedValue<E> newValue = LanternValueFactory.boundedBuilder(key)
                    .actualValue(element)
                    .defaultValue(defaultValue)
                    .maximum(maximum)
                    .minimum(minimum)
                    .build().asImmutable();
            if (element.compareTo(maximum) > 0 || element.compareTo(minimum) < 0) {
                return DataTransactionResult.errorResult(newValue);
            }
            final E oldElement = registration.set(element);
            if (oldElement == null) {
                return DataTransactionResult.successResult(newValue);
            }
            return DataTransactionResult.successReplaceResult(newValue,
                    LanternValueFactory.boundedBuilder(key)
                            .actualValue(oldElement)
                            .defaultValue(defaultValue)
                            .maximum(maximum)
                            .minimum(minimum)
                            .build().asImmutable());
        }).retrieveHandler((key1, valueContainer) -> {
            E element = registration.get();
            if (element == null) {
                return Optional.empty();
            } else {
                final E minimum = minimumSupplier.get();
                if (element.compareTo(minimum) < 0) {
                    registration.set(minimum);
                    element = minimum;
                } else {
                    final E maximum = maximumSupplier.get();
                    if (element.compareTo(minimum) > 0) {
                        registration.set(maximum);
                        element = maximum;
                    }
                }
                return Optional.of(element);
            }
        }).valueRetrieveHandler((key1, valueContainer) -> {
            E element = registration.get();
            if (element == null) {
                return Optional.empty();
            } else {
                final E minimum = minimumSupplier.get();
                final E maximum = maximumSupplier.get();
                if (element.compareTo(minimum) < 0) {
                    registration.set(minimum);
                    element = minimum;
                } else if (element.compareTo(minimum) > 0) {
                    registration.set(maximum);
                    element = maximum;
                }
                final MutableBoundedValue<E> value = LanternValueFactory.boundedBuilder(key)
                        .actualValue(element)
                        .defaultValue(defaultValue)
                        .maximum(maximum)
                        .minimum(minimum)
                        .build();
                //noinspection unchecked
                return Optional.of((V) (immutable ? value.asImmutable() : value));
            }
        }).removeHandler((key1, valueContainer) -> {
            E element = registration.get();
            if (element == null) {
                return DataTransactionResult.failNoData();
            }
            final E minimum = minimumSupplier.get();
            final E maximum = maximumSupplier.get();
            return DataTransactionResult.failResult(LanternValueFactory.boundedBuilder(key)
                    .actualValue(element)
                    .defaultValue(defaultValue)
                    .maximum(maximum)
                    .minimum(minimum)
                    .build().asImmutable());
        }));
        return registration;
    }

    @Override
    default <V extends BoundedValue<E>, E extends Comparable<E>> ElementHolderKeyRegistration<V, E> registerKey(Key<? extends V> key,
            E defaultValue, E minimum, E maximum) {
        return registerKeySupplied(key, defaultValue,
                () -> minimum,
                () -> maximum);
    }

    @Override
    default <V extends BoundedValue<E>, E extends Comparable<E>> ElementHolderKeyRegistration<V, E> registerKey(Key<? extends V> key,
            E defaultValue, Key<? extends BaseValue<E>> minimum, Key<? extends BaseValue<E>> maximum) {
        return registerKeySupplied(key, defaultValue,
                () -> get(minimum).get(),
                () -> get(maximum).get());
    }

    @Override
    default <V extends BoundedValue<E>, E extends Comparable<E>> ElementHolderKeyRegistration<V, E> registerKey(Key<? extends V> key,
            E defaultValue, E minimum, Key<? extends BaseValue<E>> maximum) {
        return registerKeySupplied(key, defaultValue,
                () -> minimum,
                () -> get(maximum).get());
    }

    @Override
    default <V extends BoundedValue<E>, E extends Comparable<E>> ElementHolderKeyRegistration<V, E> registerKey(Key<? extends V> key,
            E defaultValue, Key<? extends BaseValue<E>> minimum, E maximum) {
        return registerKeySupplied(key, defaultValue,
                () -> get(minimum).get(),
                () -> maximum);
    }

    @Override
    default <V extends BaseValue<E>, E> KeyRegistration<V, E> registerProcessorKey(Key<? extends V> key) {
        checkNotNull(key, "key");
        final Map<Key<?>, KeyRegistration> map = getRawValueMap();
        checkArgument(!map.containsKey(key), "The specified key (%s) is already registered.", key);
        final KeyRegistration<V, E> holder = new SimpleKeyRegistration.SingleProcessor<>(key);
        map.put(key, holder);
        return holder;
    }

    default Map<Key<?>, KeyRegistration> copyRawValueMap() {
        final Map<Key<?>, KeyRegistration> copy = new HashMap<>();
        final Map<Key<?>, KeyRegistration> map = getRawValueMap();
        for (Map.Entry<Key<?>, KeyRegistration> entry : map.entrySet()) {
            final KeyRegistration registration = entry.getValue();
            KeyRegistration registrationCopy;
            if (registration instanceof ElementHolderKeyRegistration) {
                //noinspection unchecked
                final ElementHolderKeyRegistrationImpl element = new ElementHolderKeyRegistrationImpl(registration.getKey());
                element.value = ((ElementHolderKeyRegistration) registration).get();
                registrationCopy = element;
            } else {
                registrationCopy = ((SimpleKeyRegistration.SingleProcessor) registration).copy();
            }
            copy.put(entry.getKey(), registrationCopy);
        }
        return copy;
    }

    @Nullable
    default Map<Class<?>, H> copyRawAdditionalManipulators() {
        return copyRawAdditionalManipulators(HashMap::new);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    default Map<Class<?>, H> copyRawAdditionalManipulators(Supplier<Map<Class<?>, ? extends H>> supplier) {
        final Map<Class<?>, H> map = getRawAdditionalContainers();
        if (map == null) {
            return null;
        }
        final Map copy = supplier.get();
        for (Map.Entry<Class<?>, H> entry : map.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().copy());
        }
        return copy;
    }

    @Nullable
    default <R extends ValueContainer<?>> Map<Class<?>, R> copyConvertedRawAdditionalManipulators(Function<H, R> converter) {
        // No method reference here, thanks intellij...
        //noinspection Convert2MethodRef
        return copyConvertedRawAdditionalManipulators(converter, () -> new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    default <R extends ValueContainer<?>> Map<Class<?>, R> copyConvertedRawAdditionalManipulators(Function<H, R> converter,
            Supplier<Map<Class<?>, R>> supplier) {
        final Map<Class<?>, H> map = getRawAdditionalContainers();
        if (map == null) {
            return null;
        }
        final Map copy = supplier.get();
        for (Map.Entry<Class<?>, H> entry : map.entrySet()) {
            copy.put(entry.getKey(), ((Function) converter).apply(entry.getValue()));
        }
        return copy;
    }
}
