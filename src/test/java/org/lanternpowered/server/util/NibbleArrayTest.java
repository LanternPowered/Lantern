package org.lanternpowered.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class NibbleArrayTest {

    @Test
    public void test() {
        byte[] arrayPacked = new byte[] { (byte) (5 << 4 | 0), (byte) (9 << 4 | 15), (byte) (4 << 4 | 6) };
        byte[] array = new byte[] { 0, 5, 15, 9, 6, 4 };
        NibbleArray nibbleArray0 = new NibbleArray(array.length, arrayPacked, true);
        NibbleArray nibbleArray1 = new NibbleArray(array.length, array, false);
        assertEquals(nibbleArray0.length(), nibbleArray1.length());
        assertArrayEquals(nibbleArray0.getPackedArray(), nibbleArray1.getPackedArray());
        assertArrayEquals(nibbleArray0.getArray(), nibbleArray1.getArray());
        for (int i = 0; i < array.length; i++) {
            assertEquals(nibbleArray1.get(i), array[i]);
        }
        for (int i = 0; i < array.length; i++) {
            assertEquals(nibbleArray0.get(i), array[i]);
        }
    }
}
