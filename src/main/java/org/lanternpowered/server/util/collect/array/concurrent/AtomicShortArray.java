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
package org.lanternpowered.server.util.collect.array.concurrent;

import static org.lanternpowered.server.util.Conditions.checkArrayRange;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerArray;

import javax.annotation.Nullable;

public class AtomicShortArray implements Serializable {

    private static final long serialVersionUID = 3434275139515033068L;

    private static boolean even(int index) {
        return (index & 0x1) == 0;
    }

    private static int key(short odd, short even) {
        return odd << 16 | even & 0xffff;
    }

    private static short keyOdd(int key) {
        return (short) ((key >> 16) & 0xffff);
    }

    private static short keyEven(int key) {
        return (short) (key & 0xffff);
    }

    private final int length;
    private final int backingArraySize;
    private final AtomicIntegerArray backingArray;

    /**
     * Creates a new {@link AtomicShortArray} of the given length, with all
     * elements initially zero.
     *
     * @param length The length of the array
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
     * @param array The content and length that should be used
     */
    public AtomicShortArray(short[] array) {
        this.length = array.length;
        this.backingArraySize = (this.length & 1) + (this.length >> 1);
        final int[] array0 = new int[this.backingArraySize];
        for (int i = 0; i < this.backingArraySize; i++) {
            int j = i << 1;
            int value = array[j];
            if (++j < array.length) {
                value |= array[j] << 16;
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
     * @return The length
     */
    public final int length() {
        return this.length;
    }

    /**
     * Gets an element from the array at a given index.
     *
     * @param index The index
     * @return The element
     */
    public final short get(int index) {
        checkArrayRange(index, this.length);
        int packed = this.getPacked(index);
        return even(index) ? keyEven(packed) : keyOdd(packed);
    }

    /**
     * Sets an element in the array at a given index and returns the old value.
     *
     * @param index The index
     * @param value The new value
     * @return The old value
     */
    public final int getAndSet(int index, short value) {
        checkArrayRange(index, this.length);
        boolean success = false;
        short odd = 0;
        short even = 0;
        short oldValue = 0;
        int backingIndex = index >> 1;
        boolean evenIndex = even(index);
        while (!success) {
            int oldPacked = this.backingArray.get(backingIndex);
            if (evenIndex) {
                even = value;
                oldValue = keyEven(oldPacked);
                odd = keyOdd(oldPacked);
            } else {
                oldValue = keyOdd(oldPacked);
                even = keyEven(oldPacked);
                odd = value;
            }
            int newPacked = key(odd, even);
            success = this.backingArray.compareAndSet(backingIndex, oldPacked, newPacked);
        }
        return oldValue;
    }

    /**
     * Sets two elements in the array at once. The index must be even.
     *
     * @param index The index
     * @param even The new value for the element at (index)
     * @param odd The new value for the element at (index + 1)
     */
    public final void set(int index, short even, short odd) {
        checkArrayRange(index, this.length);
        if ((index & 0x1) != 0) {
            throw new IllegalArgumentException("When setting 2 elements at once, the index must be even!");
        }
        this.backingArray.set(index >> 1, key(odd, even));
    }

    /**
     * Sets the element at the given index, but only if the previous value was the expected value.
     *
     * @param index The index
     * @param expected The expected value
     * @param newValue The new value
     * @return True on success
     */
    public final boolean compareAndSet(int index, short expected, short newValue) {
        checkArrayRange(index, this.length);
        boolean success = false;
        short odd = 0;
        short even = 0;
        short oldValue = 0;
        int backingIndex = index >> 1;
        boolean evenIndex = even(index);
        while (!success) {
            int oldPacked = this.backingArray.get(backingIndex);
            if (evenIndex) {
                even = newValue;
                oldValue = keyEven(oldPacked);
                odd = keyOdd(oldPacked);
            } else {
                oldValue = keyOdd(oldPacked);
                even = keyEven(oldPacked);
                odd = newValue;
            }
            if (oldValue != expected) {
                return false;
            }
            int newPacked = key(odd, even);
            success = this.backingArray.compareAndSet(backingIndex, oldPacked, newPacked);
        }
        return true;
    }

    private short addAndGet(int index, short delta, boolean old) {
        checkArrayRange(index, this.length);
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
     * @param array The provided array
     * @return An array containing the values in the array
     */
    public final short[] getArray(@Nullable short[] array) {
        if (array == null || array.length != this.length) {
            array = new short[this.length];
        }
        for (int i = 0; i < this.length; i++) {
            int packed = this.getPacked(i);
            array[i] = keyEven(packed);
            if (++i < this.length) {
                array[i] = keyOdd(packed);
            }
        }
        return array;
    }

    /**
     * Sets an element to the given value.
     *
     * @param index The index
     * @param value The new value
     */
    public final void set(int index, short value) {
        this.getAndSet(index, value);
    }

    /**
     * Sets an element to the given value, but the update may not happen immediately.
     *
     * @param index The index
     * @param value The new value
     */
    public final void lazySet(int index, short value) {
        this.set(index, value);
    }

    /**
     * Sets the element at the given index, but only if the previous value was the expected value. This may fail spuriously.
     *
     * @param index The index
     * @param expected The expected value
     * @param newValue The new value
     * @return True on success
     */
    public final boolean weakCompareAndSet(int index, short expected, short newValue) {
        return this.compareAndSet(index, expected, newValue);
    }

    /**
     * Atomically adds a delta to an element, and gets the new value.
     *
     * @param index The index
     * @param delta The delta to add to the element
     * @return The new value
     */
    public final short addAndGet(int index, short delta) {
        return this.addAndGet(index, delta, false);
    }

    /**
     * Atomically adds a delta to an element, and gets the old value.
     *
     * @param index The index
     * @param delta The delta to add to the element
     * @return The old value
     */
    public final short getAndAdd(int index, short delta) {
        return this.addAndGet(index, delta, true);
    }

    /**
     * Atomically increments an element and returns the old value.
     *
     * @param index The index
     * @return The old value
     */
    public final short getAndIncrement(int index) {
        return this.getAndAdd(index, (short) 1);
    }

    /**
     * Atomically decrements an element and returns the old value.
     *
     * @param index The index
     * @return The old value
     */
    public final short getAndDecrement(int index) {
        return this.getAndAdd(index, (short) -1);
    }

    /**
     * Atomically increments an element and returns the new value.
     *
     * @param index The index
     * @return The new value
     */
    public final short incrementAndGet(int index) {
        return this.addAndGet(index, (short) 1);
    }

    /**
     * Atomically decrements an element and returns the new value.
     *
     * @param index The index
     * @return The new value
     */
    public final short decrementAndGet(int index) {
        return this.addAndGet(index, (short) -1);
    }

    /**
     * Gets an array containing all the values in the array.
     *
     * The returned values are not guaranteed to be from the same time instant.
     *
     * @return The array
     */
    public final short[] getArray() {
        return this.getArray(null);
    }

    /**
     * Returns a string representation of the array.
     *
     * The returned values are not guaranteed to be from the same time instant.
     *
     * @return The string representation
     */
    @Override
    public String toString() {
        return Arrays.toString(this.getArray());
    }
}
