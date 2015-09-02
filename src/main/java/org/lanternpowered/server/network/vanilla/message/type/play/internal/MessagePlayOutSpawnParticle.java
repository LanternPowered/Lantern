package org.lanternpowered.server.network.vanilla.message.type.play.internal;

import org.lanternpowered.server.network.message.Message;

import com.flowpowered.math.vector.Vector3f;

public final class MessagePlayOutSpawnParticle implements Message {

    private final int particleId;

    private final Vector3f position;
    private final Vector3f offset;

    private final float data;
    private final int count;
    private final int[] extra;

    public MessagePlayOutSpawnParticle(int particleId, Vector3f position, Vector3f offset, float data,
            int count, int[] extra) {
        this.particleId = particleId;
        this.position = position;
        this.offset = offset;
        this.count = count;
        this.extra = extra;
        this.data = data;
    }

    public int getParticleId() {
        return this.particleId;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public Vector3f getOffset() {
        return this.offset;
    }

    public float getData() {
        return this.data;
    }

    public int getCount() {
        return this.count;
    }

    public int[] getExtra() {
        return this.extra;
    }
}
