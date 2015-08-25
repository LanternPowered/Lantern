package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.data.EnumDifficulty;

public final class MessagePlayOutSetDifficulty implements Message {

    private final EnumDifficulty difficulty;

    /**
     * Creates a new set difficulty message.
     * 
     * @param difficulty the difficulty
     */
    public MessagePlayOutSetDifficulty(EnumDifficulty difficulty) {
        this.difficulty = checkNotNull(difficulty, "difficulty");
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
