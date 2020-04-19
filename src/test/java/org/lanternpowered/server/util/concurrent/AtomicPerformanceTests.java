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
package org.lanternpowered.server.util.concurrent;

import org.junit.Test;
import org.lanternpowered.server.util.collect.array.concurrent.AtomicByteArray;
import org.lanternpowered.server.util.collect.array.concurrent.AtomicNibbleArray;
import org.lanternpowered.server.util.collect.array.concurrent.AtomicShortArray;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicPerformanceTests {

    private final static String MESSAGE = "%s for %s tests took: %s ms";
    private final static int TESTS = 100000;

    @Test
    public void testSetPerformance() {
        for (int i = 0; i < 3; i++) {
            testSetPerformance0();
        }
    }

    // @Test
    public void testSetPerformance0() {
        AtomicIntegerArray array0 = new AtomicIntegerArray(TESTS);
        long time = System.currentTimeMillis();
        for (int i = 0; i < TESTS; i++) {
            array0.set(i, i);
        }
        System.out.println(String.format(MESSAGE, "AtomicIntegerArray",
                TESTS, System.currentTimeMillis() - time));
        int[] parray0 = new int[TESTS];
        time = System.currentTimeMillis();
        for (int i = 0; i < TESTS; i++) {
            parray0[i] = i;
        }
        System.out.println(String.format(MESSAGE, "int[]",
                TESTS, System.currentTimeMillis() - time));
        AtomicShortArray array1 = new AtomicShortArray(TESTS);
        time = System.currentTimeMillis();
        for (int i = 0; i < TESTS; i++) {
            array1.set(i, (short) i);
        }
        System.out.println(String.format(MESSAGE, "AtomicShortArray",
                TESTS, System.currentTimeMillis() - time));
        short[] parray1 = new short[TESTS];
        time = System.currentTimeMillis();
        for (int i = 0; i < TESTS; i++) {
            parray1[i] = (short) i;
        }
        System.out.println(String.format(MESSAGE, "short[]",
                TESTS, System.currentTimeMillis() - time));
        AtomicByteArray array2 = new AtomicByteArray(TESTS);
        time = System.currentTimeMillis();
        for (int i = 0; i < TESTS; i++) {
            array2.set(i, (byte) (i % 255));
        }
        System.out.println(String.format(MESSAGE, "AtomicByteArray",
                TESTS, System.currentTimeMillis() - time));
        byte[] parray3 = new byte[TESTS];
        time = System.currentTimeMillis();
        for (int i = 0; i < TESTS; i++) {
            parray3[i] = (byte) (i % 255);
        }
        System.out.println(String.format(MESSAGE, "byte[]",
                TESTS, System.currentTimeMillis() - time));
        AtomicNibbleArray array3 = new AtomicNibbleArray(TESTS);
        time = System.currentTimeMillis();
        for (int i = 0; i < TESTS; i++) {
            array3.set(i, (byte) (i % 15));
        }
        System.out.println(String.format(MESSAGE, "AtomicNibbleArray",
                TESTS, System.currentTimeMillis() - time));
    }
}
