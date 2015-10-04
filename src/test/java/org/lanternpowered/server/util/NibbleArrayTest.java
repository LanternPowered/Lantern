package org.lanternpowered.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

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
