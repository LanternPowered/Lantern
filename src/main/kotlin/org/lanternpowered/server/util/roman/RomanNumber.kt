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
package org.lanternpowered.server.util.roman

import com.google.common.base.Preconditions
import it.unimi.dsi.fastutil.chars.Char2IntMap
import it.unimi.dsi.fastutil.chars.Char2IntOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import java.util.Locale

class RomanNumber private constructor(private val roman: String, private val value: Int) : Number() {

    companion object {

        /**
         * Converts the integer value into a [RomanNumber].
         *
         * @param value The integer value
         * @return The roman number
         * @throws IllegalRomanNumberException If the specified is negative, zero or greater then 3999
         */
        @Throws(IllegalRomanNumberException::class)
        fun toString(value: Int): String {
            checkIllegalNumber(value)
            val builder = StringBuilder()
            var remaining = value
            for (entry in VALUES_TO_STRING.int2ObjectEntrySet()) {
                val count = remaining / entry.intKey.toLong()
                for (i in 0 until count) {
                    builder.append(entry.value)
                }
                remaining %= entry.intKey
            }
            return builder.toString()
        }

        /**
         * Parses the string argument as an [RomanNumber].
         *
         * @param s The string to parse
         * @return The roman number
         * @throws IllegalRomanNumberException If the specified is negative, zero or greater then 3999
         */
        @JvmStatic
        fun parse(s: String): RomanNumber {
            Preconditions.checkNotNull(s)
            var result = 0
            val roman = s.toUpperCase(Locale.ENGLISH)
            for (i in 0 until roman.length - 1) {
                val value = parseChar(roman, i)
                val next = parseChar(roman, i + 1)
                if (value < next) {
                    result -= value
                } else {
                    result += value
                }
            }
            result += parseChar(roman, roman.length - 1)
            checkIllegalNumber(result)
            return RomanNumber(roman, result)
        }

        /**
         * Converts the integer value into a [RomanNumber].
         *
         * @param value The integer value
         * @return The roman number
         * @throws IllegalRomanNumberException If the specified is negative, zero or greater then 3999
         */
        @JvmStatic
        fun valueOf(value: Int): RomanNumber {
            return RomanNumber(toString(value), value)
        }

        private fun checkIllegalNumber(value: Int) {
            if (value <= 0) {
                throw IllegalRomanNumberException("Roman numbers cannot be negative or zero.")
            }
            if (value > 3999) {
                throw IllegalRomanNumberException("Roman numbers cannot be greater then 3999.")
            }
        }

        private fun parseChar(string: String, index: Int): Int {
            val c = string[index]
            val v = CHAR_VALUES[c]
            if (v == INVALID_CHAR) {
                throw NumberFormatException("Invalid Roman number, illegal character "
                        + c + " at index " + index + " of input: " + string)
            }
            return v
        }

        private val CHAR_VALUES: Char2IntMap = Char2IntOpenHashMap()
        private const val INVALID_CHAR = -1
        private val VALUES_TO_STRING: Int2ObjectMap<String> = Int2ObjectLinkedOpenHashMap()

        init {
            CHAR_VALUES['M'] = 1000
            CHAR_VALUES['D'] = 500
            CHAR_VALUES['C'] = 100
            CHAR_VALUES['L'] = 50
            CHAR_VALUES['X'] = 10
            CHAR_VALUES['V'] = 5
            CHAR_VALUES['I'] = 1
            CHAR_VALUES.defaultReturnValue(INVALID_CHAR)
            VALUES_TO_STRING[1000] = "M"
            VALUES_TO_STRING[900] = "CM"
            VALUES_TO_STRING[500] = "D"
            VALUES_TO_STRING[400] = "CD"
            VALUES_TO_STRING[100] = "C"
            VALUES_TO_STRING[90] = "XC"
            VALUES_TO_STRING[50] = "L"
            VALUES_TO_STRING[40] = "XL"
            VALUES_TO_STRING[10] = "X"
            VALUES_TO_STRING[9] = "IX"
            VALUES_TO_STRING[5] = "V"
            VALUES_TO_STRING[4] = "IV"
            VALUES_TO_STRING[1] = "I"
        }
    }

    /**
     * Adds the specified [RomanNumber] to this number
     * and returns the result [RomanNumber].
     *
     * @param number The number to add
     * @return The result number
     * @throws IllegalRomanNumberException If the result number is negative, zero or greater then 3999
     */
    operator fun plus(number: RomanNumber): RomanNumber {
        val result = value + number.value
        checkIllegalNumber(result)
        return valueOf(result)
    }

    /**
     * Subtracts the specified [RomanNumber] from this number
     * and returns the result [RomanNumber].
     *
     * @param number The number to subtract
     * @return The result number
     * @throws IllegalRomanNumberException If the result number is negative, zero or greater then 3999
     */
    operator fun minus(number: RomanNumber): RomanNumber {
        val result = value - number.value
        checkIllegalNumber(result)
        return valueOf(result)
    }

    override fun toInt(): Int = this.value
    override fun toByte(): Byte = this.value.toByte()
    override fun toShort(): Short = this.value.toShort()
    override fun toChar(): Char = this.value.toChar()
    override fun toLong(): Long = this.value.toLong()
    override fun toFloat(): Float = this.value.toFloat()
    override fun toDouble(): Double = this.value.toDouble()

    override fun toString(): String = this.roman
    override fun hashCode(): Int = Integer.hashCode(this.value)
    override fun equals(other: Any?): Boolean = other is RomanNumber && other.value == this.value
}
