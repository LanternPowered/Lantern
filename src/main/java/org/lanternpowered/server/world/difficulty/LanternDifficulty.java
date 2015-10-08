package org.lanternpowered.server.world.difficulty;

import org.spongepowered.api.world.difficulty.Difficulty;

public class LanternDifficulty implements Difficulty {

    private final String name;
    private final byte internalId;

    public LanternDifficulty(String name, int internalId) {
        this.internalId = (byte) internalId;
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

    public byte getInternalId() {
        return this.internalId;
    }
}
