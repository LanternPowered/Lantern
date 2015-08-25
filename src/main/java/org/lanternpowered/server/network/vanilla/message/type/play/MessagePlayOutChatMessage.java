package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;

public class MessagePlayOutChatMessage implements Message {

    private final Text message;
    private final ChatType chatType;

    public MessagePlayOutChatMessage(Text message, ChatType chatType) {
        this.chatType = chatType;
        this.message = message;
    }

    public Text getMessage() {
        return this.message;
    }

    public ChatType getChatType() {
        return this.chatType;
    }
}
