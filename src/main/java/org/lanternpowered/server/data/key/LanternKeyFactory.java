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
package org.lanternpowered.server.data.key;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.immutable.ImmutableBoundedValue;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.PatternListValue;
import org.spongepowered.api.data.value.mutable.SetValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.data.value.mutable.WeightedCollectionValue;

@SuppressWarnings("deprecation") // Ugh, shouldn't be deprecated
public final class LanternKeyFactory {

    public static <E> Key<Value<E>> makeValueKey(TypeToken<E> elementToken,
            DataQuery query, String id, String name) {
        final TypeToken<Value<E>> valueToken = new TypeToken<Value<E>>() {}
                .where(new TypeParameter<E>() {}, elementToken);
        return new LanternKeyBuilder<>().type(valueToken).query(query).id(id).name(name).build();
    }

    public static <E> Key<Value<E>> makeValueKey(TypeToken<E> elementToken,
            DataQuery query, String id) {
        return makeValueKey(elementToken, query, id, query.last().toString());
    }

    public static <E> Key<Value<E>> makeValueKey(Class<E> elementType,
            DataQuery query, String id, String name) {
        return makeValueKey(TypeToken.of(elementType), query, id, name);
    }

    public static <E> Key<Value<E>> makeValueKey(Class<E> elementType,
            DataQuery query, String id) {
        return makeValueKey(TypeToken.of(elementType), query, id, query.last().toString());
    }

    public static <E> Key<MutableBoundedValue<E>> makeMutableBoundedValueKey(TypeToken<E> elementToken,
            DataQuery query, String id, String name) {
        final TypeToken<MutableBoundedValue<E>> valueToken = new TypeToken<MutableBoundedValue<E>>() {}
                .where(new TypeParameter<E>() {}, elementToken);
        return new LanternKeyBuilder<>().type(valueToken).query(query).id(id).name(name).build();
    }

    public static <E> Key<MutableBoundedValue<E>> makeMutableBoundedValueKey(TypeToken<E> elementToken,
            DataQuery query, String id) {
        return makeMutableBoundedValueKey(elementToken, query, id, query.last().toString());
    }

    public static <E> Key<MutableBoundedValue<E>> makeMutableBoundedValueKey(Class<E> elementType,
            DataQuery query, String id, String name) {
        return makeMutableBoundedValueKey(TypeToken.of(elementType), query, id, name);
    }

    public static <E> Key<MutableBoundedValue<E>> makeMutableBoundedValueKey(Class<E> elementType,
            DataQuery query, String id) {
        return makeMutableBoundedValueKey(TypeToken.of(elementType), query, id, query.last().toString());
    }

    public static <E> Key<ImmutableBoundedValue<E>> makeImmutableBoundedValueKey(TypeToken<E> elementToken,
            DataQuery query, String id, String name) {
        final TypeToken<ImmutableBoundedValue<E>> valueToken = new TypeToken<ImmutableBoundedValue<E>>() {}
                .where(new TypeParameter<E>() {}, elementToken);
        return new LanternKeyBuilder<>().type(valueToken).query(query).id(id).name(name).build();
    }

    public static <E> Key<ImmutableBoundedValue<E>> makeImmutableBoundedValueKey(TypeToken<E> elementToken,
            DataQuery query, String id) {
        return makeImmutableBoundedValueKey(elementToken, query, id, query.last().toString());
    }

    public static <E> Key<ImmutableBoundedValue<E>> makeImmutableBoundedValueKey(Class<E> elementType,
            DataQuery query, String id, String name) {
        return makeImmutableBoundedValueKey(TypeToken.of(elementType), query, id, name);
    }

    public static <E> Key<ImmutableBoundedValue<E>> makeImmutableBoundedValueKey(Class<E> elementType,
            DataQuery query, String id) {
        return makeImmutableBoundedValueKey(TypeToken.of(elementType), query, id, query.last().toString());
    }

    public static <E> Key<ListValue<E>> makeListKey(TypeToken<E> elementToken,
            DataQuery query, String id, String name) {
        final TypeToken<ListValue<E>> valueToken = new TypeToken<ListValue<E>>() {}
                .where(new TypeParameter<E>() {}, elementToken);
        return new LanternKeyBuilder<>().type(valueToken).query(query).id(id).name(name).build();
    }

    public static <E> Key<ListValue<E>> makeListKey(TypeToken<E> elementToken,
            DataQuery query, String id) {
        return makeListKey(elementToken, query, id, query.last().toString());
    }

    public static <E> Key<ListValue<E>> makeListKey(Class<E> elementType,
            DataQuery query, String id, String name) {
        return makeListKey(TypeToken.of(elementType), query, id, name);
    }

    public static <E> Key<ListValue<E>> makeListKey(Class<E> elementType,
            DataQuery query, String id) {
        return makeListKey(TypeToken.of(elementType), query, id, query.last().toString());
    }

    public static Key<PatternListValue> makePatternListKey(DataQuery query, String id, String name) {
        final TypeToken<PatternListValue> valueToken = new TypeToken<PatternListValue>() {};
        return new LanternKeyBuilder<>().type(valueToken).query(query).id(id).name(name).build();
    }

    public static Key<PatternListValue> makePatternListKey(DataQuery query, String id) {
        return makePatternListKey(query, id, query.last().toString());
    }

    public static <E> Key<SetValue<E>> makeSetKey(TypeToken<E> elementToken,
            DataQuery query, String id, String name) {
        final TypeToken<SetValue<E>> valueToken = new TypeToken<SetValue<E>>() {}
                .where(new TypeParameter<E>() {}, elementToken);
        return new LanternKeyBuilder<>().type(valueToken).query(query).id(id).name(name).build();
    }

    public static <E> Key<SetValue<E>> makeSetKey(TypeToken<E> elementToken,
            DataQuery query, String id) {
        return makeSetKey(elementToken, query, id, query.last().toString());
    }

    public static <E> Key<SetValue<E>> makeSetKey(Class<E> elementType,
            DataQuery query, String id, String name) {
        return makeSetKey(TypeToken.of(elementType), query, id, name);
    }

    public static <E> Key<SetValue<E>> makeSetKey(Class<E> elementType,
            DataQuery query, String id) {
        return makeSetKey(TypeToken.of(elementType), query, id, query.last().toString());
    }

    public static <E> Key<OptionalValue<E>> makeOptionalKey(TypeToken<E> elementToken,
            DataQuery query, String id, String name) {
        final TypeToken<OptionalValue<E>> valueToken = new TypeToken<OptionalValue<E>>() {}
                .where(new TypeParameter<E>() {}, elementToken);
        return new LanternKeyBuilder<>().type(valueToken).query(query).id(id).name(name).build();
    }

    public static <E> Key<OptionalValue<E>> makeOptionalKey(TypeToken<E> elementToken,
            DataQuery query, String id) {
        return makeOptionalKey(elementToken, query, id, query.last().toString());
    }

    public static <E> Key<OptionalValue<E>> makeOptionalKey(Class<E> elementType,
            DataQuery query, String id, String name) {
        return makeOptionalKey(TypeToken.of(elementType), query, id, name);
    }

    public static <E> Key<OptionalValue<E>> makeOptionalKey(Class<E> elementType,
            DataQuery query, String id) {
        return makeOptionalKey(TypeToken.of(elementType), query, id);
    }

    public static <K, V> Key<MapValue<K, V>> makeMapKeyWithKeyAndValue(TypeToken<K> keyToken, TypeToken<V> valueToken,
            DataQuery query, String id, String name) {
        final TypeToken<MapValue<K, V>> valueToken0 = new TypeToken<MapValue<K, V>>() {}
                .where(new TypeParameter<K>() {}, keyToken)
                .where(new TypeParameter<V>() {}, valueToken);
        return new LanternKeyBuilder<>().type(valueToken0).query(query).id(id).name(name).build();
    }

    public static <K, V> Key<MapValue<K, V>> makeMapKeyWithKeyAndValue(TypeToken<K> keyToken, TypeToken<V> valueToken,
            DataQuery query, String id) {
        return makeMapKeyWithKeyAndValue(keyToken, valueToken, query, id, query.last().toString());
    }

    public static <K, V> Key<MapValue<K, V>> makeMapKeyWithKeyAndValue(Class<K> keyType, Class<V> valueType,
            DataQuery query, String id, String name) {
        return makeMapKeyWithKeyAndValue(TypeToken.of(keyType), TypeToken.of(valueType), query, id, name);
    }

    public static <K, V> Key<MapValue<K, V>> makeMapKeyWithKeyAndValue(Class<K> keyType, Class<V> valueType,
            DataQuery query, String id) {
        return makeMapKeyWithKeyAndValue(TypeToken.of(keyType), TypeToken.of(valueType), query, id, query.last().toString());
    }

    public static <E> Key<WeightedCollectionValue<E>> makeWeightedCollectionKey(TypeToken<E> elementToken,
            DataQuery query, String id, String name) {
        final TypeToken<WeightedCollectionValue<E>> valueToken = new TypeToken<WeightedCollectionValue<E>>() {}
                .where(new TypeParameter<E>() {}, elementToken);
        return new LanternKeyBuilder<>().type(valueToken).query(query).id(id).name(name).build();
    }

    public static <E> Key<WeightedCollectionValue<E>> makeWeightedCollectionKey(TypeToken<E> elementToken,
            DataQuery query, String id) {
        return makeWeightedCollectionKey(elementToken, query, id, query.last().toString());
    }

    public static <E> Key<WeightedCollectionValue<E>> makeWeightedCollectionKey(Class<E> elementType,
            DataQuery query, String id) {
        return makeWeightedCollectionKey(TypeToken.of(elementType), query, id);
    }

    public static <E> Key<WeightedCollectionValue<E>> makeWeightedCollectionKey(Class<E> elementType,
            DataQuery query, String id, String name) {
        return makeWeightedCollectionKey(TypeToken.of(elementType), query, id, name);
    }

    private LanternKeyFactory() {
    }
}
