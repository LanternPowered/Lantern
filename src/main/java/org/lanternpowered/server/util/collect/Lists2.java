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
package org.lanternpowered.server.util.collect;

import org.lanternpowered.server.util.collect.expirable.ExpirableValue;
import org.lanternpowered.server.util.collect.expirable.ExpirableValueList;
import org.lanternpowered.server.util.collect.expirable.SimpleExpirableValue;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

@NonnullByDefault
public final class Lists2 {

    public static <V, B extends ExpirableValue<V>> ExpirableValueList<V, B> createExpirableValueList(Function<V, B> backValueSupplier) {
        return new ExpirableValueListImpl<>(new ArrayList<>(), backValueSupplier);
    }

    public static <V, B extends ExpirableValue<V>> ExpirableValueList<V, B> createExpirableValueListWithPredicate(
            Predicate<V> expirationChecker) {
        // Casting weirdness...
        //noinspection unchecked
        return new ExpirableValueListImpl<>(new ArrayList<>(), value -> (B) new PredicateExpirableValue(value, expirationChecker));
    }

    public static <V, B extends ExpirableValue<V>> ExpirableValueList<V, B> createCopyOnWriteExpirableValueList(Function<V, B> backValueSupplier) {
        return new ExpirableValueListImpl<>(new CopyOnWriteArrayList<>(), backValueSupplier);
    }

    public static <V, B extends ExpirableValue<V>> ExpirableValueList<V, B> createCopyOnWriteExpirableValueListWithPredicate(
            Predicate<V> expirationChecker) {
        // Casting weirdness...
        //noinspection unchecked
        return new ExpirableValueListImpl<>(new CopyOnWriteArrayList<>(), value -> (B) new PredicateExpirableValue(value, expirationChecker));
    }

    private static class PredicateExpirableValue<V> extends SimpleExpirableValue<V> {

        private final Predicate<V> predicate;

        public PredicateExpirableValue(V value, Predicate<V> predicate) {
            super(value);
            this.predicate = predicate;
        }

        @Override
        public boolean isExpired() {
            return this.predicate.test(this.getValue());
        }
    }

    private static class ExpirableValueListImpl<V, B extends ExpirableValue<V>> extends AbstractList<V> implements ExpirableValueList<V, B> {

        private final List<B> backing;
        private final Function<V, B> backValueSupplier;

        public ExpirableValueListImpl(List<B> backing, Function<V, B> backValueSupplier) {
            this.backValueSupplier = backValueSupplier;
            this.backing = backing;
        }

        private void clean(int endIndex) {
            final Iterator<B> it = this.backing.iterator();
            while (it.hasNext() && endIndex >= 0) {
                final B value = it.next();
                if (value.isExpired()) {
                    it.remove();
                } else {
                    endIndex--;
                }
            }
        }

        @Nullable
        @Override
        public V set(int index, V element) {
            this.clean(index);
            final B old = this.backing.set(index, this.backValueSupplier.apply(element));
            return old == null ? null : old.getValue();
        }

        @Override
        public void add(int index, V element) {
            this.clean(index);
            this.backing.add(index, this.backValueSupplier.apply(element));
        }

        @Nullable
        @Override
        public V remove(int index) {
            this.clean(index);
            final B old = this.backing.remove(index);
            return old == null ? null : old.getValue();
        }

        @Override
        public V get(int index) {
            final Iterator<B> it = this.backing.iterator();
            while (it.hasNext()) {
                final B value = it.next();
                if (value.isExpired()) {
                    it.remove();
                } else if (--index < 0) {
                    return value.getValue();
                }
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public int size() {
            final Iterator<B> it = this.backing.iterator();
            int size = 0;
            while (it.hasNext()) {
                final B value = it.next();
                if (value.isExpired()) {
                    it.remove();
                } else {
                    size++;
                }
            }
            return size;
        }

        @Override
        public List<B> getBacking() {
            return this.backing;
        }
    }

    private Lists2() {
    }
}
