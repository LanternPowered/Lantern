package org.lanternpowered.server.network.forge.message.type.handshake;

import java.util.Map;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.plugin.PluginContainer;

import com.google.common.collect.ImmutableMap;

public final class MessageForgeHandshakeInOutModList implements Message {

    private final Map<String, String> entries;

    public MessageForgeHandshakeInOutModList(Map<String, String> entries) {
        this.entries = ImmutableMap.copyOf(entries);
    }

    public MessageForgeHandshakeInOutModList(Iterable<PluginContainer> plugins) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (PluginContainer plugin : plugins) {
            builder.put(plugin.getId(), plugin.getVersion());
        }
        this.entries = builder.build();
    }

    public Map<String, String> getEntries() {
        return this.entries;
    }
}
