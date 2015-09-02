package org.lanternpowered.server.effect.particle;

import org.spongepowered.api.effect.particle.ParticleType;

public class LanternParticleType implements ParticleType {

    private final String name;

    private final boolean hasMotion;
    private final int internalId;

    public LanternParticleType(int internalId, String name, boolean hasMotion) {
        this.internalId = internalId;
        this.hasMotion = hasMotion;
        this.name = name;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean hasMotion() {
        return this.hasMotion;
    }

    public int getInternalId() {
        return this.internalId;
    }
}
