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
package org.lanternpowered.server.data.manipulator.immutable;

import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.data.manipulator.mutable.IMappedData;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.ImmutableMappedData;
import org.spongepowered.api.data.manipulator.mutable.MappedData;
import org.spongepowered.api.data.value.immutable.ImmutableMapValue;
import org.spongepowered.api.data.value.mutable.MapValue;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AbstractImmutableMappedData<K, V, I extends ImmutableMappedData<K, V, I, M>, M extends MappedData<K, V, M, I>>
        extends AbstractImmutableData<I, M> implements IImmutableMappedData<K, V, I, M> {

    private final Key<? extends MapValue<K, V>> mapKey;

    public AbstractImmutableMappedData(Class<I> immutableManipulatorType, Class<M> manipulatorType, Key<MapValue<K, V>> mapKey) {
        this(immutableManipulatorType, manipulatorType, mapKey, ImmutableMap.of());
    }

    public AbstractImmutableMappedData(Class<I> immutableManipulatorType, Class<M> manipulatorType, Key<MapValue<K, V>> mapKey, Map<K, V> map) {
        super(immutableManipulatorType, manipulatorType);
        getValueCollection().register(mapKey, ImmutableMap.copyOf(map));
        this.mapKey = mapKey;
    }

    public AbstractImmutableMappedData(M manipulator) {
        super(manipulator);
        //noinspection unchecked
        this.mapKey = ((IMappedData<K, V, M, I>) manipulator).getMapKey();
    }

    @Override
    public Key<? extends MapValue<K, V>> getMapKey() {
        return this.mapKey;
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
    public ImmutableMapValue<K, V> getMapValue() {
        return tryGetImmutableValueFor(this.mapKey);
    }
}
