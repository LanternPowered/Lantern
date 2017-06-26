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
package co.aikar.util;

import java.lang.reflect.Constructor;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

/**
 * Allows you to pass a Loader function that when a key is accessed that
 * doesn't exist, automatically loads the entry into the map by calling
 * the loader function.
 *
 * <p><code>.get()</code>Will only return null if the Loader
 * can return null.</p>
 *
 * <p>You may pass any backing Map to use.</p>
 *
 * <p>This class is not thread safe and should be wrapped with
 * Collections.synchronizedMap on the OUTSIDE of the LoadingMap
 * if needed.</p>
 *
 * <p>Do not wrap the backing map with Collections.synchronizedMap.</p>
 *
 * @param <K> The key
 * @param <V> The value
 */
public class LoadingMap<K, V> extends AbstractMap<K, V> {

    private final Map<K, V> backingMap;
    private final Function<K, V> loader;

    /**
     * Initializes an auto loading map using the specified
     * loader and backing map.
     *
     * @param backingMap The backing map
     * @param loader The loader function
     */
    public LoadingMap(Map<K, V> backingMap, Function<K, V> loader) {
        this.backingMap = backingMap;
        this.loader = loader;
    }

    /**
     * Creates a new LoadingMap with the specified map and loader
     *
     * @param backingMap The backing map
     * @param loader The loader function
     * @param <K> The key
     * @param <V> The value
     * @return The loading map
     */
    public static <K, V> Map<K, V> of(Map<K, V> backingMap, Function<K, V> loader) {
        return new LoadingMap<>(backingMap, loader);
    }

    /**
     * Creates a LoadingMap with an auto instantiating loader.
     *
     * <p>Will auto construct class of value when not found.</p>
     *
     * <p>Since this uses Reflection, It is more efficient to define your own
     * static loader than using this helper, but if performance is not critical,
     * this is easier.</p>
     *
     * @param backingMap Actual map being used
     * @param keyClass Class used for the K generic
     * @param valueClass Class used for the V generic
     * @param <K> Key type of the Map
     * @param <V> Value type of the Map
     * @return Map that auto instantiates on <code>.get()</code>
     */
    public static <K, V> Map<K, V> newAutoMap(Map<K, V> backingMap, final Class<? extends K> keyClass, final Class<? extends V> valueClass) {
        return new LoadingMap<>(backingMap, new AutoInstantiatingLoader<>(keyClass, valueClass));
    }

    /**
     * Creates a LoadingMap with an auto instantiating loader.
     *
     * <p>Will auto construct class of value when not found.</p>
     *
     * <p>Since this uses Reflection, It is more efficient to define your own
     * static loader than using this helper, but if performance is not critical,
     * this is easier.</p>
     *
     * @param backingMap Actual map being used
     * @param valueClass Class used for the V generic
     * @param <K> Key type of the Map
     * @param <V> Value type of the Map
     * @return Map that auto instantiates on <code>.get()</code>
     */
    public static <K, V> Map<K, V> newAutoMap(Map<K, V> backingMap, final Class<? extends V> valueClass) {
        return newAutoMap(backingMap, null, valueClass);
    }

    /**
     * Creates a LoadingMap with an auto instantiating loader.
     *
     * <p>Will auto construct class of value when not found.</p>
     *
     * <p>Since this uses Reflection, It is more efficient to define your own
     * static loader than using this helper, but if performance is not critical,
     * this is easier.</p>
     *
     * <p>Uses a hashmap as its backing map.</p>
     *
     * @param keyClass Class used for the K generic
     * @param valueClass Class used for the V generic
     * @param <K> Key type of the Map
     * @param <V> Value type of the Map
     * @return Map that auto instantiates on <code>.get()</code>
     */
    public static <K, V> Map<K, V> newHashAutoMap(final Class<? extends K> keyClass, final Class<? extends V> valueClass) {
        return newAutoMap(new HashMap<>(), keyClass, valueClass);
    }

    /**
     * Creates a LoadingMap with an auto instantiating loader.
     *
     * <p>Will auto construct class of value when not found.</p>
     *
     * <p>Since this uses Reflection, It is more efficient to define your own
     * static loader than using this helper, but if performance is not critical,
     * this is easier.</p>
     *
     * <p>Uses a hashmap as its backing map.</p>
     *
     * @param valueClass Class used for the V generic
     * @param <K> Key type of the Map
     * @param <V> Value type of the Map
     * @return Map that auto instantiates on <code>.get()</code>
     */
    public static <K, V> Map<K, V> newHashAutoMap(final Class<? extends V> valueClass) {
        return newHashAutoMap(null, valueClass);
    }

    /**
     * Creates a LoadingMap with an auto instantiating loader.
     *
     * <p>Will auto construct class of value when not found.</p>
     *
     * <p>Since this uses Reflection, It is more efficient to define your own
     * static loader than using this helper, but if performance is not critical,
     * this is easier.</p>
     *
     * <p>Uses a hashmap as its backing map.</p>
     *
     * @param keyClass Class used for the K generic
     * @param valueClass Class used for the V generic
     * @param initialCapacity The initial capacity of the map
     * @param loadFactor The load factor for the map
     * @param <K> Key type of the Map
     * @param <V> Value type of the Map
     * @return Map that auto instantiates on <code>.get()</code>
     */
    public static <K, V> Map<K, V> newHashAutoMap(final Class<? extends K> keyClass, final Class<? extends V> valueClass, int initialCapacity,
            float loadFactor) {
        return newAutoMap(new HashMap<>(initialCapacity, loadFactor), keyClass, valueClass);
    }

    /**
     * Creates a LoadingMap with an auto instantiating loader.
     *
     * <p>Will auto construct class of value when not found.</p>
     *
     * <p>Since this uses Reflection, It is more efficient to define your own
     * static loader than using this helper, but if performance is not critical,
     * this is easier.</p>
     *
     * <p>Uses a hashmap as its backing map.</p>
     *
     * @param valueClass Class used for the V generic
     * * @param initialCapacity The initial capacity of the map
     * @param loadFactor The load factor for the map
     * @param <K> Key type of the Map
     * @param <V> Value type of the Map
     * @return Map that auto instantiates on <code>.get()</code>
     */
    public static <K, V> Map<K, V> newHashAutoMap(final Class<? extends V> valueClass, int initialCapacity, float loadFactor) {
        return newHashAutoMap(null, valueClass, initialCapacity, loadFactor);
    }

    /**
     * Initializes an auto loading map using a HashMap.
     *
     * @param loader The loader function
     * @param <K> The key
     * @param <V> The value
     * @return The new loading map
     */
    public static <K, V> Map<K, V> newHashMap(Function<K, V> loader) {
        return new LoadingMap<>(new HashMap<>(), loader);
    }

    /**
     * Initializes an auto loading map using a HashMap.
     *
     * @param loader The loader function
     * @param initialCapacity The initial capacity of the map
     * @param loadFactor The load factor for the map
     * @param <K> The key
     * @param <V> The value
     * @return The new loading map
     */
    public static <K, V> Map<K, V> newHashMap(Function<K, V> loader, int initialCapacity, float loadFactor) {
        return new LoadingMap<>(new HashMap<>(initialCapacity, loadFactor), loader);
    }

    /**
     * Initializes an auto loading map using an Identity HashMap.
     *
     * @param loader The loader function
     * @param <K> The key
     * @param <V> The value
     * @return The new loading map
     */
    public static <K, V> Map<K, V> newIdentityHashMap(Function<K, V> loader) {
        return new LoadingMap<>(new IdentityHashMap<>(), loader);
    }

    /**
     * Initializes an auto loading map using an Identity HashMap.
     *
     * @param loader The loader function
     * @param initialCapacity The initial capacity for the map
     * @param <K> The key
     * @param <V> The value
     * @return The new loading map
     */
    public static <K, V> Map<K, V> newIdentityHashMap(Function<K, V> loader, int initialCapacity) {
        return new LoadingMap<>(new IdentityHashMap<>(initialCapacity), loader);
    }

    @Override
    public int size() {
        return this.backingMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.backingMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.backingMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.backingMap.containsValue(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
        V res = this.backingMap.get(key);
        if (res == null && key != null) {
            res = this.loader.apply((K) key);
            if (res != null) {
                this.backingMap.put((K) key, res);
            }
        }
        return res;
    }

    @Override
    public V put(K key, V value) {
        return this.backingMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return this.backingMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.backingMap.putAll(m);
    }

    @Override
    public void clear() {
        this.backingMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return this.backingMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.backingMap.values();
    }

    @Override
    public boolean equals(Object o) {
        return this.backingMap.equals(o);
    }

    @Override
    public int hashCode() {
        return this.backingMap.hashCode();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.backingMap.entrySet();
    }

    @Override
    public LoadingMap<K, V> clone() {
        return new LoadingMap<>(this.backingMap, this.loader);
    }

    private static class AutoInstantiatingLoader<K, V> implements Function<K, V> {

        final Constructor<? extends V> constructor;
        private final Class<? extends V> valueClass;

        AutoInstantiatingLoader(Class<? extends K> keyClass, Class<? extends V> valueClass) {
            try {
                this.valueClass = valueClass;
                if (keyClass != null) {
                    this.constructor = valueClass.getConstructor(keyClass);
                } else {
                    this.constructor = null;
                }
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        valueClass.getName() + " does not have a constructor for " + keyClass.getName());
            }
        }

        @Override
        public V apply(K input) {
            try {
                return (this.constructor != null ? this.constructor.newInstance(input) : this.valueClass.newInstance());
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object object) {
            return false;
        }
    }

    /**
     * Due to java stuff, you will need to cast it to {@link Function}
     * for some cases.
     *
     * @param <T> The value
     */
    public static abstract class Feeder<T> implements Function<Object, T> {

        @Override
        @Nullable
        public T apply(@Nullable Object input) {
            return apply();
        }

        public abstract T apply();

    }
}
