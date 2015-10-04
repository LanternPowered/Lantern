package org.lanternpowered.server.data.value.mutable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import org.lanternpowered.server.data.value.immutable.ImmutableLanternMapValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableMapValue;
import org.spongepowered.api.data.value.mutable.MapValue;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class LanternMapValue<K, V> extends LanternValue<Map<K, V>> implements MapValue<K, V> {

    public LanternMapValue(Key<? extends BaseValue<Map<K, V>>> key) {
        this(key, Maps.<K, V>newHashMap());
    }

    public LanternMapValue(Key<? extends BaseValue<Map<K, V>>> key, Map<K, V> actualValue) {
        this(key, ImmutableMap.<K, V>of(), actualValue);
    }

    public LanternMapValue(Key<? extends BaseValue<Map<K, V>>> key, Map<K, V> defaultMap, Map<K, V> actualMap) {
        super(key, ImmutableMap.copyOf(defaultMap), Maps.newHashMap(actualMap));
    }

    @Override
    public MapValue<K, V> transform(Function<Map<K, V>, Map<K, V>> function) {
        this.actualValue = Maps.newHashMap(checkNotNull(checkNotNull(function).apply(this.actualValue)));
        return this;
    }

    @Override
    public ImmutableMapValue<K, V> asImmutable() {
        return new ImmutableLanternMapValue<K, V>(getKey(), ImmutableMap.copyOf(this.actualValue));
    }

    @Override
    public int size() {
        return this.actualValue.size();
    }

    @Override
    public MapValue<K, V> put(K key, V value) {
        this.actualValue.put(checkNotNull(key), checkNotNull(value));
        return this;
    }

    @Override
    public MapValue<K, V> putAll(Map<K, V> map) {
        this.actualValue.putAll(checkNotNull(map));
        return this;
    }

    @Override
    public MapValue<K, V> remove(K key) {
        this.actualValue.remove(checkNotNull(key));
        return this;
    }

    @Override
    public MapValue<K, V> removeAll(Iterable<K> keys) {
        for (K key : keys) {
            this.actualValue.remove(checkNotNull(key));
        }
        return this;
    }

    @Override
    public MapValue<K, V> removeAll(Predicate<Map.Entry<K, V>> predicate) {
        for (Iterator<Map.Entry<K, V>> iterator = this.actualValue.entrySet().iterator(); iterator.hasNext(); ) {
            final Map.Entry<K, V> entry = iterator.next();
            if (!checkNotNull(predicate).test(entry)) {
                iterator.remove();
            }
        }
        return this;
    }

    @Override
    public boolean containsKey(K key) {
        return this.actualValue.containsKey(checkNotNull(key));
    }

    @Override
    public boolean containsValue(V value) {
        return this.actualValue.containsValue(checkNotNull(value));
    }

    @Override
    public ImmutableSet<K> keySet() {
        return ImmutableSet.copyOf(this.actualValue.keySet());
    }

    @Override
    public ImmutableSet<Map.Entry<K, V>> entrySet() {
        return ImmutableSet.copyOf(this.actualValue.entrySet());
    }

    @Override
    public ImmutableCollection<V> values() {
        return ImmutableSet.copyOf(this.actualValue.values());
    }
}
