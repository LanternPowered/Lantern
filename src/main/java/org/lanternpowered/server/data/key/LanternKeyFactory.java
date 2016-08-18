/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.data.key;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.mutable.MobSpawnerData;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.PatternListValue;
import org.spongepowered.api.data.value.mutable.SetValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.data.value.mutable.WeightedCollectionValue;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.util.weighted.WeightedSerializableObject;
import org.spongepowered.api.util.weighted.WeightedTable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class LanternKeyFactory {

    private static class SimpleKey<V extends BaseValue<E>, E> extends PluginCatalogType.Base implements LanternKey<V, E> {

        final Class<V> valueClass;
        final TypeToken<E> elementType;
        final DataQuery query;
        private final int hash;
        private final String string;

        SimpleKey(String pluginId, String name, Class<V> valueClass, TypeToken<E> elementType, DataQuery query,
                int hash, String string) {
            super(pluginId, name);
            this.valueClass = valueClass;
            this.elementType = elementType;
            this.query = query;
            this.hash = hash;
            this.string = string;
        }

        @Override
        public TypeToken<E> getElementType() {
            return this.elementType;
        }

        @Override
        public Class<V> getValueClass() {
            return this.valueClass;
        }

        @Override
        public DataQuery getQuery() {
            return this.query;
        }

        @Override
        public int hashCode() {
            return this.hash;
        }

        @Override
        public String toString() {
            return this.string;
        }
    }

    /**
     * Creates a new {@link Key} with the provided <code>E</code> element
     * class and <code>V</code> {@link Value} class along with the provided
     * default {@link DataQuery} to be used with the generated {@link Key}.
     *
     * <p>Note that {@link Key}s are not registered, but it is recommended
     * to avoid generating {@link Key}s of potentially conflicting
     * {@link DataQuery}(s).</p>
     *
     * @param elementClass The element class
     * @param valueClass The value class
     * @param query The query
     * @param <E> The type of element
     * @param <T> The type of base value class
     * @param <V> The inferred return type
     * @return The generated key
     */
    @SuppressWarnings("unchecked")
    public static <E, T extends BaseValue, V extends BaseValue<E>> LanternKey<V, E> makeSingleKey(
            Class<E> elementClass, Class<T> valueClass, DataQuery query, String pluginId, String name) {
        final TypeToken<E> typeToken = TypeToken.of(elementClass);
        final String toString = String.format("Key{Value:%s<%s>,Query:%s}", valueClass.getSimpleName(), typeToken, query);
        final int hash = Objects.hash(elementClass, valueClass, query);
        return new SimpleKey<>(pluginId, name, (Class) valueClass, typeToken, query, hash, toString);
    }

    public static <E, T extends BaseValue, V extends BaseValue<E>> LanternKey<V, E> makeSingleKey(
            Class<E> elementClass, Class<T> valueClass, DataQuery query, String name) {
        return makeSingleKey(elementClass, valueClass, query, "minecraft", name);
    }

    /**
     * Creates a new {@link Key} based on a {@link ListValue} of a type
     * <code>E</code> element along with the provided {@link DataQuery}.
     *
     * @param elementClass The element class
     * @param query The query to access the data
     * @param <E> The type of element
     * @return The generated key
     */
    @SuppressWarnings("unchecked")
    public static <E> LanternKey<ListValue<E>, List<E>> makeListKey(
            Class<E> elementClass, DataQuery query, String pluginId, String name) {
        final TypeToken<List<E>> typeToken = new TypeToken<List<E>>() {}.where(new TypeParameter<E>() {}, elementClass);
        final String toString = String.format("Key{Value:ListValue<%s>,Query:%s}", typeToken, query);
        final int hash = Objects.hash(ListValue.class, elementClass, query);
        return new SimpleKey<>(pluginId, name, (Class) ListValue.class, typeToken, query, hash, toString);
    }

    public static <E> LanternKey<ListValue<E>, List<E>> makeListKey(
            Class<E> elementClass, DataQuery query, String name) {
        return makeListKey(elementClass, query, "minecraft", name);
    }

    /**
     * Creates a new {@link Key} based on a {@link SetValue} of a type
     * <code>E</code> element along with the provided {@link DataQuery}.
     *
     * @param elementClass The element class
     * @param query The query to access the data
     * @param <E> The type of element
     * @return The generated key
     */
    @SuppressWarnings("unchecked")
    public static <E> LanternKey<SetValue<E>, Set<E>> makeSetKey(
            Class<E> elementClass, DataQuery query, String pluginId, String name) {
        final TypeToken<Set<E>> typeToken = new TypeToken<Set<E>>() {}.where(new TypeParameter<E>() {}, elementClass);
        final String toString = String.format("Key{Value:SetValue<%s>,Query:%s}", typeToken, query);
        final int hash = Objects.hash(SetValue.class, elementClass, query);
        return new SimpleKey<>(pluginId, name, (Class) SetValue.class, typeToken, query, hash, toString);
    }

    public static <E> LanternKey<SetValue<E>, Set<E>> makeSetKey(
            Class<E> elementClass, DataQuery query, String name) {
        return makeSetKey(elementClass, query, "minecraft", name);
    }

    /**
     * Creates a new {@link Key} based on a {@link MapValue} of the types
     * <code>K</code> keys and <code>V</code> values with the provided
     * {@link DataQuery} for accessing the {@link Map} in {@link DataView}s.
     *
     * @param keyClass The key class of the map
     * @param valueClass The value class of the map
     * @param query The query
     * @param <K> The type of keys
     * @param <V> The type of values
     * @return The generated key
     */
    @SuppressWarnings("unchecked")
    public static <K, V> LanternKey<MapValue<K, V>, Map<K, V>> makeMapKey(
            Class<K> keyClass, Class<V> valueClass, DataQuery query, String pluginId, String name) {
        final TypeToken<Map<K, V>> typeToken = new TypeToken<Map<K, V>>() {}
                .where(new TypeParameter<K>() {}, keyClass)
                .where(new TypeParameter<V>() {}, valueClass);
        final String toString = String.format("Key{Value:MapValue<%s,%s>,Query:%s}", keyClass.getName(), valueClass.getName(), query);
        final int hash = Objects.hash(keyClass, valueClass, query);
        return new SimpleKey<>(pluginId, name, (Class) MapValue.class, typeToken, query, hash, toString);
    }

    public static <K, V> LanternKey<MapValue<K, V>, Map<K, V>> makeMapKey(
            Class<K> keyClass, Class<V> valueClass, DataQuery query, String name) {
        return makeMapKey(keyClass, valueClass, query, "minecraft", name);
    }

    /**
     * Creates a new {@link Key} based on an {@link OptionalValue} of the type
     * <code>E</code> element type with the provided {@link DataQuery} for
     * accessing the optionally null value in {@link DataView}s.
     *
     * @param elementClass The element class
     * @param query The query
     * @param <E> The element type
     * @return The generated key
     */
    @SuppressWarnings("unchecked")
    public static <E> LanternKey<OptionalValue<E>, Optional<E>> makeOptionalKey(
            Class<E> elementClass, DataQuery query, String pluginId, String name) {
        final TypeToken<Optional<E>> typeToken = new TypeToken<Optional<E>>() {}.where(new TypeParameter<E>() {}, elementClass);
        final String toString = String.format("Key{Value:OptionalValue<%s>,Query:%s}", elementClass.getName(), query);
        final int hash = Objects.hash(Optional.class, elementClass, query);
        return new SimpleKey<>(pluginId, name, (Class) OptionalValue.class, typeToken, query, hash, toString);
    }

    public static <E> LanternKey<OptionalValue<E>, Optional<E>> makeOptionalKey(
            Class<E> elementClass, DataQuery query, String name) {
        return makeOptionalKey(elementClass, query, "minecraft", name);
    }

    /**
     * Creates a new {@link Key} based on a {@link PatternListValue} along with the provided {@link DataQuery}.
     *
     * @param query The query to access the data
     * @return The generated key
     */
    public static LanternKey<PatternListValue, List<PatternLayer>> makePatternListKey(DataQuery query, String pluginId, String name) {
        final TypeToken<List<PatternLayer>> typeToken = new TypeToken<List<PatternLayer>>() {};
        final String toString = String.format("Key{Value:PatternListValue,Query:%s}", query);
        final int hash = Objects.hash(PatternListValue.class, query);
        return new SimpleKey<>(pluginId, name, PatternListValue.class, typeToken, query, hash, toString);
    }

    public static LanternKey<PatternListValue, List<PatternLayer>> makePatternListKey(DataQuery query, String name) {
        return makePatternListKey(query, "minecraft", name);
    }

    /**
     * Creates a new {@link Key} based on a {@link WeightedCollectionValue} along with the provided element
     * <code>E<code> and the {@link DataQuery}.
     *
     * @param query The query to access the data
     * @return The generated key
     */
    @SuppressWarnings("unchecked")
    public static <E> LanternKey<WeightedCollectionValue<E>, WeightedTable<E>> makeWeightedCollectionKey(
            Class<E> elementClass, DataQuery query, String pluginId, String name) {
        final TypeToken<WeightedTable<E>> typeToken = new TypeToken<WeightedTable<E>>() {}.where(new TypeParameter<E>() {}, elementClass);
        final String toString = String.format("Key{Value:WeightedCollectionValue<%s>,Query:%s}", elementClass.getName(), query);
        final int hash = Objects.hash(PatternListValue.class, query);
        return new SimpleKey<>(pluginId, name, (Class) WeightedCollectionValue.class, typeToken, query, hash, toString);
    }

    public static <E> LanternKey<WeightedCollectionValue<E>, WeightedTable<E>> makeWeightedCollectionKey(
            Class<E> elementClass, DataQuery query, String name) {
        return makeWeightedCollectionKey(elementClass, query, "minecraft", name);
    }

    /**
     * Creates a new {@link Key} based on a {@link MobSpawnerData.NextEntityToSpawnValue} along with the provided {@link DataQuery}.
     *
     * @param query The query to access the data
     * @return The generated key
     */
    public static LanternKey<MobSpawnerData.NextEntityToSpawnValue, WeightedSerializableObject<EntitySnapshot>> makeNextEntityToSpawnKey(
            DataQuery query, String pluginId, String name) {
        final TypeToken<WeightedSerializableObject<EntitySnapshot>> typeToken = new TypeToken<WeightedSerializableObject<EntitySnapshot>>() {};
        final String toString = String.format("Key{Value:MobSpawnerData.NextEntityToSpawnValue,Query:%s}", query);
        final int hash = Objects.hash(PatternListValue.class, query);
        return new SimpleKey<>(pluginId, name, MobSpawnerData.NextEntityToSpawnValue.class, typeToken, query, hash, toString);
    }

    public static LanternKey<MobSpawnerData.NextEntityToSpawnValue, WeightedSerializableObject<EntitySnapshot>> makeNextEntityToSpawnKey(
            DataQuery query, String name) {
        return makeNextEntityToSpawnKey(query, "minecraft", name);
    }

    private LanternKeyFactory() {
    }
}
