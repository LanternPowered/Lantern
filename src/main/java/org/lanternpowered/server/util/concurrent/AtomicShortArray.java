package org.lanternpowered.server.util.concurrent;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicShortArray implements Serializable {

    private static final long serialVersionUID = 3434275139515033068L;

    private static boolean even(int index) {
        return (index & 0x1) == 0;
    }

    private static int key(short key1, short key2) {
        return key1 << 16 | key2 & 0xffff;
    }

    private static short key1(int key) {
        return (short) ((key >> 16) & 0xffff);
    }

    private static short key2(int key) {
        return (short) (key & 0xffff);
    }

    private final int length;
    private final int backingArraySize;
    private final AtomicIntegerArray backingArray;

    /**
     * Creates a new {@link AtomicShortArray} of the given length, with all
     * elements initially zero.
     *
     * @param length the length of the array
     */
    public AtomicShortArray(int length) {
        this.length = length;
        this.backingArraySize = (length & 1) + (length >> 1);
        this.backingArray = new AtomicIntegerArray(this.backingArraySize);
    }

    /**
     * Creates a new {@link AtomicShortArray} of the given length, with all
     * elements initially zero.
     *
     * @param length the length of the array
     */
    public AtomicShortArray(short[] array) {
        this.length = array.length;
        this.backingArraySize = (this.length & 1) + (this.length >> 1);
        int[] array0 = new int[this.backingArraySize];
        for (int i = 0; i < this.backingArraySize; i++) {
            int j = i << 1;
            int value = array[j];
            if (j + 1 < array.length) {
                value |= array[j + 1] << 4;
            }
            array0[i] = value;
        }
        this.backingArray = new AtomicIntegerArray(array0);
    }

    private int getPacked(int index) {
        return this.backingArray.get(index >> 1);
    }

    /**
     * Gets the length of the array.
     *
     * @return the length
     */
    public int length() {
        return this.length;
    }

    /**
     * Gets an element from the array at a given index.
     *
     * @param index the index
     * @return the element
     */
    public short get(int index) {
        int packed = this.getPacked(index);
        return even(index) ? key1(packed) : key2(packed);
    }

    /**
     * Sets an element in the array at a given index and returns the old value.
     *
     * @param index the index
     * @param value the new value
     * @return the old value
     */
    public int getAndSet(int index, short value) {
        boolean success = false;
        short odd = 0;
        short even = 0;
        short oldValue = 0;
        int backingIndex = index >> 1;
        boolean evenIndex = even(index);
        while (!success) {
            int oldPacked = this.backingArray.get(backingIndex);
            if (evenIndex) {
                oldValue = key1(oldPacked);
                even = value;
                odd = key2(oldPacked);
            } else {
                oldValue = key2(oldPacked);
                even = key1(oldPacked);
                odd = value;
            }
            int newPacked = key(even, odd);
            success = this.backingArray.compareAndSet(backingIndex, oldPacked, newPacked);
        }
        return oldValue;
    }

    /**
     * Sets two elements in the array at once. The index must be even.
     *
     * @param index the index
     * @param even the new value for the element at (index)
     * @param odd the new value for the element at (index + 1)
     */
    public void set(int index, short even, short odd) {
        if ((index & 0x1) != 0) {
            throw new IllegalArgumentException("When setting 2 elements at once, the index must be even!");
        }
        this.backingArray.set(index >> 1, key(even, odd));
    }

    /**
     * Sets the element at the given index, but only if the previous value was the expected value.
     *
     * @param index the index
     * @param expected the expected value
     * @param newValue the new value
     * @return true on success
     */
    public boolean compareAndSet(int index, short expected, short newValue) {
        boolean success = false;
        short odd = 0;
        short even = 0;
        short oldValue = 0;
        int backingIndex = index >> 1;
        boolean evenIndex = even(index);
        while (!success) {
            int oldPacked = this.backingArray.get(backingIndex);
            if (evenIndex) {
                oldValue = key1(oldPacked);
                even = newValue;
                odd = key2(oldPacked);
            } else {
                oldValue = key2(oldPacked);
                even = key1(oldPacked);
                odd = newValue;
            }
            if (oldValue != expected) {
                return false;
            }
            int newPacked = key(even, odd);
            success = this.backingArray.compareAndSet(backingIndex, oldPacked, newPacked);
        }
        return true;
    }

    private short addAndGet(int index, short delta, boolean old) {
        boolean success = false;
        short newValue = 0;
        short oldValue = 0;
        while (!success) {
            oldValue = this.get(index);
            newValue = (short) (oldValue + delta);
            success = this.compareAndSet(index, oldValue, newValue);
        }
        return old ? oldValue : newValue;
    }

    /**
     * Gets an array containing all the values in the array. The returned values are
     * not guaranteed to be from the same time instant.
     *
     * If an array is provided and it is the correct length, then
     * that array will be used as the destination array.
     *
     * @param array the provided array
     * @return an array containing the values in the array
     */
    public short[] getArray(short[] array) {
        if (array == null || array.length != this.length) {
            array = new short[this.length];
        }
        for (int i = 0; i < this.length; i += 2) {
            int packed = this.getPacked(i);
            array[i] = key1(packed);
            if (i + 1 < this.length) {
                array[i + 1] = key2(packed);
            }
        }
        return array;
    }

    /**
     * Sets an element to the given value.
     *
     * @param index the index
     * @param value the new value
     */
    public void set(int index, short value) {
        this.getAndSet(index, value);
    }

    /**
     * Sets an element to the given value, but the update may not happen immediately.
     *
     * @param index the index
     * @param value the new value
     */
    public void lazySet(int index, short value) {
        this.set(index, value);
    }

    /**
     * Sets the element at the given index, but only if the previous value was the expected value. This may fail spuriously.
     *
     * @param index the index
     * @param expected the expected value
     * @param newValue the new value
     * @return true on success
     */
    public boolean weakCompareAndSet(int index, short expected, short newValue) {
        return this.compareAndSet(index, expected, newValue);
    }

    /**
     * Atomically adds a delta to an element, and gets the new value.
     *
     * @param index the index
     * @param delta the delta to add to the element
     * @return the new value
     */
    public short addAndGet(int index, short delta) {
        return this.addAndGet(index, delta, false);
    }

    /**
     * Atomically adds a delta to an element, and gets the old value.
     *
     * @param index the index
     * @param delta the delta to add to the element
     * @return the old value
     */
    public short getAndAdd(int index, short delta) {
        return this.addAndGet(index, delta, true);
    }

    /**
     * Atomically increments an element and returns the old value.
     *
     * @param index the index
     * @return the old value
     */
    public short getAndIncrement(int index) {
        return this.getAndAdd(index, (short) 1);
    }

    /**
     * Atomically decrements an element and returns the old value.
     *
     * @param index the index
     * @return the old value
     */
    public short getAndDecrement(int index) {
        return this.getAndAdd(index, (short) -1);
    }

    /**
     * Atomically increments an element and returns the new value.
     *
     * @param index the index
     * @return the new value
     */
    public short incrementAndGet(int index) {
        return this.addAndGet(index, (short) 1);
    }

    /**
     * Atomically decrements an element and returns the new value.
     *
     * @param index the index
     * @return the new value
     */
    public short decrementAndGet(int index) {
        return this.addAndGet(index, (short) -1);
    }

    /**
     * Gets an array containing all the values in the array.
     *
     * The returned values are not guaranteed to be from the same time instant.
     *
     * @return the array
     */
    public short[] getArray() {
        return this.getArray(null);
    }

    /**
     * Returns a string representation of the array.
     *
     * The returned values are not guaranteed to be from the same time instant.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return Arrays.toString(this.getArray());
    }
}
