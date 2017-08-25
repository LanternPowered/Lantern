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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import org.lanternpowered.server.util.collect.expirable.ExpirableValue;
import org.lanternpowered.server.util.collect.expirable.ExpirableValueList;
import org.lanternpowered.server.util.collect.expirable.SimpleExpirableValue;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
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

    /**
     * Creates a non null list for the specified list, all the operations
     * should be done through the new list.
     * 
     * @param list the list
     * @return the non null list
     */
    public static <T> List<T> nonNullOf(List<T> list) {
        return checkedOf(list, e -> checkNotNull(e, "element"));
    }

    public static <T> List<T> nonNullArrayList() {
        return nonNullOf(new ArrayList<T>());
    }

    /**
     * Creates a checked list for the specified backing list, all the operations
     * should be done through the new list.
     * 
     * @param list the backing list
     * @param checker the checker that should be used to validate the added values
     * @return the checked list
     */
    public static <T> List<T> checkedOf(List<T> list, Consumer<T> checker) {
        return new CheckedList<>(checkNotNull(list, "list"), checkNotNull(checker, "checker"));
    }

    private static class CheckedListIterator<T> implements ListIterator<T> {

        private final ListIterator<T> backing;
        private final Consumer<T> checker;

        public CheckedListIterator(ListIterator<T> backing, Consumer<T> checker) {
            this.checker = checker;
            this.backing = backing;
        }

        @Override
        public boolean hasNext() {
            return this.backing.hasNext();
        }

        @Override
        public T next() {
            return this.backing.next();
        }

        @Override
        public boolean hasPrevious() {
            return this.backing.hasPrevious();
        }

        @Override
        public T previous() {
            return this.backing.previous();
        }

        @Override
        public int nextIndex() {
            return this.backing.nextIndex();
        }

        @Override
        public int previousIndex() {
            return this.backing.previousIndex();
        }

        @Override
        public void remove() {
            this.backing.remove();
        }

        @Override
        public void set(T e) {
            this.checker.accept(e);
            this.backing.set(e);
        }

        @Override
        public void add(T e) {
            this.checker.accept(e);
            this.backing.add(e);
        }
    }

    private static class CheckedList<T> implements List<T> {

        private final List<T> backing;
        private final Consumer<T> checker;

        public CheckedList(List<T> backing, Consumer<T> checker) {
            this.checker = checker;
            this.backing = backing;
        }

        @Override
        public int size() {
            return this.backing.size();
        }

        @Override
        public boolean isEmpty() {
            return this.backing.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return this.backing.contains(o);
        }

        @Override
        public Iterator<T> iterator() {
            return new CheckedListIterator<>(this.backing.listIterator(), this.checker);
        }

        @Override
        public Object[] toArray() {
            return this.backing.toArray();
        }

        @Override
        public <V> V[] toArray(V[] a) {
            return this.backing.toArray(a);
        }

        @Override
        public boolean add(T e) {
            this.checker.accept(e);
            return this.backing.add(e);
        }

        @Override
        public boolean remove(Object o) {
            return this.backing.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.backing.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            c.forEach(this.checker::accept);
            return this.backing.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            c.forEach(this.checker::accept);
            return this.backing.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return this.backing.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return this.backing.retainAll(c);
        }

        @Override
        public void clear() {
            this.backing.clear();
        }

        @Override
        public T get(int index) {
            return this.backing.get(index);
        }

        @Override
        public T set(int index, T e) {
            this.checker.accept(e);
            return this.backing.set(index, e);
        }

        @Override
        public void add(int index, T e) {
            this.checker.accept(e);
            this.backing.add(index, e);
        }

        @Override
        public T remove(int index) {
            return this.backing.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return this.backing.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return this.backing.lastIndexOf(o);
        }

        @Override
        public ListIterator<T> listIterator() {
            return new CheckedListIterator<>(this.backing.listIterator(), this.checker);
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            return new CheckedListIterator<>(this.backing.listIterator(index), this.checker);
        }

        @Override
        public List<T> subList(int fromIndex, int toIndex) {
            return new CheckedList<>(this.backing.subList(fromIndex, toIndex), this.checker);
        }
    }

    private Lists2() {
    }
}
