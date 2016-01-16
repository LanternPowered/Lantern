package org.lanternpowered.server.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.util.map.ExpirableValueMap;
import org.lanternpowered.server.util.map.SimpleExpirableValue;

import java.util.Map;
import java.util.UUID;

public class ExpirableMapTest {

    @Test
    public void test() {
        Map<String, Value> values = ImmutableMap.of(
                "A", new Value(UUID.randomUUID(), true),
                "B", new Value(UUID.randomUUID(), false)
        );

        ExpirableValueMap<String, UUID, Value> map = Maps2.createExpirableEntryMap((key, value) -> new Value(value, false));
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
