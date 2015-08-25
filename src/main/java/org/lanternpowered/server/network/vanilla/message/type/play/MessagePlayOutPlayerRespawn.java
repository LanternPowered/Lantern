package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.data.EnumDifficulty;
import org.lanternpowered.server.network.vanilla.message.data.EnumEnvironment;
import org.lanternpowered.server.network.vanilla.message.data.EnumGameMode;

public final class MessagePlayOutPlayerRespawn implements Message {

    private final EnumGameMode gameMode;
    private final EnumDifficulty difficulty;
    private final EnumEnvironment environment;

    public MessagePlayOutPlayerRespawn(EnumGameMode gameMode, EnumEnvironment environment, EnumDifficulty difficulty) {
        this.environment = checkNotNull(environment, "environment");
        this.difficulty = checkNotNull(difficulty, "difficulty");
        this.gameMode = checkNotNull(gameMode, "game mode");
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
     * Gets the environment type of the world this player is currently in.
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

}
