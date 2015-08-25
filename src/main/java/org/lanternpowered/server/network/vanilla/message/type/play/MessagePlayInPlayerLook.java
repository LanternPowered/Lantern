package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayInPlayerLook implements Message {

    private final boolean onGround;

    private final float yaw;
    private final float pitch;

    public MessagePlayInPlayerLook(float yaw, float pitch, boolean onGround) {
        this.onGround = onGround;
        this.pitch = pitch;
        this.yaw = yaw;
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
