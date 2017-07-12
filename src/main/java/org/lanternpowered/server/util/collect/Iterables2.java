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

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class Iterables2 {

    /**
     * Creates a {@link Iterable} that iterates in a reverse
     * order through the {@link Iterable}.
     *
     * @param iterable The iterable
     * @param <T> The type
     * @return The iterable
     */
    public static <T> Iterable<T> reverse(Iterable<T> iterable) {
        return iterable instanceof List ? reverse((List<T>) iterable) : reverse(Lists.newArrayList(iterable));
    }

    /**
     * Creates a {@link Iterable} that iterates in a reverse
     * order through the {@link Iterable}.
     *
     * @param list The list
     * @param <T> The type
     * @return The iterable
     */
    public static <T> Iterable<T> reverse(List<T> list) {
        checkNotNull(list, "list");
        return () -> new Iterator<T>() {
            private final ListIterator<T> it = list.listIterator(list.size());

            @Override
            public boolean hasNext() {
                return this.it.hasPrevious();
            }

            @Override
            public T next() {
                return this.it.previous();
            }

            @Override
            public void remove() {
                this.it.remove();
            }
        };
    }

    private Iterables2() {
    }
}
