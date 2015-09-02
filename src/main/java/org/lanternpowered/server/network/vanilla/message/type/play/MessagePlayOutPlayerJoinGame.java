package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.lanternpowered.server.world.dimension.LanternDimensionType;

public final class MessagePlayOutPlayerJoinGame implements Message {

    private final int entityId;
    private final int playerListSize;

    private final LanternGameMode gameMode;
    private final LanternDifficulty difficulty;
    private final LanternDimensionType dimensionType;

    // Reduce info on the debug screen
    private final boolean reducedDebug;

    public MessagePlayOutPlayerJoinGame(LanternGameMode gameMode, LanternDimensionType dimensionType,
            LanternDifficulty difficulty, int entityId, int playerListSize, boolean reducedDebug) {
        this.dimensionType = checkNotNull(dimensionType, "dimensionType");
        this.difficulty = checkNotNull(difficulty, "difficulty");
        this.gameMode = checkNotNull(gameMode, "gameMode");
        this.playerListSize = playerListSize;
        this.reducedDebug = reducedDebug;
        this.entityId = entityId;
    }

    /**
     * Gets the entity id of the player.
     * 
     * @return the entity id
     */
    public int getEntityId() {
        return this.entityId;
    }

    /**
     * Gets the size of the player list.
     * 
     * @return the list size
     */
    public int getPlayerListSize() {
        return this.playerListSize;
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

    /**
     * Gets whether reduced debug should be used, no idea what this will do,
     * maybe less information in the f3 screen?
     * 
     * @return reduced debug
     */
    public boolean getReducedDebug() {
        return this.reducedDebug;
    }

}
