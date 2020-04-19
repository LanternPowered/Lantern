/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
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
}
