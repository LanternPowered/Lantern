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
package org.lanternpowered.server.network.vanilla.packet.type.play;

import org.lanternpowered.server.network.message.Packet;
import org.spongepowered.api.text.Text;

public final class MessagePlayOutChatPacket implements Packet {

    private final Text message;
    private final Type type;

    public MessagePlayOutChatPacket(Text message, Type type) {
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
