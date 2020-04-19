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

import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

@NonnullByDefault
public final class Sets2 {

    private Sets2() {
    }

    /**
     * Creates a new <strong>weak</strong> {@link java.util.HashSet}.
     * 
     * @return the weak hash set
     */
    public static <T> Set<T> newWeakHashSet() {
        return Collections.newSetFromMap(new WeakHashMap<>());
    }

    /**
     * Creates a new <strong>weak</strong> {@link java.util.HashSet} with the specified objects as
     * initial contents.
     *
     * @param objects the objects
     * @return the weak hash set
     */
    public static <T> Set<T> newWeakHashSet(Iterable<? extends T> objects) {
        final Map<T, Boolean> map = new HashMap<>();
        for (T object : objects) {
            map.put(object, true);
        }
        return Collections.newSetFromMap(new WeakHashMap<>(map));
    }

}
