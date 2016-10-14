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
package org.lanternpowered.server.util.collect.expirable;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.lanternpowered.server.util.collect.Lists2;

public class ExpirableValueListTest {

    private static ExpirableValueList<String, Value> createList() {
        final ExpirableValueList<String, Value> list = Lists2.createExpirableValueList(value -> new Value(value, false));
        list.getBacking().addAll(Lists.newArrayList(
                new Value("A", false),
                new Value("B", true),
                new Value("C", true),
                new Value("D", false),
                new Value("E", false),
                new Value("Z", true),
                new Value("A", false),
                new Value("A", true)));
        return list;
    }

    @Test
    public void testSize() {
        assertEquals(4, createList().size());
    }

    @Test
    public void testRemove() {
        assertEquals("D", createList().remove(1));
        assertEquals("A", createList().remove(3));
    }

    @Test
    public void testSet() {
        final ExpirableValueList<String, Value> list = createList();
        assertEquals("D", list.set(1, "F"));
        list.getBacking().get(1).expired = true;
        assertEquals("E", list.set(1, "G"));
        assertEquals(3, list.size());
    }

    @Test
    public void testGet() {
        assertEquals("D", createList().get(1));
        assertEquals("A", createList().get(3));
    }

    private static class Value extends SimpleExpirableValue<String> {

        private boolean expired;

        public Value(String value, boolean expired) {
            super(value);
            this.expired = expired;
        }

        @Override
        public boolean isExpired() {
            return this.expired;
        }
    }
}
