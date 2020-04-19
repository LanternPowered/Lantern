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
