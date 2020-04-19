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
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.data.type.LanternMusicDisc;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutRecord;

public final class CodecPlayOutEffect implements Codec<Message> {

    @Override
    public ByteBuffer encode(CodecContext context, Message message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        if (message instanceof MessagePlayOutEffect) {
            final MessagePlayOutEffect message1 = (MessagePlayOutEffect) message;
            buf.writeInt(message1.getType());
            buf.writePosition(message1.getPosition());
            buf.writeInt(message1.getData());
            buf.writeBoolean(message1.isBroadcast());
        } else if (message instanceof MessagePlayOutRecord) {
            final MessagePlayOutRecord message1 = (MessagePlayOutRecord) message;
            buf.writeInt(1010);
            buf.writePosition(message1.getPosition());
            buf.writeInt(message1.getMusicDisc()
                    .map(type -> 2256 + ((LanternMusicDisc) type).getInternalId()).orElse(0));
            buf.writeBoolean(false);
        } else {
            throw new EncoderException("Unsupported message type: " + message.getClass().getName());
        }
        return buf;
    }
}
