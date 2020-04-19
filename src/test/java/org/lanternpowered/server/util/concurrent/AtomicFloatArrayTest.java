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
import org.lanternpowered.server.util.collect.array.concurrent.AtomicFloatArray;
import org.spongepowered.math.GenericMath;

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
