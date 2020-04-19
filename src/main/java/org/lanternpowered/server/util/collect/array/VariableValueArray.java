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
package org.lanternpowered.server.util.collect.array;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class VariableValueArray {

    private final long[] backing;
    private final int capacity;
    private final int bitsPerValue;
    private final long valueMask;

    public VariableValueArray(int bitsPerValue, int capacity) {
        this(null, bitsPerValue, capacity);
    }

    public VariableValueArray(int bitsPerValue, int capacity, long[] backing) {
        this(checkNotNull(backing), bitsPerValue, capacity);
    }

    private VariableValueArray(@Nullable long[] backing, int bitsPerValue, int capacity) {
        checkArgument(capacity > 0, "capacity (%s) may not be negative", capacity);
        checkArgument(bitsPerValue >= 1, "bitsPerValue (%s) may not be smaller then 1", bitsPerValue);
        checkArgument(bitsPerValue <= 64, "bitsPerValue (%s) may not be greater then 64", bitsPerValue);
        final int backingSize = (int) Math.ceil((bitsPerValue * capacity) / 64.0);
        if (backing == null) {
            this.backing = new long[backingSize];
        } else {
            checkArgument(backingSize == backing.length,
                    "expected backing size of %s, but got %s", backingSize, backing.length);
            this.backing = backing;
        }
        this.bitsPerValue = bitsPerValue;
        this.valueMask = (1L << bitsPerValue) - 1L;
        this.capacity = capacity;
    }

    public long[] getBacking() {
        return this.backing;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getBitsPerValue() {
        return this.bitsPerValue;
    }

    public int get(int index) {
        checkIndex(index);

        index *= this.bitsPerValue;
        int i0 = index >> 6;
        int i1 = index & 0x3f;

        long value = this.backing[i0] >>> i1;
        int i2 = i1 + this.bitsPerValue;
        // The value is divided over two long values
        if (i2 > 64) {
            value |= this.backing[++i0] << (64 - i1);
        }

        return (int) (value & this.valueMask);
    }

    public void set(int index, int value) {
        checkIndex(index);

        if (value < 0) {
            throw new IllegalArgumentException(String.format("value (%s) must not be negative", value));
        }
        if (value > this.valueMask) {
            throw new IllegalArgumentException(String.format("value (%s) must not be greater then %s", value, this.valueMask));
        }

        index *= this.bitsPerValue;
        int i0 = index >> 6;
        int i1 = index & 0x3f;

        this.backing[i0] = (this.backing[i0] & ~(this.valueMask << i1)) | (value & this.valueMask) << i1;
        int i2 = i1 + this.bitsPerValue;
        // The value is divided over two long values
        if (i2 > 64) {
            i0++;
            this.backing[i0] = this.backing[i0] & ~((1L << (i2 - 64)) - 1L) | value >> (64 - i1);
        }
    }

    public VariableValueArray copy() {
        return new VariableValueArray(Arrays.copyOf(this.backing, this.backing.length), this.bitsPerValue, this.capacity);
    }

    private void checkIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(String.format("index (%s) must not be negative", index));
        }
        if (index >= this.capacity) {
            throw new IndexOutOfBoundsException(String.format("index (%s) must not be greater then the capacity (%s)", index, this.capacity));
        }
    }
}
