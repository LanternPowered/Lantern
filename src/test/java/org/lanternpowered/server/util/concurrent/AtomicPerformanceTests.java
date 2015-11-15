/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.util.concurrent;

import java.util.concurrent.atomic.AtomicIntegerArray;

import org.junit.Test;

public class AtomicPerformanceTests {

    private final static String MESSAGE = "%s for %s tests took: %s ms";
    private final static int TESTS = 10000;

    @Test
    public void testSetPerformance() {
        AtomicIntegerArray array0 = new AtomicIntegerArray(10000);
        long time = System.currentTimeMillis();
        for (int i = 0; i < TESTS; i++) {
            array0.set(i, i);
        }
        System.out.println(String.format(MESSAGE, "AtomicIntegerArray#set",
                TESTS, System.currentTimeMillis() - time));
        AtomicShortArray array1 = new AtomicShortArray(10000);
        time = System.currentTimeMillis();
        for (int i = 0; i < TESTS; i++) {
            array1.set(i, (short) i);
        }
        System.out.println(String.format(MESSAGE, "AtomicShortArray#set",
                TESTS, System.currentTimeMillis() - time));
        AtomicByteArray array2 = new AtomicByteArray(10000);
        time = System.currentTimeMillis();
        for (int i = 0; i < TESTS; i++) {
            array2.set(i, (byte) (i % 255));
        }
        System.out.println(String.format(MESSAGE, "AtomicByteArray#set",
                TESTS, System.currentTimeMillis() - time));
        AtomicNibbleArray array3 = new AtomicNibbleArray(10000);
        time = System.currentTimeMillis();
        for (int i = 0; i < TESTS; i++) {
            array3.set(i, (byte) (i % 15));
        }
        System.out.println(String.format(MESSAGE, "AtomicNibbleArray#set",
                TESTS, System.currentTimeMillis() - time));
    }
}
