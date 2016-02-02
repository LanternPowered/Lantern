/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetOpLevel;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetReducedDebug;

public final class CodecPlayOutEntityStatus implements Codec<Message> {

    @Override
    public ByteBuf encode(CodecContext context, Message message) throws CodecException {
        int entityId;
        int action;
        if (message instanceof MessagePlayOutSetReducedDebug) {
            entityId = context.getChannel().attr(CodecPlayOutPlayerJoinGame.PLAYER_ENTITY_ID).get();
            action = ((MessagePlayOutSetReducedDebug) message).isReduced() ? 22 : 23;
        } else if (message instanceof MessagePlayOutSetOpLevel) {
            entityId = context.getChannel().attr(CodecPlayOutPlayerJoinGame.PLAYER_ENTITY_ID).get();
            action = 24 + Math.max(0, Math.min(4, ((MessagePlayOutSetOpLevel) message).getOpLevel()));
        } else {
            throw new CodecException("Unsupported message type: " + message.getClass().getName());
        }
        return context.byteBufAlloc().buffer(9).writeInt(entityId).writeByte(action);
    }
}
