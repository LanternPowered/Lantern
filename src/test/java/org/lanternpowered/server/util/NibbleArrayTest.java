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
package org.lanternpowered.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;
import org.lanternpowered.server.util.collect.array.NibbleArray;

public class NibbleArrayTest {

    private static final int SIZE = 15;

    private static final byte A = 5;
    private static final byte B = 2;
    private static final byte C = 7;
    private static final byte D = 11;
    private static final byte E = 14;

    @Test
    public void testArrayConstructor() {
        byte[] values = { B, A, C, D, E };
        NibbleArray array = new NibbleArray(values.length, values, false);
        assertEquals(array.length(), values.length);
        assertArrayEquals(values, array.getArray());
        for (int i = 0; i < values.length; i++) {
            assertEquals(array.get(i), values[i]);
            array.set(i, (byte) i);
            assertEquals(array.get(i), i);
        }
    }

    @Test
    public void testPackedArrayConstructor() {
        byte[] valuesPacked = { B | A << 4, (byte) (C | D << 4), E };
        byte[] values = { B, A, C, D, E };
        NibbleArray array = new NibbleArray(values.length, valuesPacked, true);
        assertEquals(array.length(), values.length);
        assertArrayEquals(values, array.getArray());
        assertArrayEquals(valuesPacked, array.getPackedArray());
        for (int i = 0; i < values.length; i++) {
            assertEquals(array.get(i), values[i]);
            array.set(i, (byte) i);
            assertEquals(array.get(i), i);
        }
    }

    @Test
    public void testGetSet() {
        NibbleArray array = new NibbleArray(SIZE);
        for (int i = 0; i < array.length(); i++) {
            array.set(i, A);
            assertEquals(array.get(i), A);
            array.set(i, B);
            assertEquals(array.get(i), B);
            array.set(i, C);
            assertEquals(array.get(i), C);
        }
    }
}
