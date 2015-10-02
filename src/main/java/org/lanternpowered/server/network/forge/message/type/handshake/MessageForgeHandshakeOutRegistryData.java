package org.lanternpowered.server.network.forge.message.type.handshake;

import java.util.List;
import java.util.Map;

import org.lanternpowered.server.network.message.Message;

import com.google.common.collect.ImmutableList;

public final class MessageForgeHandshakeOutRegistryData implements Message {

    private final List<Entry> entries;

    public MessageForgeHandshakeOutRegistryData(Iterable<Entry> entries) {
        this.entries = ImmutableList.copyOf(entries);
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public static class Entry {

        private final String name;
        private final Map<String, Integer> ids;
        private final List<String> substitutions;

        public Entry(String name, Map<String, Integer> ids, List<String> substitutions) {
            this.substitutions = substitutions;
            this.name = name;
            this.ids = ids;
        }

        public String getName() {
            return this.name;
        }

        public Map<String, Integer> getIds() {
            return this.ids;
        }

        public List<String> getSubstitutions() {
            return this.substitutions;
        }
    }
}
