package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.data.EnumDifficulty;
import org.lanternpowered.server.network.vanilla.message.data.EnumEnvironment;
import org.lanternpowered.server.network.vanilla.message.data.EnumGameMode;

public final class MessagePlayOutPlayerJoinGame implements Message {

    private final int entityId;
    private final int playerListSize;

    private final EnumGameMode gameMode;
    private final EnumDifficulty difficulty;
    private final EnumEnvironment environment;

    // Reduce info on the debug screen
    private final boolean reducedDebug;

    public MessagePlayOutPlayerJoinGame(EnumGameMode gameMode, EnumEnvironment environment, EnumDifficulty difficulty,
            int entityId, int playerListSize, boolean reducedDebug) {
        this.environment = checkNotNull(environment, "environment");
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
    public EnumGameMode getGameMode() {
        return this.gameMode;
    }

    /**
     * Gets the environment of the world this player is currently in.
     * 
     * @return the environment
     */
    public EnumEnvironment getEnvironment() {
        return this.environment;
    }

    /**
     * Gets the difficulty of the world this player is currently in.
     * 
     * @return the difficulty
     */
    public EnumDifficulty getDifficulty() {
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
