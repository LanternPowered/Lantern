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
package org.lanternpowered.server.util.roman;

import static com.google.common.base.Preconditions.checkNotNull;

import it.unimi.dsi.fastutil.chars.Char2IntMap;
import it.unimi.dsi.fastutil.chars.Char2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.Locale;

public final class RomanNumber extends Number {

    /**
     * Parses the string argument as an {@link RomanNumber}.
     *
     * @param s The string to parse
     * @return The roman number
     * @throws IllegalRomanNumberException If the specified is negative, zero or greater then 3999
     */
    public static RomanNumber parse(String s) throws NumberFormatException, IllegalRomanNumberException {
        checkNotNull(s);
        int result = 0;
        final String roman = s.toUpperCase(Locale.ENGLISH);
        for (int i = 0; i < roman.length() - 1; i++) {
            final int value = parseChar(roman, i);
            final int next = parseChar(roman, i + 1);
            if (value < next) {
                result -= value;
            } else {
                result += value;
            }
        }
        result += parseChar(roman, roman.length() - 1);
        checkIllegalNumber(result);
        return new RomanNumber(roman, result);
    }

    /**
     * Converts the integer value into a {@link RomanNumber}.
     *
     * @param value The integer value
     * @return The roman number
     * @throws IllegalRomanNumberException If the specified is negative, zero or greater then 3999
     */
    public static RomanNumber valueOf(int value) throws IllegalRomanNumberException {
        checkIllegalNumber(value);
        final StringBuilder builder = new StringBuilder();
        for (Int2ObjectMap.Entry<String> entry : VALUES_TO_STRING.int2ObjectEntrySet()) {
            final long count = value / entry.getIntKey();
            for (long i = 0; i < count; i++) {
                builder.append(entry.getValue());
            }
            value = value % entry.getIntKey();
        }
        return new RomanNumber(builder.toString(), value);
    }

    private static void checkIllegalNumber(int value) throws IllegalRomanNumberException {
        if (value <= 0) {
            throw new IllegalRomanNumberException("Roman numbers cannot be negative or zero.");
        }
        if (value > 3999) {
            throw new IllegalRomanNumberException("Roman numbers cannot be greater then 3999.");
        }
    }

    private static int parseChar(String string, int index) {
        final char c = string.charAt(index);
        final int v = CHAR_VALUES.get(c);
        if (v == INVALID_CHAR) {
            throw new NumberFormatException("Invalid Roman number, illegal character "
                    + c + " at index " + index + " of input: " + string);
        }
        return v;
    }

    private static final Char2IntMap CHAR_VALUES = new Char2IntOpenHashMap();
    private static final int INVALID_CHAR = -1;

    private static final Int2ObjectMap<String> VALUES_TO_STRING = new Int2ObjectLinkedOpenHashMap<>();

    static {
        CHAR_VALUES.put('M', 1000);
        CHAR_VALUES.put('D', 500);
        CHAR_VALUES.put('C', 100);
        CHAR_VALUES.put('L', 50);
        CHAR_VALUES.put('X', 10);
        CHAR_VALUES.put('V', 5);
        CHAR_VALUES.put('I', 1);
        CHAR_VALUES.defaultReturnValue(INVALID_CHAR);
        VALUES_TO_STRING.put(1000, "M");
        VALUES_TO_STRING.put(900,  "CM");
        VALUES_TO_STRING.put(500,  "D");
        VALUES_TO_STRING.put(400,  "CD");
        VALUES_TO_STRING.put(100,  "C");
        VALUES_TO_STRING.put(90,   "XC");
        VALUES_TO_STRING.put(50,   "L");
        VALUES_TO_STRING.put(40,   "XL");
        VALUES_TO_STRING.put(10,   "X");
        VALUES_TO_STRING.put(9,    "IX");
        VALUES_TO_STRING.put(5,    "V");
        VALUES_TO_STRING.put(4,    "IV");
        VALUES_TO_STRING.put(1,    "I");
    }

    private final String roman;
    private final int value;

    private RomanNumber(String roman, int value) {
        this.roman = roman;
        this.value = value;
    }

    /**
     * Adds the specified {@link RomanNumber} to this number
     * and returns the result {@link RomanNumber}.
     *
     * @param number The number to add
     * @return The result number
     * @throws IllegalRomanNumberException If the result number is negative, zero or greater then 3999
     */
    public RomanNumber add(RomanNumber number) throws IllegalRomanNumberException {
        checkNotNull(number, "number");
        final int result = this.value + number.value;
        checkIllegalNumber(result);
        return valueOf(result);
    }

    /**
     * Subtracts the specified {@link RomanNumber} from this number
     * and returns the result {@link RomanNumber}.
     *
     * @param number The number to subtract
     * @return The result number
     * @throws IllegalRomanNumberException If the result number is negative, zero or greater then 3999
     */
    public RomanNumber subtract(RomanNumber number) throws IllegalRomanNumberException {
        checkNotNull(number, "number");
        final int result = this.value - number.value;
        checkIllegalNumber(result);
        return valueOf(result);
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public int intValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.roman;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.value);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RomanNumber && ((RomanNumber) o).value == this.value;
    }
}
