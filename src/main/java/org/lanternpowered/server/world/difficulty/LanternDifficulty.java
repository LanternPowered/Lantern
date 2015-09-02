package org.lanternpowered.server.world.difficulty;

import org.spongepowered.api.world.difficulty.Difficulty;

public class LanternDifficulty implements Difficulty {

    private final String name;
    private final int internalId;

    public LanternDifficulty(String name, int internalId) {
        this.internalId = internalId;
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

    public int getInternalId() {
        return this.internalId;
    }
}
