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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.lanternpowered.server.util.collect.Maps2;

import java.util.Map;
import java.util.UUID;

public class ExpirableValueMapTest {

    @Test
    public void test() {
        Map<String, Value> values = ImmutableMap.of(
                "A", new Value(UUID.randomUUID(), true),
                "B", new Value(UUID.randomUUID(), false)
        );

        ExpirableValueMap<String, UUID, Value> map = Maps2.createExpirableValueMap((key, value) -> new Value(value, false));
        map.getBacking().putAll(values);

        assertFalse(map.containsKey("A"));
        assertTrue(map.containsKey("B"));

        map.put("A", UUID.randomUUID());
        assertTrue(map.containsKey("A"));

        map.getBacking().get("A").expired = true;
        assertFalse(map.containsKey("A"));
    }

    private static class Value extends SimpleExpirableValue<UUID> {

        private boolean expired;

        public Value(UUID value, boolean expired) {
            super(value);
            this.expired = expired;
        }

        @Override
        public boolean isExpired() {
            return this.expired;
        }
    }
}
