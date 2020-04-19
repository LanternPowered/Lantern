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
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChatMessage;

public final class CodecPlayOutChatMessage implements Codec<MessagePlayOutChatMessage> {

    @SuppressWarnings("deprecation")
    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutChatMessage message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        context.write(buf, ContextualValueTypes.TEXT, message.getMessage());
        buf.writeByte((byte) message.getType().ordinal());
        return buf;
    }
}
