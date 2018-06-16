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
package org.lanternpowered.server.inventory.property;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.util.Tuple;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

/**
 * Base class for InventoryProperty implementations which stubs out all of the
 * common boilerplate functionality.
 *
 * @param <K> Key type, use {@link String} if no particular key type is required
 * @param <V> Value type
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractInventoryProperty<K, V> implements InventoryProperty<K, V> {

    private static Map<Class<?>, Tuple<Class<?>, String>> defaultKeys = new ConcurrentHashMap<>();
    private static Map<Class<?>, Comparator<?>> comparators = new ConcurrentHashMap<>();

    /**
     * Operator for comparing to other properties. Operators should always be
     * applied by consumers in a &lt;this&gt; &lt;OPERATOR&gt; &lt;other&gt;
     * pattern
     */
    protected final Operator operator;

    /**
     * The key.
     */
    private final K key;

    /**
     * The value.
     */
    protected V value;

    /**
     * Initialise internal values to defaults, use this ctor if you plan to
     * override {@link #getKey} and {@link #getValue} yourself.
     */
    protected AbstractInventoryProperty() {
        this(null);
    }

    /**
     * Initialise key to default, and value to the supplied value.
     *
     * @param value The value of the property
     */
    protected AbstractInventoryProperty(@Nullable V value) {
        this(null, value);
    }

    /**
     * Initialise the value to the specified value and use the specified
     * operator, use the default key.
     *
     * @param value The property value
     * @param op The operator for the property
     */
    protected AbstractInventoryProperty(@Nullable V value, @Nullable Operator op) {
        this(null, value, op);
    }

    /**
     * Use the specified key and value and set operator to the default.
     *
     * @param key The key identifying the property
     * @param value The property value
     */
    protected AbstractInventoryProperty(@Nullable K key, @Nullable V value) {
        this(key, value, null);
    }

    protected AbstractInventoryProperty(@Nullable K key, @Nullable V value, @Nullable Operator operator) {
        this.key = key != null ? key : this.getDefaultKey(value);
        this.value = value;
        this.operator = operator != null ? operator : this.getDefaultOperator(this.key, value);
    }

    /**
     * Gets the default value for {@link #key}, used in case null is passed in
     * (since we can't have a null key). In general this should return the class
     * name of the property itself but subclasses are free to alter this
     * behaviour if they wish.
     *
     * @param value Value passed in to the ctor, supplied in case a subclass
     *      wants to return a specific default key based on the value
     * @return default key to use. Must not be null!
     */
    @SuppressWarnings("unchecked")
    protected K getDefaultKey(@Nullable V value) {
        return (K) getDefaultKey(this.getClass());
    }

    /**
     * Gets the default key for the provided InventoryProperty class.
     *
     * @param clazz The InventoryProperty class.
     * @param <T> The InventoryProperty type.
     * @return default key to use.
     */
    public static <T extends InventoryProperty<?, ?>> Object getDefaultKey(Class<T> clazz) {
        return getTypeAndKey(clazz).getSecond();
    }

    /**
     * Gets the {@link Class} which represents the {@link InventoryProperty}
     * from the implementation class.
     *
     * @param clazz The implementation class
     * @param <T> The implementation type
     * @return The property type
     */
    public static <T extends InventoryProperty<?, ?>> Class<? super InventoryProperty<?,?>> getType(Class<T> clazz) {
        return (Class<? super InventoryProperty<?, ?>>) getTypeAndKey(clazz).getFirst();
    }

    private static <T extends InventoryProperty<?, ?>> Tuple<Class<?>, String> getTypeAndKey(Class<T> clazz) {
        return defaultKeys.computeIfAbsent(clazz, clazz1 -> {
            if (clazz1.isInterface()) {
                return new Tuple<>(clazz, clazz1.getSimpleName().toLowerCase(Locale.ENGLISH));
            }
            for (Class<?> interf : clazz1.getInterfaces()) {
                if (InventoryProperty.class.isAssignableFrom(interf)) {
                    return new Tuple<>(interf, interf.getSimpleName().toLowerCase(Locale.ENGLISH));
                }
            }
            return new Tuple<>(clazz, clazz.getSimpleName().toLowerCase(Locale.ENGLISH));
        });
    }

    /**
     * Return the default operator to use, based on the supplied key and value.
     *
     * @param key Property key
     * @param value Property initial value, may be null
     * @return operator to use
     */
    protected Operator getDefaultOperator(K key, @Nullable V value) {
        return Operator.defaultOperator();
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public Operator getOperator() {
        return this.operator;
    }

    @Override
    public boolean matches(@Nullable Property<?, ?> other) {
        return getOperator().compare(this, other);
    }

    @Override
    public int compareTo(Property<?, ?> other) {
        if (other == null) {
            return 1;
        }
        final V thisValue = getValue();
        final V otherValue = (V) other.getValue();
        if (otherValue == null) {
            return 1;
        } else if (thisValue == null) {
            return -1;
        }
        final Comparator<V> comparator = (Comparator<V>) comparators.computeIfAbsent(getClass(), propertyClass -> {
            // Generate a new comparator based on this class
            final TypeToken<?> valueType = TypeToken.of(propertyClass)
                    .resolveType(AbstractInventoryProperty.class.getTypeParameters()[1]);
            final Class<?> rawValueType = valueType.getRawType();
            return (a, b) -> {
                if (!rawValueType.isInstance(b)) {
                    return 1;
                }
                return compareValue((V) a, (V) b);
            };
        });
        return comparator.compare(thisValue, otherValue);
    }

    protected abstract int compareValue(V thisValue, V otherValue);

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final InventoryProperty<?, ?> other = (InventoryProperty<?, ?>) obj;
        return Objects.equals(getKey(), other.getKey()) && Objects.equals(getValue(), other.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue());
    }
}

