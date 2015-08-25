package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayOutEntityHeadLook implements Message {

    private final int entityId;
    private final float yaw;

    public MessagePlayOutEntityHeadLook(int entityId, float yaw) {
        this.entityId = entityId;
        this.yaw = yaw;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public float getYaw() {
        return this.yaw;
    }

}
