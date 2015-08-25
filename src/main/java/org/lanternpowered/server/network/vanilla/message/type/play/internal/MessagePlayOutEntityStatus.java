package org.lanternpowered.server.network.vanilla.message.type.play.internal;

import org.lanternpowered.server.network.message.Message;

public class MessagePlayOutEntityStatus implements Message {

    private final int entityId;
    private final int status;

    public MessagePlayOutEntityStatus(int entityId, int status) {
        this.entityId = entityId;
        this.status = status;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getStatus() {
        return this.status;
    }
}
