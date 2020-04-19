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
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInChannelResponse;

public final class CodecLoginInChannelResponse implements Codec<MessageLoginInChannelResponse> {

    @Override
    public MessageLoginInChannelResponse decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final int transactionId = buf.readVarInt();
        final ByteBuffer content;
        if (buf.readBoolean()) { // Whether content is following
            content = buf.slice(); // Slice content for performance over copying
            content.retain(); // Retain the buffer until released
        } else {
            // Just a empty buffer
            content = context.byteBufAlloc().buffer(0);
        }
        return new MessageLoginInChannelResponse(transactionId, content);
    }
}
