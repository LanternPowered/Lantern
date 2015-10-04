package org.lanternpowered.server.data.value.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import org.lanternpowered.server.data.value.mutable.LanternMapValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableMapValue;
import org.spongepowered.api.data.value.mutable.MapValue;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class ImmutableLanternMapValue<K, V> extends ImmutableLanternValue<Map<K, V>> implements ImmutableMapValue<K, V> {

    public ImmutableLanternMapValue(Key<? extends BaseValue<Map<K, V>>> key) {
        this(key, ImmutableMap.<K, V>of());
    }

    public ImmutableLanternMapValue(Key<? extends BaseValue<Map<K, V>>> key, Map<K, V> actualValue) {
        super(key, ImmutableMap.<K, V>of(), ImmutableMap.copyOf(actualValue));
    }

    @Override
    public ImmutableMapValue<K, V> with(Map<K, V> value) {
        return new ImmutableLanternMapValue<K, V>(getKey(), checkNotNull(value));
    }

    @Override
    public ImmutableMapValue<K, V> transform(Function<Map<K, V>, Map<K, V>> function) {
        return new ImmutableLanternMapValue<K, V>(getKey(), checkNotNull(checkNotNull(function).apply(this.actualValue)));
    }

    @Override
    public MapValue<K, V> asMutable() {
        final Map<K, V> map = Maps.newHashMap();
        map.putAll(this.actualValue);
        return new LanternMapValue<K, V>(getKey(), map);
    }

    @Override
    public int size() {
        return this.actualValue.size();
    }

    @Override
    public ImmutableMapValue<K, V> with(K key, V value) {
        return new ImmutableLanternMapValue<K, V>(getKey(), ImmutableMap.<K, V>builder().putAll(this.actualValue)
                .put(checkNotNull(key), checkNotNull(value)).build());
    }

    @Override
    public ImmutableMapValue<K, V> withAll(Map<K, V> map) {
        return new ImmutableLanternMapValue<K, V>(getKey(), ImmutableMap.<K, V>builder().putAll(this.actualValue)
                .putAll(map).build());
    }

    @Override
    public ImmutableMapValue<K, V> without(K key) {
        final ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        for (Map.Entry<K, V> entry : this.actualValue.entrySet()) {
            if (!entry.getKey().equals(key)) {
                builder.put(entry.getKey(), entry.getValue());
            }
        }
        return new ImmutableLanternMapValue<K, V>(getKey(), builder.build());
    }

    @Override
    public ImmutableMapValue<K, V> withoutAll(Iterable<K> keys) {
        final ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        for (Map.Entry<K, V> entry : this.actualValue.entrySet()) {
            if (!Iterables.contains(keys, entry.getKey())) {
                builder.put(entry.getKey(), entry.getValue());
            }
        }
        return new ImmutableLanternMapValue<K, V>(getKey(), builder.build());
    }

    @Override
    public ImmutableMapValue<K, V> withoutAll(Predicate<Map.Entry<K, V>> predicate) {
        final ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        for (Map.Entry<K, V> entry : this.actualValue.entrySet()) {
            if (checkNotNull(predicate).test(entry)) {
                builder.put(entry.getKey(), entry.getValue());
            }
        }
        return new ImmutableLanternMapValue<K, V>(getKey(), builder.build());
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
        return (ImmutableSet<K>) this.actualValue.keySet();
    }

    @Override
    public ImmutableSet<Map.Entry<K, V>> entrySet() {
        return (ImmutableSet<Map.Entry<K, V>>) this.actualValue.entrySet();
    }

    @Override
    public ImmutableCollection<V> values() {
        return (ImmutableCollection<V>) this.actualValue.values();
    }
}
