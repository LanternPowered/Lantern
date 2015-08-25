package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

import com.flowpowered.math.vector.Vector3d;

public final class MessagePlayOutEntityLookAndRelativeMove implements Message {

    private final int entityId;
    private final boolean onGround;

    private final float yaw;
    private final float pitch;

    private final Vector3d delta;

    public MessagePlayOutEntityLookAndRelativeMove(int entityId, Vector3d delta, float yaw, float pitch, boolean onGround) {
        checkNotNull(delta, "delta");
        checkArgument(Math.abs(delta.getX()) < 4 && Math.abs(delta.getY()) < 4 && Math.abs(delta.getZ()) < 4, "delta must be smaller then 4");

        this.onGround = onGround;
        this.entityId = entityId;
        this.delta = delta;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Vector3d getDelta() {
        return this.delta;
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
