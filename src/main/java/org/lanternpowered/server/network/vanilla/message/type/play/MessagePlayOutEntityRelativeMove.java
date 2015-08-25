package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import org.lanternpowered.server.network.message.Message;

import com.flowpowered.math.vector.Vector3d;

public final class MessagePlayOutEntityRelativeMove implements Message {

    private final int entityId;
    private final boolean onGround;

    private final Vector3d delta;

    public MessagePlayOutEntityRelativeMove(int entityId, Vector3d delta, boolean onGround) {
        checkNotNull(delta, "delta");
        checkArgument(Math.abs(delta.getX()) < 4 && Math.abs(delta.getY()) < 4 && Math.abs(delta.getZ()) < 4, "delta must be smaller then 4");

        this.onGround = onGround;
        this.entityId = entityId;
        this.delta = delta;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public Vector3d getDelta() {
        return this.delta;
    }

}
