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
package org.lanternpowered.server.util.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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
