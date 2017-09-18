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

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicFloat extends Number {

    private static final long serialVersionUID = 4712809432683706435L;

    private final AtomicInteger value;

    /**
     * Creates a new {@link AtomicFloat} with initial value {@code 0}.
     */
    public AtomicFloat() {
        this(0f);
    }

    /**
     * Creates a new {@link AtomicFloat} with the given initial value.
     *
     * @param initialValue the initial value
     */
    public AtomicFloat(float initialValue) {
        this.value = new AtomicInteger(Float.floatToRawIntBits(initialValue));
    }

    /**
     * Gets the current value.
     *
     * @return the current value
     */
    public final float get() {
        return Float.intBitsToFloat(this.value.get());
    }

    /**
     * Sets to the given value.
     *
     * @param newValue the new value
     */
    public final void set(int newValue) {
        this.value.set(Float.floatToRawIntBits(newValue));
    }

    /**
     * Eventually sets to the given value.
     *
     * @param newValue the new value
     */
    public final void lazySet(float newValue) {
        this.value.lazySet(Float.floatToRawIntBits(newValue));
    }

    /**
     * Atomically sets to the given value and returns the old value.
     *
     * @param newValue the new value
     * @return the previous value
     */
    public final float getAndSet(float newValue) {
        return Float.intBitsToFloat(this.value.getAndSet(Float.floatToRawIntBits(newValue)));
    }

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     *
     * @param expect the expected value
     * @param update the new value
     * @return true if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public final boolean compareAndSet(float expect, float update) {
        return this.value.compareAndSet(Float.floatToRawIntBits(expect), Float.floatToRawIntBits(update));
    }

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     *
     * May fail spuriously and does not provide ordering guarantees, so is only rarely an
     * appropriate alternative to {@code compareAndSet}.
     *
     * @param expect the expected value
     * @param update the new value
     * @return true if successful.
     */
    public final boolean weakCompareAndSet(float expect, float update) {
        return this.value.weakCompareAndSet(Float.floatToRawIntBits(expect), Float.floatToRawIntBits(update));
    }

    private float addAndGet(float delta, boolean old) {
        while (true) {
            int oldIntValue = this.value.get();
            float oldValue = Float.intBitsToFloat(oldIntValue);
            float newValue = oldValue + delta;
            if (this.value.compareAndSet(oldIntValue, Float.floatToRawIntBits(newValue))) {
                return old ? oldValue : newValue;
            }
        }
    }

    /**
     * Atomically increments by one the current value.
     *
     * @return the previous value
     */
    public final float getAndIncrement() {
        return this.getAndAdd(1f);
    }

    /**
     * Atomically decrements by one the current value.
     *
     * @return the previous value
     */
    public final float getAndDecrement() {
        return this.getAndAdd(-1f);
    }

    /**
     * Atomically adds the given value to the current value.
     *
     * @param delta the value to add
     * @return the previous value
     */
    public final float getAndAdd(float delta) {
        return this.addAndGet(delta, true);
    }

    /**
     * Atomically increments by one the current value.
     *
     * @return the updated value
     */
    public final float incrementAndGet() {
        return this.addAndGet(1f);
    }

    /**
     * Atomically decrements by one the current value.
     *
     * @return the updated value
     */
    public final float decrementAndGet() {
        return this.addAndGet(-1f);
    }

    /**
     * Atomically adds the given value to the current value.
     *
     * @param delta the value to add
     * @return the updated value
     */
    public final float addAndGet(float delta) {
        return this.addAndGet(delta, false);
    }

    /**
     * Returns the String representation of the current value.
     * 
     * @return the string representation
     */
    @Override
    public String toString() {
        return Float.toString(this.get());
    }

    @Override
    public int intValue() {
        return (int) this.get();
    }

    @Override
    public long longValue() {
        return (long) this.get();
    }

    @Override
    public float floatValue() {
        return this.get();
    }

    @Override
    public double doubleValue() {
        return (double) this.get();
    }
    
}
