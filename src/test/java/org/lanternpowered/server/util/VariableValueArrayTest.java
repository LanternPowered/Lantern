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
package org.lanternpowered.server.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VariableValueArrayTest {

    @Test
    public void test() {
        int bits = 5;
        int maxValue = (1 << bits) - 1;
        int capacity = 4096;
        VariableValueArray array = new VariableValueArray(bits, capacity);
        for (int i = 0; i < capacity; i++) {
            int value = i % maxValue;
            array.set(i, value);
            assertEquals(value, array.get(i));
        }
        for (int i = 0; i < capacity; i++) {
            int value = maxValue - (i % maxValue);
            array.set(i, value);
            assertEquals(array.get(i), value);
        }
    }
}
