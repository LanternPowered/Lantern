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
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutHeldItemChange;

/**
 * Note that incoming and outgoing codecs are not the same, but we use the same
 * message class.
 */
public final class CodecPlayInOutHeldItemChange implements Codec<MessagePlayInOutHeldItemChange> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayInOutHeldItemChange message) throws CodecException {
        return context.byteBufAlloc().buffer(1).writeByte((byte) message.getSlot());
    }

    @Override
    public MessagePlayInOutHeldItemChange decode(CodecContext context, ByteBuffer buf) throws CodecException {
        return new MessagePlayInOutHeldItemChange((byte) buf.readShort());
    }
}
