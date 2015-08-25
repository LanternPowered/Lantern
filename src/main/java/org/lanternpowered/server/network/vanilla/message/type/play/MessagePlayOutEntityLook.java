package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayOutEntityLook implements Message {

    private final int entityId;
    private final boolean onGround;

    private final float yaw;
    private final float pitch;

    public MessagePlayOutEntityLook(int entityId, float yaw, float pitch, boolean onGround) {
        this.onGround = onGround;
        this.entityId = entityId;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

}
