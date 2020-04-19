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

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class Collections3 {

    /**
     * Picks a random element from the given {@link Collection},
     * or {@code null} if the collection is empty.
     *
     * @param collection The collection
     * @param <T> The element type
     * @return The element
     */
    @Nullable
    public static <T> T pickRandomElement(Collection<T> collection) {
        final int size = collection.size();
        if (size == 0) {
            return null;
        }
        final int index = ThreadLocalRandom.current().nextInt(size);
        int i = 0;
        for (T element : collection) {
            if (i++ == index) {
                return element;
            }
        }
        throw new IllegalStateException("Should never be reached");
    }
}
