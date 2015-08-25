package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.data.EnumGameMode;

public final class MessagePlayOutSetGameMode implements Message {

    private final EnumGameMode gameMode;

    /**
     * Creates a new set game mode message.
     * 
     * @param gameMode the game mode
     */
    public MessagePlayOutSetGameMode(EnumGameMode gameMode) {
        this.gameMode = checkNotNull(gameMode, "gameMode");
    }

    /**
     * Gets the game mode.
     * 
     * @return the game mode
     */
    public EnumGameMode getGameMode() {
        return this.gameMode;
    }

}
