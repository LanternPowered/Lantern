package org.lanternpowered.server.network.vanilla.message.type.connection;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

public final class MessageOutDisconnect implements Message {

    private final String json;

    /**
     * Creates a new disconnect message with a specific reason.
     * 
     * @param json the reason
     */
    public MessageOutDisconnect(String json) {
        this.json = checkNotNull(json, "json");
    }

    /**
     * Gets the json disconnect message.
     * 
     * @return the json message
     */
    public String getJson() {
        return this.json;
    }

}
