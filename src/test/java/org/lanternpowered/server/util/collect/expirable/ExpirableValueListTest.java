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
