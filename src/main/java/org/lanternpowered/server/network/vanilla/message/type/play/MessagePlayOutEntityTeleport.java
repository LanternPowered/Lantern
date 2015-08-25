package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

import com.flowpowered.math.vector.Vector3d;

public final class MessagePlayOutEntityTeleport implements Message {

    private final int entityId;
    private final boolean onGround;

    private final float yaw;
    private final float pitch;

    private final Vector3d position;

    public MessagePlayOutEntityTeleport(int entityId, Vector3d position, float yaw, float pitch, boolean onGround) {
        this.position = checkNotNull(position, "position");
        this.onGround = onGround;
        this.entityId = entityId;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Vector3d getPosition() {
        return this.position;
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
