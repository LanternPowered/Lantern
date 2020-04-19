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
package org.lanternpowered.server.network.vanilla.message.codec.login;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutChannelRequest;

public final class CodecLoginOutChannelRequest implements Codec<MessageLoginOutChannelRequest> {

    @Override
    public ByteBuffer encode(CodecContext context, MessageLoginOutChannelRequest message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(message.getTransactionId());
        buf.writeString(message.getChannel());
        buf.writeBytes(message.getContent());
        return buf;
    }
}
