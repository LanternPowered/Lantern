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
package org.lanternpowered.server.network.vanilla.message.codec.handshake;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.ProxyType;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.handshake.MessageHandshakeIn;

public final class CodecHandshakeIn implements Codec<MessageHandshakeIn> {

    @Override
    public MessageHandshakeIn decode(CodecContext context, ByteBuffer buf) throws CodecException {
        int protocol = buf.readVarInt();
        String hostname;
        if (Lantern.getGame().getGlobalConfig().getProxyType() != ProxyType.NONE) {
            hostname = buf.readString();
        } else {
            hostname = buf.readLimitedString(255);
        }
        short port = buf.readShort();
        int state = buf.readVarInt();
        return new MessageHandshakeIn(state, hostname, port, protocol);
    }
}
