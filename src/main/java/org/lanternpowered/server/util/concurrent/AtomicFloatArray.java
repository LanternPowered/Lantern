package org.lanternpowered.server.util.concurrent;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicFloatArray implements Serializable {

    private static final long serialVersionUID = 1128664741971193255L;

    private final AtomicIntegerArray backingArray;

    /**
     * Creates a new {@link AtomicFloatArray} of the given length, with all
     * elements initially zero.
     *
     * @param length the length of the array
     */
    public AtomicFloatArray(int length) {
        this(new float[length]);
    }

    /**
     * Creates a new {@link AtomicFloatArray} with the same length as, and
     * all elements copied from, the given array.
     *
     * @param array the array to copy elements from
     * @throws NullPointerException if array is null
     */
    public AtomicFloatArray(float[] array) {
        int[] array0 = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            array0[i] = Float.floatToRawIntBits(array[i]);
        }
        this.backingArray = new AtomicIntegerArray(array0);
    }

    /**
     * Returns the length of the array.
     *
     * @return the length of the array
     */
    public final int length() {
        return this.backingArray.length();
    }

    /**
     * Gets the current value at position {@code index}.
     *
     * @param index the index
     * @return the current value
     */
    public final float get(int index) {
        return Float.intBitsToFloat(this.backingArray.get(index));
    }

    /**
     * Sets the element at position {@code index} to the given value.
     *
     * @param index the index
     * @param newValue the new value
     */
    public final void set(int index, float newValue) {
        this.backingArray.set(index, Float.floatToRawIntBits(newValue));
    }

    /**
     * Eventually sets the element at position {@code index} to the given value.
     *
     * @param index the index
     * @param newValue the new value
     */
    public final void lazySet(int index, float newValue) {
        this.backingArray.lazySet(index, Float.floatToRawIntBits(newValue));
    }

    /**
     * Atomically sets the element at position {@code index} to the given
     * value and returns the old value.
     *
     * @param index the index
     * @param newValue the new value
     * @return the previous value
     */
    public final float getAndSet(int index, float newValue) {
        return Float.intBitsToFloat(this.backingArray.getAndSet(index, Float.floatToRawIntBits(newValue)));
    }

    /**
     * Atomically sets the element at position {@code index} to the given
     * updated value if the current value {@code ==} the expected value.
     *
     * @param index the index
     * @param expect the expected value
     * @param update the new value
     * @return true if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public final boolean compareAndSet(int index, float expect, float update) {
        return this.backingArray.compareAndSet(index, Float.floatToRawIntBits(expect), Float.floatToRawIntBits(update));
    }

    /**
     * Atomically sets the element at position {@code index} to the given
     * updated value if the current value {@code ==} the expected value.
     *
     * May fail spuriously and does not provide ordering guarantees, so is only rarely an
     * appropriate alternative to {@code compareAndSet}.
     *
     * @param index the index
     * @param expect the expected value
     * @param update the new value
     * @return true if successful.
     */
    public final boolean weakCompareAndSet(int index, float expect, float update) {
        return this.compareAndSet(index, expect, update);
    }

    /**
     * Atomically increments by one the element at index {@code index}.
     *
     * @param index the index
     * @return the previous value
     */
    public final float getAndIncrement(int index) {
        return this.getAndAdd(index, 1f);
    }

    /**
     * Atomically decrements by one the element at index {@code index}.
     *
     * @param index the index
     * @return the previous value
     */
    public final float getAndDecrement(int index) {
        return this.getAndAdd(index, -1f);
    }

    private float addAndGet(int index, float delta, boolean old) {
        boolean success = false;
        float newValue = 0;
        float oldValue = 0;
        while (!success) {
            oldValue = this.get(index);
            newValue = oldValue + delta;
            success = this.compareAndSet(index, oldValue, newValue);
        }
        return old ? oldValue : newValue;
    }

    /**
     * Atomically adds the given value to the element at index {@code index}.
     *
     * @param index the index
     * @param delta the value to add
     * @return the previous value
     */
    public final float getAndAdd(int index, float delta) {
        return this.addAndGet(index, delta, true);
    }

    /**
     * Atomically increments by one the element at index {@code index}.
     *
     * @param index the index
     * @return the updated value
     */
    public final float incrementAndGet(int index) {
        return this.addAndGet(index, 1f);
    }

    /**
     * Atomically decrements by one the element at index {@code index}.
     *
     * @param index the index
     * @return the updated value
     */
    public final float decrementAndGet(int index) {
        return this.addAndGet(index, -1f);
    }

    /**
     * Atomically adds the given value to the element at index {@code index}.
     *
     * @param index the index
     * @param delta the value to add
     * @return the updated value
     */
    public final float addAndGet(int index, float delta) {
        return this.addAndGet(index, delta, false);
    }

    /**
     * Gets an array containing all the values in the array. The returned values are not
     * guaranteed to be from the same time instant.
     *
     * If an array is provided and it is the correct length, then that array will be
     * used as the destination array.
     *
     * @param array the provided array
     * @return an array containing the values in the array
     */
    public final float[] getArray(float[] array) {
        int length = this.length();
        if (array == null || array.length != length) {
            array = new float[length];
        }
        for (int i = 0; i < length; i++) {
            array[i] = this.get(i);
        }
        return array;
    }

    /**
     * Gets an array containing all the values in the array.
     *
     * The returned values are not guaranteed to be from
     * the same time instant.
     *
     * @return the array
     */
    public final float[] getArray() {
        return this.getArray(null);
    }

    /**
     * Returns the String representation of the current values of array.
     * 
     * The returned values are not guaranteed to be from
     * the same time instant.
     * 
     * @return the string representation
     */
    @Override
    public String toString() {
        return Arrays.toString(this.getArray());
    }
}
