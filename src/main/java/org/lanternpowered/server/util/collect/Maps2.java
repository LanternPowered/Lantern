/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.util.collect;

import org.lanternpowered.server.util.collect.expirable.ExpirableValue;
import org.lanternpowered.server.util.collect.expirable.ExpirableValueMap;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class Maps2 {

    public static <K, V, B extends ExpirableValue<V>> ExpirableValueMap<K, V, B> createExpirableValueMap(
            BiFunction<K, V, B> backingEntrySupplier) {
        return new ExpirableValueMapImpl<>(new HashMap<>(), backingEntrySupplier);
    }

    public static <K, V, B extends ExpirableValue<V>> ExpirableValueMap<K, V, B> createConcurrentExpirableValueMap(
            BiFunction<K, V, B> backingEntrySupplier) {
        return new ExpirableValueMapImpl<>(new ConcurrentHashMap<>(), backingEntrySupplier);
    }

    private static class ExpirableValueMapImpl<K, V, E extends ExpirableValue<V>> extends AbstractMap<K, V> implements ExpirableValueMap<K, V, E> {

        private final BiFunction<K, V, E> backEntrySupplier;
        private final Set<Entry<K, E>> backingEntrySet;
        private final Map<K, E> backingMap;

        private final Set<Entry<K, V>> entrySet = new AbstractSet<Entry<K, V>>() {

            @Override
            public Iterator<Entry<K, V>> iterator() {
                final Iterator<Entry<K, E>> backIterator = backingEntrySet.iterator();
                return new Iterator<Entry<K,V>>() {
                    @Nullable private Entry<K, E> next;

                    @Override
                    public void remove() {
                        backIterator.remove();
                    }

                    @Override
                    public boolean hasNext() {
                        if (this.next != null && !this.next.getValue().isExpired()) {
                            return true;
                        }
                        this.next = this.next0();
                        return this.next != null;
                    }

                    @Override
                    public Entry<K, V> next() {
                        if (!this.hasNext()) {
                            // Just throw a exception
                            backIterator.next();
                        }
                        Entry<K, V> entry = new AbstractMap.SimpleEntry<>(this.next.getKey(), this.next.getValue().getValue());
                        this.next = null;
                        return entry;
                    }

                    public Entry<K, E> next0() {
                        while (backIterator.hasNext()) {
                            Entry<K, E> next = backIterator.next();
                            if (next.getValue().isExpired()) {
                                backIterator.remove();
                            } else {
                                return next;
                            }
                        }
                        return null;
                    }
                };
            }

            @Override
            public int size() {
                return (int) this.stream().count();
            }

            @Override
            public boolean add(Entry<K, V> entry) {
                return put(entry.getKey(), entry.getValue()) != null;
            }

        };

        public ExpirableValueMapImpl(Map<K, E> backingMap, BiFunction<K, V, E> backEntrySupplier) {
            this.backEntrySupplier = backEntrySupplier;
            this.backingEntrySet = backingMap.entrySet();
            this.backingMap = backingMap;
        }

        @Nullable
        @Override
        public V put(K key, @Nullable V value) {
            final ExpirableValue<V> old = this.backingMap.put(key, this.backEntrySupplier.apply(key, value));
            return old != null && !old.isExpired() ? old.getValue() : null;
        }

        @Override
        public boolean containsKey(Object key) {
            final Iterator<Entry<K, E>> it = this.backingEntrySet.iterator();
            while (it.hasNext()) {
                final Entry<K, E> entry = it.next();
                if (entry.getValue().isExpired()) {
                    it.remove();
                } else if (Objects.equals(entry.getKey(), key)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            final Iterator<Entry<K, E>> it = this.backingEntrySet.iterator();
            while (it.hasNext()) {
                final Entry<K, E> entry = it.next();
                if (entry.getValue().isExpired()) {
                    it.remove();
                } else if (Objects.equals(entry.getValue().getValue(), value)) {
                    return true;
                }
            }
            return false;
        }

        @Nullable
        @Override
        public V get(Object key) {
            final Iterator<Entry<K, E>> it = this.backingEntrySet.iterator();
            while (it.hasNext()) {
                final Entry<K, E> entry = it.next();
                if (entry.getValue().isExpired()) {
                    it.remove();
                } else if (Objects.equals(entry.getKey(), key)) {
                    return entry.getValue().getValue();
                }
            }
            return null;
        }

        @Nullable
        @Override
        public V remove(Object key) {
            final ExpirableValue<V> old = this.backingMap.remove(key);
            return old == null || old.isExpired() ? null : old.getValue();
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return this.entrySet;
        }

        @Override
        public Map<K, E> getBacking() {
            return this.backingMap;
        }
    }
}
