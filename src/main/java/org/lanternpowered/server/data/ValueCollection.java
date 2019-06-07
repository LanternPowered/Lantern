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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.data.element.Element;
import org.lanternpowered.server.data.element.ElementKeyRegistration;
import org.lanternpowered.server.data.processor.ElementProcessorBuilder;
import org.lanternpowered.server.data.processor.Processor;
import org.lanternpowered.server.data.processor.ValueProcessorKeyRegistration;
import org.lanternpowered.server.data.value.LanternValueFactory;
import org.lanternpowered.server.util.copy.Copyable;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.BoundedValue;
import org.spongepowered.api.data.value.Value;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings({ "unchecked", "ConstantConditions" })
public final class ValueCollection implements Copyable<ValueCollection> {

    public enum Mode {
        /**
         * Normal behavior.
         */
        NORMAL,
        /**
         * All the {@link Key}s will be registered as non removable.
         */
        NON_REMOVABLE,
    }

    public static ValueCollection create() {
        return create(Mode.NORMAL);
    }

    /**
     * Creates a new {@link ValueCollection}
     * with the given {@link Mode}.
     *
     * @param mode The mode
     * @return The value collection
     */
    public static ValueCollection create(Mode mode) {
        checkNotNull(mode, "mode");
        return new ValueCollection(new HashMap<>(), mode);
    }

    private final Map<Key<?>, KeyRegistration> values;
    private final Set<Key<?>> unmodifiableKeys;
    private final Collection<KeyRegistration<?,?>> unmodifiableRegistrations;
    private final Mode mode;

    private ValueCollection(Map<Key<?>, KeyRegistration> values, Mode mode) {
        this.unmodifiableKeys = Collections.unmodifiableSet(values.keySet());
        this.unmodifiableRegistrations = (Collection) Collections.unmodifiableCollection(values.values());
        this.values = values;
        this.mode = mode;
    }

    private void checkKey(Key<?> key) {
        checkNotNull(key, "key");
        checkArgument(!this.values.containsKey(key), "The specified key (%s) is already registered.", key);
    }

    @Override
    public ValueCollection copy() {
        final Map<Key<?>, KeyRegistration> values = new HashMap<>();
        for (Map.Entry<Key<?>, KeyRegistration> entry : this.values.entrySet()) {
            final KeyRegistration registration = entry.getValue();
            values.put(entry.getKey(), Copyable.copy(registration).orElse(registration));
        }
        return new ValueCollection(values, this.mode);
    }

    /**
     * Gets a unmodifiable {@link Collection} with all
     * the registered {@link Key}s.
     *
     * @return The keys
     */
    public Collection<Key<?>> getKeys() {
        return this.unmodifiableKeys;
    }

    /**
     * Gets a unmodifiable {@link Collection} with all
     * the {@link KeyRegistration}s.
     *
     * @return The key registrations
     */
    public Collection<KeyRegistration<?,?>> getAll() {
        return this.unmodifiableRegistrations;
    }

    /**
     * Gets whether the specified {@link Key} is registered
     * to this {@link ValueCollection}.
     *
     * @param key The key
     * @return Is registered
     */
    public boolean has(Key<?> key) {
        return this.values.containsKey(key);
    }

    /**
     * Gets the {@link KeyRegistration} for the given {@link Key}, if present.
     *
     * @param key The key
     * @param <V> The value type
     * @param <E> The element type
     * @return The key registration, if present
     */
    public <V extends Value<E>, E> Optional<KeyRegistration<V, E>> get(Key<? extends Value<E>> key) {
        return Optional.ofNullable(this.values.get(key));
    }

    /**
     * Gets the {@link Element} for the given {@link Key}, if present.
     *
     * @param key The key
     * @param <E> The element type
     * @return The element, if present
     */
    public <E> Optional<Element<E>> getElement(Key<? extends Value<E>> key) {
        checkNotNull(key, "key");
        final Object object = this.values.get(key);
        return object instanceof Element ? Optional.of((Element<E>) object) : Optional.empty();
    }

    /**
     * Registers the given {@link Key} with a default value.
     *
     * @param key The key
     * @param defaultValue The default value
     * @param <V> The value type
     * @param <E> The element type
     * @return The element key registration
     */
    public <V extends Value<E>, E> ElementKeyRegistration<V, E> registerNonRemovable(
            Key<? extends V> key, E defaultValue) {
        checkNotNull(defaultValue, "defaultValue");
        checkKey(key);
        final ElementKeyRegistration<V, E> processor = (ElementKeyRegistration<V, E>) ElementProcessorBuilder.createNonRemovable(key);
        processor.set(defaultValue);
        this.values.put(key, processor);
        return processor;
    }

    /**
     * Registers the given {@link Key} with a default value.
     *
     * @param key The key
     * @param defaultValue The default value
     * @param <V> The value type
     * @param <E> The element type
     * @return The element key registration
     */
    public <V extends Value<E>, E> ElementKeyRegistration<V, E> register(
            Key<? extends V> key, @Nullable E defaultValue) {
        if (this.mode == Mode.NON_REMOVABLE) {
            return registerNonRemovable(key, defaultValue);
        }
        checkKey(key);
        final ElementKeyRegistration<V, E> processor = (ElementKeyRegistration<V, E>) ElementProcessorBuilder.createDefault(key);
        processor.set(defaultValue);
        this.values.put(key, processor);
        return processor;
    }

    /**
     * Registers a {@link Key} without a {@link Value.Mutable} attached to it. This means that there
     * won't be any data attached to the {@link Key}, but it will use a {@link Processor} to
     * retrieve the data depending on other {@link Key}s.
     *
     * <p>For example: {@link Keys#BODY_ROTATIONS} which will use a {@link Processor}
     * to retrieve all the body parts data from the {@link IValueContainer} to build the {@link Value.Mutable}.</p>
     *
     * @param key The key
     * @param defaultValue The default value
     * @param builderConsumer The builder consumer
     * @param <V> The value type
     * @param <E> The element type
     * @return The element key registration
     */
    public <V extends Value<E>, E> ElementKeyRegistration<V, E> register(
            Key<? extends V> key, @Nullable E defaultValue, Consumer<ElementProcessorBuilder<V, E>> builderConsumer) {
        checkKey(key);
        checkNotNull(builderConsumer, "builderConsumer");
        final ElementProcessorBuilder<V, E> builder = ElementProcessorBuilder.create(key);
        builderConsumer.accept(builder);
        final ElementKeyRegistration<V, E> element = (ElementKeyRegistration<V, E>) builder.build();
        element.set(defaultValue);
        this.values.put(key, element);
        return element;
    }

    /**
     * Registers the given {@link Key} as a {@link ValueProcessorKeyRegistration}.
     *
     * @param key The key
     * @param <V> The value type
     * @param <E> The element type
     * @return The element key registration
     */
    public <V extends Value<E>, E> ValueProcessorKeyRegistration<V, E> registerProcessor(Key<? extends V> key) {
        checkKey(key);
        final ValueProcessorKeyRegistration<V, E> processor = ValueProcessorKeyRegistration.create(key);
        this.values.put(key, processor);
        return processor;
    }

    private <V extends BoundedValue<E>, E extends Comparable<E>> ElementKeyRegistration<V, E> registerSupplied(
            Key<? extends V> key, E defaultValue,
            Function<IValueContainer<?>, E> minimumSupplier,
            Function<IValueContainer<?>, E> maximumSupplier) {
        checkKey(key);
        final ElementProcessorBuilder<V, E> builder = ElementProcessorBuilder.create(key);
        final boolean immutable = key.getValueToken().getRawType().isAssignableFrom(Value.Immutable.class);
        builder.fastOfferHandler((valueContainer, holder, element) -> {
            final E minimum = minimumSupplier.apply(valueContainer);
            final E maximum = maximumSupplier.apply(valueContainer);
            if (element.compareTo(maximum) > 0 || element.compareTo(minimum) < 0) {
                return false;
            }
            holder.set(element);
            return true;
        });
        builder.offerHandler((valueContainer, holder, element) -> {
            final E minimum = minimumSupplier.apply(valueContainer);
            final E maximum = maximumSupplier.apply(valueContainer);
            final BoundedValue.Immutable<E> newValue = LanternValueFactory.boundedBuilder(key)
                    .actualValue(element)
                    .defaultValue(defaultValue)
                    .maximum(maximum)
                    .minimum(minimum)
                    .build().asImmutable();
            if (element.compareTo(maximum) > 0 || element.compareTo(minimum) < 0) {
                return DataTransactionResult.errorResult(newValue);
            }
            final E oldElement = holder.set(element);
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
        });
        builder.retrieveHandler((valueContainer, holder) -> {
            E element = holder.get();
            if (element == null) {
                return Optional.empty();
            } else {
                final E minimum = minimumSupplier.apply(valueContainer);
                if (element.compareTo(minimum) < 0) {
                    holder.set(minimum);
                    element = minimum;
                } else {
                    final E maximum = maximumSupplier.apply(valueContainer);
                    if (element.compareTo(maximum) > 0) {
                        holder.set(maximum);
                        element = maximum;
                    }
                }
                return Optional.of(element);
            }
        });
        builder.valueRetrieveHandler((valueContainer, holder) -> {
            E element = holder.get();
            if (element == null) {
                return Optional.empty();
            } else {
                final E minimum = minimumSupplier.apply(valueContainer);
                final E maximum = maximumSupplier.apply(valueContainer);
                if (element.compareTo(minimum) < 0) {
                    holder.set(minimum);
                    element = minimum;
                } else if (element.compareTo(maximum) > 0) {
                    holder.set(maximum);
                    element = maximum;
                }
                final BoundedValue.Mutable<E> value = LanternValueFactory.boundedBuilder(key)
                        .actualValue(element)
                        .defaultValue(defaultValue)
                        .maximum(maximum)
                        .minimum(minimum)
                        .build();
                return Optional.of((V) (immutable ? value.asImmutable() : value));
            }
        });
        builder.fastRemoveHandler((valueContainer, holder) -> false);
        builder.removeHandler((valueContainer, holder) -> {
            E element = holder.get();
            if (element == null) {
                return DataTransactionResult.failNoData();
            }
            final E minimum = minimumSupplier.apply(valueContainer);
            final E maximum = maximumSupplier.apply(valueContainer);
            return DataTransactionResult.failResult(LanternValueFactory.boundedBuilder(key)
                    .actualValue(element)
                    .defaultValue(defaultValue)
                    .maximum(maximum)
                    .minimum(minimum)
                    .build().asImmutable());
        });
        final ElementKeyRegistration<V, E> element = (ElementKeyRegistration<V, E>) builder.build();
        element.set(defaultValue);
        this.values.put(key, element);
        return element;
    }

    public <V extends BoundedValue<E>, E extends Comparable<E>> ElementKeyRegistration<V, E> registerWithSuppliedBounds(Key<? extends V> key,
            E defaultValue, Function<IValueContainer<?>, E> minimumSupplier, Function<IValueContainer<?>, E> maximumSupplier) {
        return registerSupplied(key, defaultValue, minimumSupplier, maximumSupplier);
    }

    public <V extends BoundedValue<E>, E extends Comparable<E>> ElementKeyRegistration<V, E> registerWithSuppliedMax(Key<? extends V> key,
            E defaultValue, E minimum, Function<IValueContainer<?>, E> maximumSupplier) {
        return registerSupplied(key, defaultValue, container -> minimum, maximumSupplier);
    }

    public <V extends BoundedValue<E>, E extends Comparable<E>> ElementKeyRegistration<V, E> registerWithSuppliedMin(Key<? extends V> key,
            E defaultValue, Function<IValueContainer<?>, E> minimumSupplier, E maximum) {
        return registerSupplied(key, defaultValue, minimumSupplier, container -> maximum);
    }

    public <V extends BoundedValue<E>, E extends Comparable<E>> ElementKeyRegistration<V, E> register(Key<? extends V> key,
            E defaultValue, E minimum, E maximum) {
        return registerSupplied(key, defaultValue,
                container -> minimum,
                container -> maximum);
    }

    public <V extends BoundedValue<E>, E extends Comparable<E>> ElementKeyRegistration<V, E> register(Key<? extends V> key,
            E defaultValue, Key<? extends Value<E>> minimum, Key<? extends Value<E>> maximum) {
        return registerSupplied(key, defaultValue,
                container -> container.get(minimum).get(),
                container -> container.get(maximum).get());
    }

    public <V extends BoundedValue<E>, E extends Comparable<E>> ElementKeyRegistration<V, E> register(Key<? extends V> key,
            E defaultValue, E minimum, Key<? extends Value<E>> maximum) {
        return registerSupplied(key, defaultValue,
                container -> minimum,
                container -> container.get(maximum).get());
    }

    public <V extends BoundedValue<E>, E extends Comparable<E>> ElementKeyRegistration<V, E> register(Key<? extends V> key,
            E defaultValue, Key<? extends Value<E>> minimum, E maximum) {
        return registerSupplied(key, defaultValue,
                container -> container.get(minimum).get(),
                container -> maximum);
    }
}
