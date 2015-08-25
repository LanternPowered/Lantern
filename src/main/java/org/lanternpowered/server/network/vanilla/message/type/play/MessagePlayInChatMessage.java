package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayInChatMessage implements Message {

    private final String message;

    public MessagePlayInChatMessage(String message) {
        this.message = checkNotNull(message, "message");
    }

    /**
     * Gets the message received from the client.
     * 
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

}
