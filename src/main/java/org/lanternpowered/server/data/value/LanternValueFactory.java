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
import org.lanternpowered.server.data.value.immutable.ImmutableLanternEntityValue;
import org.lanternpowered.server.data.value.immutable.ImmutableLanternItemValue;
import org.lanternpowered.server.data.value.immutable.ImmutableLanternListValue;
import org.lanternpowered.server.data.value.immutable.ImmutableLanternMapValue;
import org.lanternpowered.server.data.value.immutable.ImmutableLanternOptionalValue;
import org.lanternpowered.server.data.value.immutable.ImmutableLanternPatternListValue;
import org.lanternpowered.server.data.value.immutable.ImmutableLanternSetValue;
import org.lanternpowered.server.data.value.immutable.ImmutableLanternValue;
import org.lanternpowered.server.data.value.immutable.ImmutableLanternWeightedCollectionValue;
import org.lanternpowered.server.data.value.mutable.LanternBoundedValue;
import org.lanternpowered.server.data.value.mutable.LanternEntityValue;
import org.lanternpowered.server.data.value.mutable.LanternItemValue;
import org.lanternpowered.server.data.value.mutable.LanternListValue;
import org.lanternpowered.server.data.value.mutable.LanternMapValue;
import org.lanternpowered.server.data.value.mutable.LanternOptionalValue;
import org.lanternpowered.server.data.value.mutable.LanternPatternListValue;
import org.lanternpowered.server.data.value.mutable.LanternSetValue;
import org.lanternpowered.server.data.value.mutable.LanternValue;
import org.lanternpowered.server.data.value.mutable.LanternWeightedCollectionValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.BoundedValue;
import org.spongepowered.api.data.value.ValueFactory;
import org.spongepowered.api.data.value.immutable.ImmutableListValue;
import org.spongepowered.api.data.value.immutable.ImmutableMapValue;
import org.spongepowered.api.data.value.immutable.ImmutableOptionalValue;
import org.spongepowered.api.data.value.immutable.ImmutablePatternListValue;
import org.spongepowered.api.data.value.immutable.ImmutableSetValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.immutable.ImmutableWeightedCollectionValue;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.PatternListValue;
import org.spongepowered.api.data.value.mutable.SetValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.data.value.mutable.WeightedCollectionValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStack;
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

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class LanternValueFactory implements ValueFactory {

    private static LanternValueFactory instance = new LanternValueFactory();

    public static LanternValueFactory get() {
        return instance;
    }

    private final Map<Class<?>, ValueSupplier> valueSuppliers = new IdentityHashMap<>();
    private final Map<Key<?>, ValueProcessorKeyRegistration> keyRegistrations = new HashMap<>();
    private final Collection<ValueProcessorKeyRegistration<?,?>> unmodifiableKeyRegistrations =
            Collections.unmodifiableCollection((Collection) this.keyRegistrations.values());

    private final static Comparator<Integer> INT_COMPARATOR = Integer::compare;
    private final static Comparator<Double> DOUBLE_COMPARATOR = Double::compare;

    private LanternValueFactory() {
        registerSupplier(Value.class, ValueSupplier.<Value, Object>of(
                (key, element) -> {
                    if (element instanceof ItemStack) {
                        return new LanternItemValue(key, (ItemStack) element);
                    } else if (element instanceof Entity) {
                        return new LanternEntityValue<>(key, (Entity) element);
                    } else {
                        return new LanternValue<>(key, element);
                    }
                },
                (key, element, defElement) -> {
                    if (element instanceof ItemStack) {
                        return new LanternItemValue(key, (ItemStack) defElement, (ItemStack) element);
                    } else if (element instanceof Entity) {
                        return new LanternEntityValue<>(key, (Entity) element);
                    } else {
                        return new LanternValue<>(key, defElement, element);
                    }
                }));
        registerSupplier(MutableBoundedValue.class, ValueSupplier.of(
                (key, element) -> {
                    if (key.getElementToken().getRawType() == Integer.class) {
                        return new LanternBoundedValue(key, element, INT_COMPARATOR, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                    } else if (key.getElementToken().getRawType() == Double.class) {
                        return new LanternBoundedValue(key, element, DOUBLE_COMPARATOR, -Double.MAX_VALUE, Double.MAX_VALUE);
                    }
                    throw new IllegalStateException();
                },
                (key, element, defElement) -> {
                    if (key.getElementToken().getRawType() == Integer.class) {
                        return new LanternBoundedValue(key, element, INT_COMPARATOR, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                    } else if (key.getElementToken().getRawType() == Double.class) {
                        return new LanternBoundedValue(key, element, DOUBLE_COMPARATOR, -Double.MAX_VALUE, Double.MAX_VALUE);
                    }
                    throw new IllegalStateException();
                }));
        registerSupplier(ListValue.class, ValueSupplier.<ListValue, List<Object>>of(
                LanternListValue::new, (key, element, defElement) -> new LanternListValue<>(key, defElement, element)));
        registerSupplier(SetValue.class, ValueSupplier.<SetValue, Set<Object>>of(
                LanternSetValue::new, (key, element, defElement) -> new LanternSetValue<>(key, defElement, element)));
        registerSupplier(MapValue.class, ValueSupplier.<MapValue, Map<Object, Object>>of(
                LanternMapValue::new, (key, element, defElement) -> new LanternMapValue<>(key, defElement, element)));
        registerSupplier(OptionalValue.class, ValueSupplier.<OptionalValue, Optional<Object>>of(
                LanternOptionalValue::new, (key, element, defElement) -> new LanternOptionalValue(key, defElement, element)));
        registerSupplier(PatternListValue.class, ValueSupplier.<PatternListValue, List<PatternLayer>>of(
                LanternPatternListValue::new, (key, element, defElement) -> new LanternPatternListValue(key, defElement, element)));
        registerSupplier(WeightedCollectionValue.class, ValueSupplier.<WeightedCollectionValue, WeightedTable<?>>of(
                LanternWeightedCollectionValue::new, (key, element, defElement) -> new LanternWeightedCollectionValue(key, defElement, element)));
        registerSupplier(ImmutableValue.class, ValueSupplier.<ImmutableValue, Object>of(
                (key, element) -> {
                    if (element instanceof ItemStack) {
                        return new ImmutableLanternItemValue(key, (ItemStack) element);
                    } else if (element instanceof Entity) {
                        return new ImmutableLanternEntityValue<>(key, (Entity) element);
                    } else {
                        return new ImmutableLanternValue<>(key, element);
                    }
                },
                (key, element, defElement) -> {
                    if (element instanceof ItemStack) {
                        return new ImmutableLanternItemValue(key, (ItemStack) defElement, (ItemStack) element);
                    } else if (element instanceof Entity) {
                        return new ImmutableLanternEntityValue<>(key, (Entity) element);
                    } else {
                        return new ImmutableLanternValue<>(key, defElement, element);
                    }
                }));
        registerSupplier(ImmutableListValue.class, ValueSupplier.<ImmutableListValue, List<Object>>of(
                ImmutableLanternListValue::new, (key, element, defElement) -> new ImmutableLanternListValue<>(key, defElement, element)));
        registerSupplier(ImmutableSetValue.class, ValueSupplier.<ImmutableSetValue, Set<Object>>of(
                ImmutableLanternSetValue::new, (key, element, defElement) -> new ImmutableLanternSetValue<>(key, defElement, element)));
        registerSupplier(ImmutableMapValue.class, ValueSupplier.<ImmutableMapValue, Map<Object, Object>>of(
                ImmutableLanternMapValue::new, (key, element, defElement) -> new ImmutableLanternMapValue<>(key, defElement, element)));
        registerSupplier(ImmutableOptionalValue.class, ValueSupplier.<ImmutableOptionalValue, Optional<Object>>of(
                ImmutableLanternOptionalValue::new, (key, element, defElement) -> new ImmutableLanternOptionalValue(key, defElement, element)));
        registerSupplier(ImmutablePatternListValue.class, ValueSupplier.<ImmutablePatternListValue, List<PatternLayer>>of(
                ImmutableLanternPatternListValue::new, (key, element, defElement) -> new ImmutableLanternPatternListValue(key, defElement, element)));
        registerSupplier(ImmutableWeightedCollectionValue.class, ValueSupplier.<ImmutableWeightedCollectionValue, WeightedTable<?>>of(
                ImmutableLanternWeightedCollectionValue::new, (key, element, defElement) ->
                        new ImmutableLanternWeightedCollectionValue(key, defElement, element)));
    }

    public <V extends BaseValue<E>, E> ValueProcessorKeyRegistration<V, E> registerKey(Key<? extends V> key) {
        final ValueProcessorKeyRegistration<V, E> registration = ValueProcessorKeyRegistration.create(key);
        this.keyRegistrations.put(key, registration);
        return registration;
    }

    public <V extends BaseValue<E>, E> Optional<ValueProcessorKeyRegistration<V, E>> getKeyRegistration(Key<? extends V> key) {
        return Optional.ofNullable(this.keyRegistrations.get(checkNotNull(key, "key")));
    }

    public Collection<ValueProcessorKeyRegistration<?,?>> getKeyRegistrations() {
        return this.unmodifiableKeyRegistrations;
    }

    public <R extends BaseValue, E> void registerSupplier(Class<R> type, ValueSupplier<R, E> supplier) {
        this.valueSuppliers.put(type, supplier);
    }

    /**
     * Creates a {@link Value} with the proper type for the specified {@link Key}.
     *
     * @param key the key to create a value for
     * @param element the element (object) that is stored in the value
     * @param <V> the value type
     * @param <E> the element type
     * @return the value instance
     */
    public <V extends BaseValue<E>, E> V createValueForKey(Key<? extends V> key, E element) {
        checkNotNull(key, "key");
        checkNotNull(element, "element");
        final ValueSupplier supplier = this.valueSuppliers.get(key.getValueToken().getRawType());
        checkArgument(supplier != null, "The BaseValue type used by the key (" + key.getValueToken().getRawType().getName() + ") isn't supported.");
        return (V) supplier.get(key, element);
    }

    /**
     * Creates a {@link Value} with the proper type for the specified {@link Key}.
     *
     * @param key the key to create a value for
     * @param element the element (object) that is stored in the value
     * @param defaultElement the default element (object) that is stored in the value
     * @param <V> the value type
     * @param <E> the element type
     * @return the value instance
     */
    public <V extends BaseValue<E>, E> V createValueForKey(Key<V> key, E element, E defaultElement) {
        checkNotNull(key, "key");
        checkNotNull(element, "element");
        ValueSupplier supplier = this.valueSuppliers.get(key.getValueToken().getRawType());
        checkArgument(supplier != null, "The BaseValue type used by the key (" + key.getValueToken().getRawType().getName() + ") isn't supported.");
        return (V) supplier.get(key, element, defaultElement);
    }

    @Override
    public <E> Value<E> createValue(Key<Value<E>> key, E element) {
        return new LanternValue<>(checkNotNull(key, "key"), checkNotNull(element, "element"));
    }

    @Override
    public <E> Value<E> createValue(Key<Value<E>> key, E element, E defaultValue) {
        return new LanternValue<>(checkNotNull(key, "key"), checkNotNull(defaultValue, "defaultValue"), checkNotNull(element, "element"));
    }

    @Override
    public <E> ListValue<E> createListValue(Key<ListValue<E>> key, List<E> elements) {
        return new LanternListValue<>(checkNotNull(key, "key"), checkNotNull(elements, "elements"));
    }

    @Override
    public <E> ListValue<E> createListValue(Key<ListValue<E>> key, List<E> elements, List<E> defaults) {
        return new LanternListValue<>(checkNotNull(key, "key"), checkNotNull(defaults, "defaults"), checkNotNull(elements, "elements"));
    }

    @Override
    public <E> SetValue<E> createSetValue(Key<SetValue<E>> key, Set<E> elements) {
        return new LanternSetValue<>(checkNotNull(key, "key"), checkNotNull(elements, "elements"));
    }

    @Override
    public <E> SetValue<E> createSetValue(Key<SetValue<E>> key, Set<E> elements, Set<E> defaults) {
        return new LanternSetValue<>(checkNotNull(key, "key"), checkNotNull(defaults, "defaults"), checkNotNull(elements, "elements"));
    }

    @Override
    public <K, V> MapValue<K, V> createMapValue(Key<MapValue<K, V>> key, Map<K, V> map) {
        return new LanternMapValue<>(checkNotNull(key, "key"), checkNotNull(map, "map"));
    }

    @Override
    public <K, V> MapValue<K, V> createMapValue(Key<MapValue<K, V>> key, Map<K, V> map, Map<K, V> defaults) {
        return new LanternMapValue<>(checkNotNull(key, "key"), checkNotNull(defaults, "defaults"), checkNotNull(map, "map"));
    }

    @Override
    public <E> BoundedValueBuilder<E> createBoundedValueBuilder(Key<MutableBoundedValue<E>> key) {
        return new LanternBoundedValueBuilder<>(checkNotNull(key, "key"));
    }

    @Override
    public <E> OptionalValue<E> createOptionalValue(Key<OptionalValue<E>> key, @Nullable E element) {
        return new LanternOptionalValue<>(checkNotNull(key, "key"), Optional.empty(), Optional.ofNullable(element));
    }

    @Override
    public <E> OptionalValue<E> createOptionalValue(Key<OptionalValue<E>> key, @Nullable E element, E defaultElement) {
        return new LanternOptionalValue<>(checkNotNull(key, "key"), Optional.of(defaultElement), Optional.ofNullable(element));
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
        private E defaultValue;
        @Nullable private E value;

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
        public BoundedValueBuilder<E> defaultValue(E defaultValue) {
            this.defaultValue = checkNotNull(defaultValue);
            return this;
        }

        @Override
        public BoundedValueBuilder<E> actualValue(E value) {
            this.value = checkNotNull(value);
            return this;
        }

        @Override
        public LanternBoundedValue<E> build() {
            checkState(this.comparator != null);
            checkState(this.minimum != null);
            checkState(this.maximum != null);
            checkState(this.defaultValue != null);
            if (this.value == null) {
                return new LanternBoundedValue<>(this.key, this.defaultValue, this.comparator, this.minimum, this.maximum);
            } else {
                return new LanternBoundedValue<>(this.key, this.defaultValue, this.comparator, this.minimum, this.maximum, this.value);
            }
        }
    }
}
