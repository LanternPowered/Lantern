package org.lanternpowered.server.text.chat;

import org.spongepowered.api.text.chat.ChatType;

public class LanternChatType implements ChatType {

    private final String name;
    private final byte internalId;

    public LanternChatType(String name, byte internalId) {
        this.internalId = internalId;
        this.name = name;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public byte getInternalId() {
        return this.internalId;
    }
}
