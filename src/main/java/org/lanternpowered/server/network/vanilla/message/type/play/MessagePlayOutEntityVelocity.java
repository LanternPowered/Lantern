package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

import com.flowpowered.math.vector.Vector3d;

public final class MessagePlayOutEntityVelocity implements Message {

    private final int entityId;
    private final Vector3d velocity;

    public MessagePlayOutEntityVelocity(int entityId, Vector3d velocity) {
        this.velocity = checkNotNull(velocity, "velocity");
        this.entityId = entityId;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Vector3d getVelocity() {
        return this.velocity;
    }

}
