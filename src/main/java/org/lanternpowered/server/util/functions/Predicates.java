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
package org.lanternpowered.server.util.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.function.Predicate;

public final class Predicates {

    private static final Predicate FALSE = o -> false;

    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> and(Iterable<? extends Predicate<? super T>> predicates) {
        checkNotNull(predicates, "predicates");
        final Iterator<? extends Predicate<? super T>> it = predicates.iterator();
        if (it.hasNext()) {
            Predicate predicate = checkNotNull(it.next());
            while (it.hasNext()) {
                predicate = predicate.and(checkNotNull(it.next()));
            }
            return predicate;
        }
        return FALSE;
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T> Predicate<T> and(Predicate<? extends Predicate<? super T>>... predicates) {
        checkNotNull(predicates, "predicates");
        if (predicates.length == 0) {
            //noinspection unchecked
            return FALSE;
        }
        Predicate predicate = checkNotNull(predicates[0]);
        for (int i = 1; i < predicates.length; i++) {
            predicate = predicate.and(checkNotNull(predicates[i]));
        }
        return predicate;
    }

    private Predicates() {
    }
}
