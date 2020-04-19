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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.lanternpowered.server.util.collect.array.concurrent.AtomicShortArray;

public class AtomicShortArrayTest {

    private static final int SIZE = 15;

    private static final short A = 159;
    private static final short B = 5796;
    private static final short C = 15790;
    private static final short D = 12390;
    private static final short E = 26870;

    @Test
    public void testArrayConstructor() {
        short[] values = { B, A, C, D, E };
        AtomicShortArray array = new AtomicShortArray(values);
        assertEquals(array.length(), values.length);
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], array.get(i));
            array.set(i, (byte) i);
            assertEquals(i, array.get(i));
        }
    }

    @Test
    public void testGetSet() {
        AtomicShortArray array = new AtomicShortArray(SIZE);
        for (int i = 0; i < array.length(); i++) {
            array.set(i, A);
            assertEquals(A, array.get(i));
            array.set(i, B);
            assertEquals(B, array.get(i));
            array.set(i, C);
            assertEquals(C, array.get(i));
        }
    }

    @Test
    public void testLazyGetSet() {
        AtomicShortArray array = new AtomicShortArray(SIZE);
        for (int i = 0; i < array.length(); i++) {
            array.lazySet(i, A);
            assertEquals(A, array.get(i));
            array.lazySet(i, B);
            assertEquals(B, array.get(i));
            array.lazySet(i, C);
            assertEquals(C, array.get(i));
        }
    }

    @Test
    public void testCompareAndSet() {
        AtomicShortArray array = new AtomicShortArray(SIZE);
        for (int i = 0; i < SIZE; i++) {
            array.set(i, A);
            assertTrue(array.compareAndSet(i, A, B));
            assertTrue(array.compareAndSet(i, B, C));
            assertEquals(C, array.get(i));
            assertFalse(array.compareAndSet(i, D, E));
            assertEquals(C, array.get(i));
            assertTrue(array.compareAndSet(i, C, E));
            assertEquals(E, array.get(i));
        }
    }

    @Test
    public void testGetAndSet() {
        AtomicShortArray array = new AtomicShortArray(SIZE);
        for (int i = 0; i < SIZE; i++) {
            array.set(i, A);
            assertEquals(A, array.getAndSet(i, B));
            assertEquals(B, array.getAndSet(i, E));
            assertEquals(E, array.getAndSet(i, A));
        }
    }
}
