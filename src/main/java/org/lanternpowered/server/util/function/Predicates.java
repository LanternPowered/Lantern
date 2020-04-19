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
package org.lanternpowered.server.util.function;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public final class Predicates {

    private static final Predicate FALSE = o -> false;
    private static final Predicate TRUE = o -> true;

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

    public static <T> Predicate<T> and(Predicate<? extends Predicate<? super T>>... predicates) {
        checkNotNull(predicates, "predicates");
        if (predicates.length == 0) {
            return FALSE;
        }
        Predicate predicate = checkNotNull(predicates[0]);
        for (int i = 1; i < predicates.length; i++) {
            predicate = predicate.and(checkNotNull(predicates[i]));
        }
        return predicate;
    }

    public static <T> Predicate<T> or(Iterable<? extends Predicate<? super T>> predicates) {
        checkNotNull(predicates, "predicates");
        final Iterator<? extends Predicate<? super T>> it = predicates.iterator();
        if (it.hasNext()) {
            Predicate predicate = checkNotNull(it.next());
            while (it.hasNext()) {
                predicate = predicate.or(checkNotNull(it.next()));
            }
            return predicate;
        }
        return TRUE;
    }

    @SafeVarargs
    public static <T> Predicate<T> or(Predicate<? extends Predicate<? super T>>... predicates) {
        checkNotNull(predicates, "predicates");
        if (predicates.length == 0) {
            return TRUE;
        }
        Predicate predicate = checkNotNull(predicates[0]);
        for (int i = 1; i < predicates.length; i++) {
            predicate = predicate.or(checkNotNull(predicates[i]));
        }
        return predicate;
    }

    public static <T> Predicate<T> not(Predicate<T> predicate) {
        checkNotNull(predicate, "predicate");
        return o -> !predicate.test(o);
    }

    private Predicates() {
    }
}
