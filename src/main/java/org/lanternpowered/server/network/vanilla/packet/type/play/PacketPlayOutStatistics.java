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
package org.lanternpowered.server.network.vanilla.packet.type.play;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.network.message.Packet;

import java.util.Set;

public final class PacketPlayOutStatistics implements Packet {
    private final ImmutableSet<Entry> entries;

    public PacketPlayOutStatistics(Set<Entry> entries) {
        this.entries = ImmutableSet.copyOf(checkNotNull(entries, "entries"));
    }

    /**
     * Gets the entries.
     * 
     * @return the entries
     */
    public ImmutableSet<Entry> getEntries() {
        return this.entries;
    }

    public static class Entry {
        private final String name;
        private final int value;

        /**
         * Creates a new statistics message entry.
         * 
         * @param name the name
         * @param value the value
         */
        public Entry(String name, int value) {
            this.name = checkNotNullOrEmpty(name, "name");
            this.value = value;
        }

        /**
         * Gets the name of the entry.
         * 
         * @return the name
         */
        public String getName() {
            return this.name;
        }

        /**
         * Gets the value of the entry.
         * 
         * @return the value
         */
        public int getValue() {
            return this.value;
        }

    }
}
