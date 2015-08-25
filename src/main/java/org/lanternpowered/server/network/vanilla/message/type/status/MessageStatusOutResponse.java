package org.lanternpowered.server.network.vanilla.message.type.status;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

public final class MessageStatusOutResponse implements Message {

    private final String response;

    /**
     * Creates a status response.
     * 
     * @param response The json response
     */
    public MessageStatusOutResponse(String response) {
        this.response = checkNotNull(response, "response");
    }

    /**
     * Gets the json response.
     * 
     * @return The json response
     */
    public String getResponse() {
        return this.response;
    }

}
