package org.lanternpowered.server.text.chat;

import org.spongepowered.api.text.chat.ChatType;

public class LanternChatType implements ChatType {

    private final String name;

    public LanternChatType(String name) {
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
}
