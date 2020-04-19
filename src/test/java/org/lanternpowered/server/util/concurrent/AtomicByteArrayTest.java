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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.lanternpowered.server.util.collect.array.concurrent.AtomicByteArray;

public class AtomicByteArrayTest {

    private static final int SIZE = 15;

    private static final byte A = 5;
    private static final byte B = 17;
    private static final byte C = 85;
    private static final byte D = 20;
    private static final byte E = 112;

    @Test
    public void testArrayConstructor() {
        byte[] values = { B, A, C, D, E };
        AtomicByteArray array = new AtomicByteArray(values);
        assertEquals(array.length(), values.length);
        assertArrayEquals(values, array.getArray());
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], array.get(i));
            array.set(i, (byte) i);
            assertEquals(i, array.get(i));
        }
    }

    @Test
    public void testGetSet() {
        AtomicByteArray array = new AtomicByteArray(SIZE);
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
        AtomicByteArray array = new AtomicByteArray(SIZE);
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
        AtomicByteArray array = new AtomicByteArray(SIZE);
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
        AtomicByteArray array = new AtomicByteArray(SIZE);
        for (int i = 0; i < SIZE; i++) {
            array.set(i, A);
            assertEquals(A, array.getAndSet(i, B));
            assertEquals(B, array.getAndSet(i, E));
            assertEquals(E, array.getAndSet(i, A));
        }
    }
}
