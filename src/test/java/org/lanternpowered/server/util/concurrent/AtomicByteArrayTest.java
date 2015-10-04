package org.lanternpowered.server.util.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class AtomicByteArrayTest {

    private static final int SIZE = 20;

    private static final byte ONE = 1;
    private static final byte TWO = 2;
    private static final byte THREE = 3;
    private static final byte FIVE = 5;
    private static final byte SIX = 5;

    @Test
    public void testArrayConstructor() {
        byte[] values = { TWO, ONE, THREE, FIVE, SIX };
        AtomicByteArray array = new AtomicByteArray(values);
        assertEquals(array.length(), values.length);
        for (int i = 0; i < values.length; i++) {
            assertEquals(array.get(i), values[i]);
            array.set(i, (byte) i);
            assertEquals(array.get(i), i);
        }
    }

    @Test
    public void testGetSet() {
        AtomicByteArray array = new AtomicByteArray(SIZE);
        for (int i = 0; i < array.length(); i++) {
            array.set(i, ONE);
            assertEquals(array.get(i), ONE);
            array.set(i, TWO);
            assertEquals(array.get(i), TWO);
            array.set(i, THREE);
            assertEquals(array.get(i), THREE);
        }
    }

    @Test
    public void testLazyGetSet() {
        AtomicByteArray array = new AtomicByteArray(SIZE);
        for (int i = 0; i < array.length(); i++) {
            array.lazySet(i, ONE);
            assertEquals(array.get(i), ONE);
            array.lazySet(i, TWO);
            assertEquals(array.get(i), TWO);
            array.lazySet(i, THREE);
            assertEquals(array.get(i), THREE);
        }
    }

    @Test
    public void testCompareAndSet() {
        AtomicByteArray array = new AtomicByteArray(SIZE);
        for (int i = 0; i < SIZE; i++) {
            array.set(i, ONE);
            assertTrue(array.compareAndSet(i, ONE, TWO));
            assertTrue(array.compareAndSet(i, TWO, THREE));
            assertEquals(array.get(i), THREE);
            assertFalse(array.compareAndSet(i, FIVE, SIX));
            assertEquals(array.get(i), THREE);
            assertTrue(array.compareAndSet(i, THREE, SIX));
            assertEquals(array.get(i), SIX);
        }
    }

    @Test
    public void testGetAndSet() {
        AtomicByteArray array = new AtomicByteArray(SIZE);
        for (int i = 0; i < SIZE; i++) {
            array.set(i, ONE);
            assertEquals(array.getAndSet(i, TWO), ONE);
            assertEquals(array.getAndSet(i, SIX), TWO);
            assertEquals(array.getAndSet(i, ONE), SIX);
        }
    }
}
