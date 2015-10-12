package org.lanternpowered.server.world.difficulty;

import org.lanternpowered.server.catalog.SimpleLanternCatalogType;
import org.spongepowered.api.world.difficulty.Difficulty;

public final class LanternDifficulty extends SimpleLanternCatalogType implements Difficulty {

    private final byte internalId;

    public LanternDifficulty(String identifier, int internalId) {
        super(identifier);
        this.internalId = (byte) internalId;
    }

    public byte getInternalId() {
        return this.internalId;
    }
}
