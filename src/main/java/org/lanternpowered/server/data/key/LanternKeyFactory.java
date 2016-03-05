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
    public static <E, T extends BaseValue, V extends BaseValue<E>> Key<V> makeSingleKey(final Class<E> elementClass, final Class<T> valueClass,
            final DataQuery query) {
        return new LanternKey<V, E>() {

            private final int hash = Objects.hash(elementClass, valueClass, query);
            private final TypeToken<E> elementType = TypeToken.of(elementClass);

            @Override
            public TypeToken<E> getElementType() {
                return this.elementType;
            }

            @SuppressWarnings({ "unchecked", "rawtypes"})
            @Override
            public Class<V> getValueClass() {
                return (Class<V>) (Class) valueClass;
            }

            @Override
            public DataQuery getQuery() {
                return query;
            }

            @Override
            public int hashCode() {
                return this.hash;
            }

            @Override
            public String toString() {
                return "Key{Value:" + valueClass.getSimpleName() + "<" + elementClass.getSimpleName() + ">, Query: " + query.toString() + "}";
            }
        };
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
    public static <E> Key<ListValue<E>> makeListKey(final Class<E> elementClass, final DataQuery query) {
        return new LanternKey<ListValue<E>, List<E>>() {

            private final int hash = Objects.hash(ListValue.class, elementClass, query);
            private final TypeToken<List<E>> elementType = new TypeToken<List<E>>() {}
                    .where(new TypeParameter<E>() {}, elementClass);

            @Override
            public TypeToken<List<E>> getElementType() {
                return this.elementType;
            }

            @SuppressWarnings({ "unchecked", "rawtypes"})
            @Override
            public Class<ListValue<E>> getValueClass() {
                return (Class<ListValue<E>>) (Class) ListValue.class;
            }

            @Override
            public DataQuery getQuery() {
                return query;
            }

            @Override
            public int hashCode() {
                return this.hash;
            }

            @Override
            public String toString() {
                return "Key{Value:ListValue<" + elementClass.getSimpleName() + ">, Query: " + query.toString() + "}";
            }
        };
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
    public static <E> Key<SetValue<E>> makeSetKey(final Class<E> elementClass, final DataQuery query) {
        return new LanternKey<SetValue<E>, Set<E>>() {

            private final int hash = Objects.hash(SetValue.class, elementClass, query);
            private final TypeToken<Set<E>> elementType = new TypeToken<Set<E>>() {}
                    .where(new TypeParameter<E>() {}, elementClass);

            @Override
            public TypeToken<Set<E>> getElementType() {
                return this.elementType;
            }

            @SuppressWarnings({ "unchecked", "rawtypes"})
            @Override
            public Class<SetValue<E>> getValueClass() {
                return (Class<SetValue<E>>) (Class) SetValue.class;
            }

            @Override
            public DataQuery getQuery() {
                return query;
            }

            @Override
            public int hashCode() {
                return this.hash;
            }

            @Override
            public String toString() {
                return "Key{Value:SetValue<" + elementClass.getSimpleName() + ">, Query: " + query.toString() + "}";
            }
        };
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
    public static <K, V> Key<MapValue<K, V>> makeMapKey(final Class<K> keyClass, final Class<V> valueClass, final DataQuery query) {
        return new LanternKey<MapValue<K, V>, Map<K, V>>() {

            private final int hash = Objects.hash(keyClass, valueClass, query);
            private final TypeToken<Map<K, V>> elementType = new TypeToken<Map<K, V>>() {}
                    .where(new TypeParameter<K>() {}, keyClass)
                    .where(new TypeParameter<V>() {}, valueClass);

            @Override
            public TypeToken<Map<K, V>> getElementType() {
                return this.elementType;
            }

            @SuppressWarnings({ "unchecked", "rawtypes"})
            @Override
            public Class<MapValue<K, V>> getValueClass() {
                return (Class<MapValue<K, V>>) (Class) MapValue.class;
            }

            @Override
            public DataQuery getQuery() {
                return query;
            }

            @Override
            public int hashCode() {
                return this.hash;
            }

            @Override
            public String toString() {
                return "Key{Value:MapValue<" + keyClass.getSimpleName() + "," + valueClass.getSimpleName() + ">, Query: " + query.toString() + "}";
            }
        };
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
    public static <E> Key<OptionalValue<E>> makeOptionalKey(final Class<E> elementClass, final DataQuery query) {
        return new LanternKey<OptionalValue<E>, Optional<E>>() {

            private final int hash = Objects.hash(Optional.class, elementClass, query);
            private final TypeToken<Optional<E>> elementType = new TypeToken<Optional<E>>() {}
                    .where(new TypeParameter<E>() {}, elementClass);

            @Override
            public TypeToken<Optional<E>> getElementType() {
                return this.elementType;
            }

            @SuppressWarnings({ "unchecked", "rawtypes"})
            @Override
            public Class<OptionalValue<E>> getValueClass() {
                return (Class<OptionalValue<E>>) (Class<?>) OptionalValue.class;
            }

            @Override
            public DataQuery getQuery() {
                return query;
            }

            @Override
            public int hashCode() {
                return this.hash;
            }

            @Override
            public String toString() {
                return "Key{Value:OptionalValue<" + elementClass.getSimpleName() + ">, Query: " + query.toString() + "}";
            }
        };
    }

    /**
     * Creates a new {@link Key} based on a {@link PatternListValue} along with the provided {@link DataQuery}.
     *
     * @param query The query to access the data
     * @return The generated key
     */
    public static Key<PatternListValue> makePatternListKey(final DataQuery query) {
        return new LanternKey<PatternListValue, List<PatternLayer>>() {

            private final int hash = Objects.hash(PatternListValue.class, query);
            private final TypeToken<List<PatternLayer>> elementType = new TypeToken<List<PatternLayer>>() {};

            @Override
            public Class<PatternListValue> getValueClass() {
                return PatternListValue.class;
            }

            @Override
            public TypeToken<List<PatternLayer>> getElementType() {
                return this.elementType;
            }

            @Override
            public DataQuery getQuery() {
                return query;
            }

            @Override
            public int hashCode() {
                return this.hash;
            }

            @Override
            public String toString() {
                return "Key{Value:PatternListValue, Query: " + query.toString() + "}";
            }
        };
    }

    /**
     * Creates a new {@link Key} based on a {@link WeightedCollectionValue} along with the provided element
     * <code>E<code> and the {@link DataQuery}.
     *
     * @param query The query to access the data
     * @return The generated key
     */
    public static <E> Key<WeightedCollectionValue<E>> makeWeightedCollectionKey(final Class<E> elementClass, final DataQuery query) {
        return new LanternKey<WeightedCollectionValue<E>, WeightedTable<E>>() {

            private final int hash = Objects.hash(WeightedCollectionValue.class, elementClass, query);
            private final TypeToken<WeightedTable<E>> elementType = new TypeToken<WeightedTable<E>>() {}
                    .where(new TypeParameter<E>() {}, elementClass);

            @Override
            public TypeToken<WeightedTable<E>> getElementType() {
                return this.elementType;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Class<WeightedCollectionValue<E>> getValueClass() {
                return (Class) WeightedCollectionValue.class;
            }

            @Override
            public DataQuery getQuery() {
                return query;
            }

            @Override
            public int hashCode() {
                return this.hash;
            }

            @Override
            public String toString() {
                return "Key{Value:WeightedCollectionValue<" + elementClass.getSimpleName() + ">, Query: " + query.toString() + "}";
            }
        };
    }

    /**
     * Creates a new {@link Key} based on a {@link MobSpawnerData.NextEntityToSpawnValue} along with the provided {@link DataQuery}.
     *
     * @param query The query to access the data
     * @return The generated key
     */
    public static Key<MobSpawnerData.NextEntityToSpawnValue> makeNextEntityToSpawnKey(final DataQuery query) {
        return new LanternKey<MobSpawnerData.NextEntityToSpawnValue, WeightedSerializableObject<EntitySnapshot>>() {

            private final TypeToken<WeightedSerializableObject<EntitySnapshot>> elementType = new TypeToken<WeightedSerializableObject<EntitySnapshot>>() {};
            private final int hash = Objects.hash(MobSpawnerData.NextEntityToSpawnValue.class, query);

            @Override
            public TypeToken<WeightedSerializableObject<EntitySnapshot>> getElementType() {
                return this.elementType;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Class<MobSpawnerData.NextEntityToSpawnValue> getValueClass() {
                return MobSpawnerData.NextEntityToSpawnValue.class;
            }

            @Override
            public DataQuery getQuery() {
                return query;
            }

            @Override
            public int hashCode() {
                return this.hash;
            }

            @Override
            public String toString() {
                return "Key{Value:MobSpawnerData.NextEntityToSpawnValue, Query: " + query.toString() + "}";
            }
        };
    }

    private LanternKeyFactory() {
    }
}
