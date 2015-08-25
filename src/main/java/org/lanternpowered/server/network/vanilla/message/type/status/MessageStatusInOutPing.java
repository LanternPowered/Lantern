package org.lanternpowered.server.network.vanilla.message.type.status;

import org.lanternpowered.server.network.message.Message;

public final class MessageStatusInOutPing implements Message {

    private final long time;

    public MessageStatusInOutPing(long time) {
        this.time = time;
    }

    /**
     * Gets the time value.
     * 
     * @return The time
     */
    public long getTime() {
        return this.time;
    }

}
