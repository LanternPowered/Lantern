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
package org.spongepowered.api.data.key;

import org.lanternpowered.server.data.key.LanternKeyFactory;
import org.lanternpowered.server.util.IdGenerator;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.SetValue;

/**
 * Override the default key factory with our own. All method signatures
 * must match the one of the api version.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class KeyFactory {

    private KeyFactory() {}

    // TODO: Use proper ids for the keys

    public static <E, T extends BaseValue, V extends BaseValue<E>> Key<V> makeSingleKey(
            final Class<E> elementClass, final Class<T> valueClass, final DataQuery query) {
        return LanternKeyFactory.makeSingleKey(elementClass, valueClass, query, "unknown", IdGenerator.generate(query.toString()));
    }

    public static <E> Key<ListValue<E>> makeListKey(final Class<E> elementClass, final DataQuery query) {
        return LanternKeyFactory.makeListKey(elementClass, query, "unknown", IdGenerator.generate(query.toString()));
    }

    public static <E> Key<SetValue<E>> makeSetKey(final Class<E> elementClass, final DataQuery query) {
        return LanternKeyFactory.makeSetKey(elementClass, query, "unknown", IdGenerator.generate(query.toString()));
    }

    public static <K, V> Key<MapValue<K, V>> makeMapKey(final Class<K> keyClass, final Class<V> valueclass, final DataQuery query) {
        return LanternKeyFactory.makeMapKey(keyClass, valueclass, query, "unknown", IdGenerator.generate(query.toString()));
    }

    public static <E> Key<OptionalValue<E>> makeOptionalKey(final Class<E> elementClass, final DataQuery query) {
        return LanternKeyFactory.makeOptionalKey(elementClass, query, "unknown", IdGenerator.generate(query.toString()));
    }

    static <E, V extends BaseValue<E>> Key<V> fake(final String keyName) {
        return new Key<V>() {
            @Override
            public Class<V> getValueClass() {
                throw new UnsupportedOperationException("Key " + keyName + " is not implemented");
            }

            @Override
            public DataQuery getQuery() {
                throw new UnsupportedOperationException("Key " + keyName + " is not implemented");
            }
        };
    }

}

