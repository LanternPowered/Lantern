package org.lanternpowered.server.network.vanilla.message.type.play;

import java.util.Set;

import org.lanternpowered.server.network.message.Message;

import com.google.common.collect.ImmutableSet;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

public final class MessagePlayOutStatistics implements Message {
    private final ImmutableSet<Entry> entries;

    public MessagePlayOutStatistics(Set<Entry> entries) {
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
