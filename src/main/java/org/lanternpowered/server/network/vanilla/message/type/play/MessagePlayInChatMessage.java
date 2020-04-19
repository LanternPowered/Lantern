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
package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayInChatMessage implements Message {

    private final String message;

    public MessagePlayInChatMessage(String message) {
        this.message = checkNotNull(message, "message");
    }

    /**
     * Gets the message received from the client.
     * 
     * @return The message
     */
    public String getMessage() {
        return this.message;
    }

}
