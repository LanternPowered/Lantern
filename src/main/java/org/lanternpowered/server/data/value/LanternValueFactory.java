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
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.data.processor.ValueProcessorKeyRegistration;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.BoundedValue;
import org.spongepowered.api.data.value.ListValue;
import org.spongepowered.api.data.value.MapValue;
import org.spongepowered.api.data.value.OptionalValue;
import org.spongepowered.api.data.value.SetValue;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.data.value.WeightedCollectionValue;
import org.spongepowered.api.util.weighted.WeightedTable;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("unchecked")
public class LanternValueFactory implements Value.Factory {

    private static LanternValueFactory instance = new LanternValueFactory();

    public static LanternValueFactory get() {
        return instance;
    }

    private final Map<Class<?>, ValueSupplier> valueSuppliers = new IdentityHashMap<>();
    private final Map<Key<?>, ValueProcessorKeyRegistration> keyRegistrations = new HashMap<>();
    private final Collection<ValueProcessorKeyRegistration<?,?>> unmodifiableKeyRegistrations =
            Collections.unmodifiableCollection((Collection) this.keyRegistrations.values());

    protected interface BoundedValueConstructor {
        <E> BoundedValue<E> create(Key<? extends Value<E>> key, E value, E min, E max, Comparator<E> comparator);
    }

    protected static class BoundedValueSupplier<V extends BoundedValue<E>, E> implements ValueSupplier<V, E> {

        private final BoundedValueConstructor constructor;

        BoundedValueSupplier(BoundedValueConstructor constructor) {
            this.constructor = constructor;
        }

        @Override
        public V get(Key key, E element) {
            final Class<?> type = key.getElementToken().getRawType();
            if (type == Integer.class) {
                return (V) this.constructor.create(key, (Integer) element, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer::compare);
            } else if (type == Double.class) {
                return (V) this.constructor.create(key, (Double) element, -Double.MAX_VALUE, Double.MAX_VALUE, Double::compare);
            } else if (type == Long.class) {
                return (V) this.constructor.create(key, (Long) element, Long.MIN_VALUE, Long.MAX_VALUE, Long::compare);
            } else if (type == Float.class) {
                return (V) this.constructor.create(key, (Float) element, -Float.MAX_VALUE, Float.MAX_VALUE, Float::compare);
            } else if (type == Short.class) {
                return (V) this.constructor.create(key, (Short) element, Short.MIN_VALUE, Short.MAX_VALUE, Short::compare);
            } else if (type == Byte.class) {
                return (V) this.constructor.create(key, (Byte) element, Byte.MIN_VALUE, Byte.MAX_VALUE, Byte::compare);
            }
            throw new IllegalStateException();
        }
    }

    private LanternValueFactory() {
        registerSupplier(Value.Mutable.class, LanternMutableValue::new);
        registerSupplier(Value.Immutable.class, LanternImmutableValue::new);

        registerSupplier(ListValue.Mutable.class,
                (key, element) -> new LanternMutableListValue(key, (List) element));
        registerSupplier(ListValue.Immutable.class,
                (key, element) -> new LanternImmutableListValue(key, (List) element));

        registerSupplier(SetValue.Mutable.class,
                (key, element) -> new LanternMutableSetValue(key, (Set) element));
        registerSupplier(SetValue.Immutable.class,
                (key, element) -> new LanternImmutableSetValue(key, (Set) element));

        registerSupplier(MapValue.Mutable.class,
                (key, element) -> new LanternMutableMapValue(key, (Map) element));
        registerSupplier(MapValue.Immutable.class,
                (key, element) -> new LanternImmutableMapValue(key, (Map) element));

        registerSupplier(OptionalValue.Mutable.class,
                (key, element) -> new LanternMutableOptionalValue(key, (Optional) element));
        registerSupplier(OptionalValue.Immutable.class,
                (key, element) -> new LanternImmutableOptionalValue(key, (Optional) element));

        registerSupplier(WeightedCollectionValue.Mutable.class,
                (key, element) -> new LanternMutableWeightedCollectionValue(key, (WeightedTable) element));
        registerSupplier(WeightedCollectionValue.Immutable.class,
                (key, element) -> new LanternImmutableWeightedCollectionValue(key, (WeightedTable) element));

        registerSupplier(BoundedValue.Mutable.class, new BoundedValueSupplier<>(LanternMutableBoundedValue::new));
        registerSupplier(BoundedValue.Immutable.class, new BoundedValueSupplier<>(LanternImmutableBoundedValue::new));
    }

    public <V extends Value<E>, E> ValueProcessorKeyRegistration<V, E> registerKey(Key<? extends V> key) {
        final ValueProcessorKeyRegistration<V, E> registration = ValueProcessorKeyRegistration.create(key);
        this.keyRegistrations.put(key, registration);
        return registration;
    }

    public <V extends Value<E>, E> Optional<ValueProcessorKeyRegistration<V, E>> getKeyRegistration(Key<? extends V> key) {
        return Optional.ofNullable(this.keyRegistrations.get(checkNotNull(key, "key")));
    }

    public Collection<ValueProcessorKeyRegistration<?,?>> getKeyRegistrations() {
        return this.unmodifiableKeyRegistrations;
    }

    public <V extends Value<E>, E> void registerSupplier(Class<V> type, ValueSupplier<V, E> supplier) {
        this.valueSuppliers.put(type, supplier);
    }

    /**
     * Creates a {@link Value.Mutable} with the proper type for the specified {@link Key}.
     *
     * @param key the key to create a value for
     * @param element the element (object) that is stored in the value
     * @param <V> the value type
     * @param <E> the element type
     * @return the value instance
     */
    public <V extends Value<E>, E> V createValueForKey(Key<? extends V> key, E element) {
        checkNotNull(key, "key");
        checkNotNull(element, "element");
        final ValueSupplier supplier = this.valueSuppliers.get(key.getValueToken().getRawType());
        checkArgument(supplier != null, "The BaseValue type used by the key (" + key.getValueToken().getRawType().getName() + ") isn't supported.");
        return (V) supplier.get(key, element);
    }

    @Override
    public <E> Value.Mutable<E> createValue(Key<? extends Value<E>> key, E element) {
        return new LanternMutableValue<>(key, element);
    }

    @Override
    public <E> ListValue.Mutable<E> createListValue(Key<? extends Value<List<E>>> key, List<E> elements) {
        return new LanternMutableListValue<>(key, elements);
    }

    @Override
    public <E> SetValue.Mutable<E> createSetValue(Key<? extends Value<Set<E>>> key, Set<E> elements) {
        return new LanternMutableSetValue<>(key, elements);
    }

    @Override
    public <K, V> MapValue.Mutable<K, V> createMapValue(Key<? extends Value<Map<K, V>>> key, Map<K, V> map) {
        return new LanternMutableMapValue<>(key, map);
    }

    @Override
    public <E> BoundedValueBuilder<E> createBoundedValueBuilder(Key<? extends BoundedValue<E>> key) {
        return new LanternBoundedValueBuilder<>(key);
    }

    @Override
    public <E> OptionalValue.Mutable<E> createOptionalValue(Key<? extends OptionalValue<E>> key, @Nullable E element) {
        return new LanternMutableOptionalValue<>(key, Optional.ofNullable(element));
    }

    public static <E> BoundedValueBuilder<E> boundedBuilder(Key<? extends BoundedValue<E>> key) {
        return new LanternBoundedValueBuilder<>(checkNotNull(key));
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    private static final class LanternBoundedValueBuilder<E> implements BoundedValueBuilder<E> {

        private final Key<? extends BoundedValue<E>> key;
        private Comparator<E> comparator;
        private E minimum;
        private E maximum;
        private E value;

        LanternBoundedValueBuilder(Key<? extends BoundedValue<E>> key) {
            this.key = checkNotNull(key);
        }

        @Override
        public BoundedValueBuilder<E> comparator(Comparator<E> comparator) {
            this.comparator = checkNotNull(comparator);
            return this;
        }

        @Override
        public BoundedValueBuilder<E> minimum(E minimum) {
            this.minimum = checkNotNull(minimum);
            if (this.comparator == null && minimum instanceof Comparable) {
                this.comparator = (o1, o2) -> ((Comparable<E>) o1).compareTo(o2);
            }
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public BoundedValueBuilder<E> maximum(E maximum) {
            this.maximum = checkNotNull(maximum);
            if (this.comparator == null && maximum instanceof Comparable) {
                this.comparator = (o1, o2) -> ((Comparable<E>) o1).compareTo(o2);
            }
            return this;
        }

        @Override
        public BoundedValueBuilder<E> value(E value) {
            this.value = checkNotNull(value);
            return this;
        }

        @Override
        public LanternMutableBoundedValue<E> build() {
            checkState(this.comparator != null);
            checkState(this.minimum != null);
            checkState(this.maximum != null);
            checkState(this.value != null);
            return new LanternMutableBoundedValue<>(this.key, this.value, this.minimum, this.maximum, this.comparator);
        }
    }
}
