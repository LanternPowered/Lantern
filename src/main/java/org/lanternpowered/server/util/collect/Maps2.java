/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.util.collect;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.lanternpowered.server.util.collect.expirable.ExpirableValue;
import org.lanternpowered.server.util.collect.expirable.ExpirableValueMap;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public final class Maps2 {

    public static <K, V, B extends ExpirableValue<V>> ExpirableValueMap<K, V, B> createExpirableValueMap(BiFunction<K, V, B> backingEntrySupplier) {
        return new ExpirableValueMapImpl<>(Maps.newHashMap(), backingEntrySupplier);
    }

    public static <K, V, B extends ExpirableValue<V>> ExpirableValueMap<K, V, B> createConcurrentExpirableValueMap(
            BiFunction<K, V, B> backingEntrySupplier) {
        return new ExpirableValueMapImpl<>(Maps.newConcurrentMap(), backingEntrySupplier);
    }

    private static class ExpirableValueMapImpl<K, V, E extends ExpirableValue<V>> extends AbstractMap<K, V> implements ExpirableValueMap<K, V, E> {

        private final BiFunction<K, V, E> backEntrySupplier;
        private final Set<Entry<K, E>> backingEntrySet;
        private final Map<K, E> backingMap;

        private final Set<Entry<K, V>> entrySet = new Set<Entry<K, V>>() {

            @Override
            public int size() {
                return (int) this.stream().count();
            }

            @Override
            public boolean isEmpty() {
                return backingEntrySet.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry && !backingEntrySet.isEmpty()) {
                    for (Entry<K, V> entry : this) {
                        if (entry.equals(o)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                final Iterator<Entry<K, E>> backIterator = backingEntrySet.iterator();
                return new Iterator<Entry<K,V>>() {
                    private Entry<K, E> next;

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
            public Object[] toArray() {
                return this.toArray(new Object[this.size()]);
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return Lists.newArrayList(this).toArray(a);
            }

            @Override
            public boolean add(Entry<K, V> entry) {
                boolean found = this.remove(entry);
                backingEntrySet.add(new AbstractMap.SimpleEntry<>(entry.getKey(), backEntrySupplier.apply(entry.getKey(), entry.getValue())));
                return found;
            }

            @Override
            public boolean remove(Object o) {
                if (o instanceof Map.Entry && !backingEntrySet.isEmpty()) {
                    final Iterator<Entry<K, V>> it = this.iterator();
                    while (it.hasNext()) {
                        if (it.next().equals(o)) {
                            it.remove();
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                final List<?> checkList = Lists.newArrayList(c);
                this.forEach(checkList::remove);
                return checkList.isEmpty();
            }

            @Override
            public boolean addAll(Collection<? extends Entry<K, V>> c) {
                boolean result = true;
                for (Entry<K, V> entry : c) {
                    result &= this.add(entry);
                }
                return result;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                final List<?> checkList = Lists.newArrayList(c);
                final Iterator<Entry<K, V>> it = this.iterator();
                while (it.hasNext()) {
                    Entry<K, V> entry = it.next();
                    if (!c.contains(entry)) {
                        it.remove();
                    } else {
                        checkList.remove(entry);
                    }
                }
                return checkList.isEmpty();
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                int count = 0;
                final Iterator<Entry<K, V>> it = this.iterator();
                while (it.hasNext()) {
                    if (c.contains(it.next())) {
                        it.remove();
                        count++;
                    }
                }
                return count == c.size();
            }

            @Override
            public void clear() {
                backingEntrySet.clear();
            }

        };

        public ExpirableValueMapImpl(Map<K, E> backingMap, BiFunction<K, V, E> backEntrySupplier) {
            this.backEntrySupplier = backEntrySupplier;
            this.backingEntrySet = backingMap.entrySet();
            this.backingMap = backingMap;
        }

        @Override
        public V put(K key, V value) {
            final ExpirableValue<V> old = this.backingMap.put(key, this.backEntrySupplier.apply(key, value));
            return old != null && !old.isExpired() ? old.getValue() : null;
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
