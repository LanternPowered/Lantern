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
