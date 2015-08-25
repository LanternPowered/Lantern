package org.lanternpowered.server.network.vanilla.message.type.connection;

import org.lanternpowered.server.network.message.MessageAsync;

public final class MessageInOutPing implements MessageAsync {

    private final int id;

    /**
     * Creates a ping message with a specific keep alive id.
     * 
     * @param id the id
     */
    public MessageInOutPing(int id) {
        this.id = id;
    }

    /**
     * Gets the keep alive id.
     * 
     * @return the id
     */
    public int getKeepAliveId() {
        return this.id;
    }

}
