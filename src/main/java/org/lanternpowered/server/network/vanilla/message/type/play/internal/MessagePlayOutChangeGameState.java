package org.lanternpowered.server.network.vanilla.message.type.play.internal;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetGameMode;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldSky;

/**
 * This message should not be used directly in the server implementation,
 * this is only for internal purposes used by other message types in the
 * processing. Messages like: {@link MessagePlayOutWorldSky},
 * {@link MessagePlayOutSetGameMode}, etc.
 */
public final class MessagePlayOutChangeGameState implements Message {

    private final int type;
    private final float value;

    /**
     * Creates the game state message.
     * 
     * @param type the type
     * @param value the value
     */
    public MessagePlayOutChangeGameState(int type, float value) {
        this.value = value;
        this.type = type;
    }

    /**
     * Gets the type of the message.
     * 
     * @return the type
     */
    public int getType() {
        return this.type;
    }

    /**
     * Gets the value of the message.
     * 
     * @return the value
     */
    public float getValue() {
        return this.value;
    }

    /*
     * public enum Type { INVALID_BED(0), RAINING_END(1), RAINING_START(2),
     * GAME_MODE(3), CREDITS(4), ARROW_HIT(6), RAIN_STRENGTH(7),
     * THUNDER_STRENGTH(8), MOB_APPEARANCE(10); private final int id; Type(int
     * id) { this.id = id; } /** Gets the id of the type.
     * @return the id
     *//*
        * public int getId() { return this.id; } }
        */

}
