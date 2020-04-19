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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPerformRespawn;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInRequestStatistics;

public final class CodecPlayInClientStatus implements Codec<Message> {

    @Override
    public Message decode(CodecContext context, ByteBuffer buf) throws CodecException {
        int action = buf.readVarInt();
        switch (action) {
            case 0: return new MessagePlayInPerformRespawn();
            case 1: return new MessagePlayInRequestStatistics();
            default:
                throw new CodecException("Received client status message with unknown action: " + action);
        }
    }
}
