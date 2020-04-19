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
package org.lanternpowered.server.util.collect.array.concurrent;

import java.util.concurrent.atomic.AtomicIntegerArray;

public final class AtomicIntegerArrayHelper {

    public static int[] toArray(AtomicIntegerArray atomicIntegerArray) {
        final int[] array = new int[atomicIntegerArray.length()];
        for (int i = 0; i < array.length; i++) {
            array[i] = atomicIntegerArray.get(i);
        }
        return array;
    }

    private AtomicIntegerArrayHelper() {
    }
}
