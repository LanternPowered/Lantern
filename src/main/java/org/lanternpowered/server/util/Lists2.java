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
package org.lanternpowered.server.util;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

@NonnullByDefault
public final class Lists2 {

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
