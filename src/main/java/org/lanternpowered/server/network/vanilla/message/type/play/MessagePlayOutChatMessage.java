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

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.text.Text;

public final class MessagePlayOutChatMessage implements Message {

    private final Text message;
    private final Type type;

    public MessagePlayOutChatMessage(Text message, Type type) {
        this.message = message;
        this.type = type;
    }

    public Text getMessage() {
        return this.message;
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {
        CHAT,
        SYSTEM,
    }
}
