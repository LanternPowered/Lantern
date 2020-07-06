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
package org.lanternpowered.api.util

import org.lanternpowered.server.util.BitHelper
import kotlin.math.ceil

/**
 * Represents an array for which can be specified how many bits
 * should be occupied for each value. These bits will be spread
 * out per long value. E.g. if you have 4 bits per value, there
 * will be 64 / 4 values be stored on a single long value. If
 * you use a number that's not the power of two, a few bits will
 * be unused per long, e.g. for bit per value 3, 64 % 3 = 1
 *
 * The implementation was changed for minecraft 1.16 changes, prior
 * to this version, values could be spread out over multiple long
 * values if the bits per value wasn't a power of two. This was
 * changed for performance reasons and was a breaking change.
 */
class VariableValueArray {

    /**
     * The size.
     */
    val size: Int

    /**
     * The amount of bits that are used for a single value.
     */
    val bitsPerValue: Int

    /**
     * The backing array that's used to store the values.
     */
    val backing: LongArray

    /**
     * A bitmask for a value.
     */
    private val valueMask: Long

    /**
     * How many values can be stored in a single long value.
     */
    private val valuesPerLong: Int

    /**
     * How many bits per value there would be if bitsPerValue
     * was the power of two, rounded up.
     */
    private val powOfTwoBitsVerValue: Int

    /**
     * Constructs a new [VariableValueArray].
     */
    constructor(bitsPerValue: Int, size: Int) : this(null, bitsPerValue, size)

    /**
     * Constructs a new [VariableValueArray].
     */
    constructor(bitsPerValue: Int, size: Int, backing: LongArray) : this(backing, bitsPerValue, size)

    private constructor(backing: LongArray?, bitsPerValue: Int, size: Int) {
        check(size > 0) { "size ($size) may not be negative" }
        check(bitsPerValue >= 1) { "bitsPerValue ($bitsPerValue) may not be smaller then 1" }
        check(bitsPerValue <= Int.SIZE_BITS) { "bitsPerValue ($bitsPerValue) may not be greater then ${Int.SIZE_BITS}}" }

        this.size = size
        this.bitsPerValue = bitsPerValue
        this.valueMask = (1L shl bitsPerValue) - 1L
        this.valuesPerLong = Long.SIZE_BITS / this.bitsPerValue
        this.powOfTwoBitsVerValue = BitHelper.nextPowOfTwo(bitsPerValue)

        val backingSize = ceil(size.toDouble() / this.valuesPerLong.toDouble()).toInt()
        if (backing == null) {
            this.backing = LongArray(backingSize)
        } else {
            check(backingSize == backing.size) { "expected backing size of $backingSize, but got ${backing.size}" }
            this.backing = backing
        }
    }

    /**
     * Creates a copy of this [VariableValueArray].
     */
    fun copy(): VariableValueArray = VariableValueArray(this.bitsPerValue, this.size, this.backing.copyOf())

    /**
     * Creates a copy of this [VariableValueArray] with the new [bitsPerValue].
     *
     * An [IllegalArgumentException] can be expected if any of the values won't
     * fit in the new [bitsPerValue].
     */
    fun copyWithBitsPerValue(bitsPerValue: Int): VariableValueArray {
        if (bitsPerValue == this.bitsPerValue)
            return copy()
        val copy = VariableValueArray(bitsPerValue, this.size)
        for (index in 0 until this.size)
            copy[index] = get(index)
        return copy
    }

    /**
     * Gets a value at the given [index].
     */
    operator fun get(index: Int): Int {
        return perform(index) { longIndex, indexInLong ->
            ((this.backing[longIndex] ushr indexInLong) and this.valueMask).toInt()
        }
    }

    /**
     * Sets a value at the given [index].
     */
    operator fun set(index: Int, value: Int) {
        require(value >= 0) { "value ($value) must not be negative" }
        require(value <= this.valueMask) { "value ($value) must not be greater than $valueMask" }

        perform(index) { longIndex, indexInLong ->
            // Remove the old value
            val cleaned = this.backing[longIndex] and (this.valueMask shl indexInLong).inv()
            // Update the new value
            this.backing[longIndex] = cleaned or (value.toLong() shl indexInLong)
        }
    }

    /**
     * Creates an iterator over the elements of the array.
     */
    operator fun iterator(): IntIterator {
        return object : IntIterator() {
            var index = 0
            override fun hasNext(): Boolean = this.index < size
            override fun nextInt(): Int {
                if (!hasNext())
                    throw NoSuchElementException()
                return get(this.index++)
            }
        }
    }

    private inline fun <R> perform(index: Int, fn: (longIndex: Int, indexInLong: Int) -> R): R {
        if (index < 0)
            throw IndexOutOfBoundsException("index ($index) must not be negative")
        if (index >= this.size)
            throw IndexOutOfBoundsException("index ($index) must not be greater than the size ($size)")

        // The long the value is located in
        val longIndex: Int
        // The value start index
        val indexInLong: Int

        // Is power of two
        if (this.powOfTwoBitsVerValue == this.bitsPerValue) {
            val bitPosition = index * this.bitsPerValue
            longIndex = bitPosition shr 6
            indexInLong = bitPosition and 0x3f
        } else {
            longIndex = (index * this.powOfTwoBitsVerValue) shr 6
            indexInLong = (index - longIndex * this.valuesPerLong) * this.bitsPerValue
        }

        return fn(longIndex, indexInLong)
    }
}
