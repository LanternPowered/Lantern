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
package org.lanternpowered.server.data.value.mutable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.data.value.immutable.ImmutableLanternMapValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableMapValue;
import org.spongepowered.api.data.value.mutable.MapValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class LanternMapValue<K, V> extends LanternValue<Map<K, V>> implements MapValue<K, V> {

    public LanternMapValue(Key<? extends BaseValue<Map<K, V>>> key) {
        this(key, Collections.emptyMap());
    }

    public LanternMapValue(Key<? extends BaseValue<Map<K, V>>> key, Map<K, V> actualValue) {
        this(key, Collections.emptyMap(), actualValue);
    }

    public LanternMapValue(Key<? extends BaseValue<Map<K, V>>> key, Map<K, V> defaultMap, Map<K, V> actualMap) {
        super(key, ImmutableMap.copyOf(defaultMap), actualMap);
    }

    @Override
    public MapValue<K, V> transform(Function<Map<K, V>, Map<K, V>> function) {
        this.actualValue = new HashMap<>(checkNotNull(checkNotNull(function).apply(this.actualValue)));
        return this;
    }

    @Override
    public ImmutableMapValue<K, V> asImmutable() {
        return new ImmutableLanternMapValue<>(getKey(), getDefault(), getActualValue());
    }

    @Override
    public int size() {
        return getActualValue().size();
    }

    @Override
    public MapValue<K, V> put(K key, V value) {
        getActualValue().put(checkNotNull(key), checkNotNull(value));
        return this;
    }

    @Override
    public MapValue<K, V> putAll(Map<K, V> map) {
        getActualValue().putAll(checkNotNull(map));
        return this;
    }

    @Override
    public MapValue<K, V> remove(K key) {
        getActualValue().remove(checkNotNull(key));
        return this;
    }

    @Override
    public MapValue<K, V> removeAll(Iterable<K> keys) {
        for (K key : keys) {
            getActualValue().remove(checkNotNull(key));
        }
        return this;
    }

    @Override
    public MapValue<K, V> removeAll(Predicate<Map.Entry<K, V>> predicate) {
        for (Iterator<Map.Entry<K, V>> iterator = getActualValue().entrySet().iterator(); iterator.hasNext(); ) {
            final Map.Entry<K, V> entry = iterator.next();
            if (!checkNotNull(predicate).test(entry)) {
                iterator.remove();
            }
        }
        return this;
    }

    @Override
    public boolean containsKey(K key) {
        return getActualValue().containsKey(checkNotNull(key));
    }

    @Override
    public boolean containsValue(V value) {
        return getActualValue().containsValue(checkNotNull(value));
    }

    @Override
    public ImmutableSet<K> keySet() {
        return ImmutableSet.copyOf(getActualValue().keySet());
    }

    @Override
    public ImmutableSet<Map.Entry<K, V>> entrySet() {
        return ImmutableSet.copyOf(getActualValue().entrySet());
    }

    @Override
    public ImmutableCollection<V> values() {
        return ImmutableSet.copyOf(getActualValue().values());
    }
}
