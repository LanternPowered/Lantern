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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.flowpowered.math.GenericMath;
import org.junit.Test;
import org.lanternpowered.server.util.collect.array.concurrent.AtomicFloatArray;

public class AtomicFloatArrayTest {

    private static final int SIZE = 15;

    private static final float EPSILON = GenericMath.FLT_EPSILON;
    private static final float A = 1259.5624f;
    private static final float B = 6924.681169f;
    private static final float C = 14893.0159f;
    private static final float D = 12390.0f;
    private static final float E = 26870.14795f;

    @Test
    public void testArrayConstructor() {
        float[] values = { B, A, C, D, E };
        AtomicFloatArray array = new AtomicFloatArray(values);
        assertEquals(array.length(), values.length);
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], array.get(i), EPSILON);
            array.set(i, (byte) i);
            assertEquals(i, array.get(i), EPSILON);
        }
    }

    @Test
    public void testGetSet() {
        AtomicFloatArray array = new AtomicFloatArray(SIZE);
        for (int i = 0; i < array.length(); i++) {
            array.set(i, A);
            assertEquals(A, array.get(i), EPSILON);
            array.set(i, B);
            assertEquals(B, array.get(i), EPSILON);
            array.set(i, C);
            assertEquals(C, array.get(i), EPSILON);
        }
    }

    @Test
    public void testLazyGetSet() {
        AtomicFloatArray array = new AtomicFloatArray(SIZE);
        for (int i = 0; i < array.length(); i++) {
            array.lazySet(i, A);
            assertEquals(A, array.get(i), EPSILON);
            array.lazySet(i, B);
            assertEquals(B, array.get(i), EPSILON);
            array.lazySet(i, C);
            assertEquals(C, array.get(i), EPSILON);
        }
    }

    @Test
    public void testCompareAndSet() {
        AtomicFloatArray array = new AtomicFloatArray(SIZE);
        for (int i = 0; i < SIZE; i++) {
            array.set(i, A);
            assertTrue(array.compareAndSet(i, A, B));
            assertTrue(array.compareAndSet(i, B, C));
            assertEquals(C, array.get(i), EPSILON);
            assertFalse(array.compareAndSet(i, D, E));
            assertEquals(C, array.get(i), EPSILON);
            assertTrue(array.compareAndSet(i, C, E));
            assertEquals(E, array.get(i), EPSILON);
        }
    }

    @Test
    public void testGetAndSet() {
        AtomicFloatArray array = new AtomicFloatArray(SIZE);
        for (int i = 0; i < SIZE; i++) {
            array.set(i, A);
            assertEquals(A, array.getAndSet(i, B), EPSILON);
            assertEquals(B, array.getAndSet(i, E), EPSILON);
            assertEquals(E, array.getAndSet(i, A), EPSILON);
        }
    }
}
