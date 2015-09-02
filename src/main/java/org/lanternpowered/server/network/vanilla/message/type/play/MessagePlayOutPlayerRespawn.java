package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.lanternpowered.server.world.dimension.LanternDimensionType;

public final class MessagePlayOutPlayerRespawn implements Message {

    private final LanternGameMode gameMode;
    private final LanternDifficulty difficulty;
    private final LanternDimensionType dimensionType;

    public MessagePlayOutPlayerRespawn(LanternGameMode gameMode, LanternDimensionType dimensionType,
            LanternDifficulty difficulty) {
        this.dimensionType = checkNotNull(dimensionType, "dimensionType");
        this.difficulty = checkNotNull(difficulty, "difficulty");
        this.gameMode = checkNotNull(gameMode, "game mode");
    }

    /**
     * Gets the game mode of the player.
     * 
     * @return the game mode
     */
    public LanternGameMode getGameMode() {
        return this.gameMode;
    }

    /**
     * Gets the dimension type of the world this player is currently in.
     * 
     * @return the dimension type
     */
    public LanternDimensionType getDimensionType() {
        return this.dimensionType;
    }

    /**
     * Gets the difficulty of the world this player is currently in.
     * 
     * @return the difficulty
     */
    public LanternDifficulty getDifficulty() {
        return this.difficulty;
    }
}
