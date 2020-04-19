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
package org.lanternpowered.server.network.vanilla.message.codec.status;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusInOutPing;

public final class CodecStatusInOutPing implements Codec<MessageStatusInOutPing> {

    @Override
    public ByteBuffer encode(CodecContext context, MessageStatusInOutPing message) throws CodecException {
        return context.byteBufAlloc().buffer(8).writeLong(message.getTime());
    }

    @Override
    public MessageStatusInOutPing decode(CodecContext context, ByteBuffer buf) throws CodecException {
        return new MessageStatusInOutPing(buf.readLong());
    }
}
