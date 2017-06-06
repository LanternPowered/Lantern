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
package org.lanternpowered.server.data.manipulator.mutable;

import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.data.manipulator.IDataManipulatorBase;
import org.lanternpowered.server.data.manipulator.immutable.IImmutableMappedData;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.ImmutableMappedData;
import org.spongepowered.api.data.manipulator.mutable.MappedData;
import org.spongepowered.api.data.value.mutable.MapValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractMappedData<K, V, M extends MappedData<K, V, M, I>, I extends ImmutableMappedData<K, V, I, M>>
        extends AbstractData<M, I> implements MappedData<K, V, M, I> {

    private final Key<MapValue<K, V>> mapKey;

    public AbstractMappedData(Class<M> manipulatorType, Class<I> immutableManipulatorType, Key<MapValue<K, V>> mapKey, Map<K, V> defaultMap) {
        super(manipulatorType, immutableManipulatorType);
        getValueCollection().register(mapKey, defaultMap);
        this.mapKey = mapKey;
    }

    public AbstractMappedData(I manipulator) {
        //noinspection unchecked
        this((IDataManipulatorBase<M, I>) manipulator);
    }

    public AbstractMappedData(M manipulator) {
        //noinspection unchecked
        this((IDataManipulatorBase<M, I>) manipulator);
    }

    protected AbstractMappedData(IDataManipulatorBase<M, I> manipulator) {
        super(manipulator);
        if (manipulator instanceof IMappedData) {
            //noinspection unchecked
            this.mapKey = ((IMappedData) manipulator).getMapKey();
        } else {
            //noinspection unchecked
            this.mapKey = ((IImmutableMappedData) manipulator).getMapKey();
        }
    }
    @Override
    public Optional<V> get(K key) {
        return Optional.ofNullable(get(this.mapKey).get().get(key));
    }

    @Override
    public Set<K> getMapKeys() {
        return get(this.mapKey).get().keySet();
    }

    @Override
    public MapValue<K, V> getMapValue() {
        return getValue(this.mapKey).get();
    }

    @Override
    public M put(K key, V value) {
        Map<K, V> theMap = get(this.mapKey).get();
        if (theMap instanceof ImmutableMap) {
            theMap = ImmutableMap.<K, V>builder().putAll(theMap).put(key, value).build();
        } else {
            theMap.put(key, value);
        }
        set(this.mapKey, theMap);
        //noinspection unchecked
        return (M) this;
    }

    @Override
    public M putAll(Map<? extends K, ? extends V> map) {
        Map<K, V> theMap = get(this.mapKey).get();
        if (theMap instanceof ImmutableMap) {
            theMap = ImmutableMap.<K, V>builder().putAll(theMap).putAll(map).build();
        } else {
            theMap.putAll(map);
        }
        set(this.mapKey, theMap);
        //noinspection unchecked
        return (M) this;
    }

    @Override
    public M remove(K key) {
        Map<K, V> theMap = get(this.mapKey).get();
        if (theMap instanceof ImmutableMap) {
            theMap = new HashMap<>(theMap);
            theMap.remove(key);
            theMap = ImmutableMap.copyOf(theMap);
        } else {
            theMap.remove(key);
        }
        set(this.mapKey, theMap);
        //noinspection unchecked
        return (M) this;
    }
}
