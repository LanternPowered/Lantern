/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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

import io.netty.handler.codec.CodecException;
import io.netty.util.AttributeKey;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerJoinGame;

public final class CodecPlayOutPlayerJoinGame implements Codec<MessagePlayOutPlayerJoinGame> {

    public final static AttributeKey<Integer> PLAYER_ENTITY_ID = AttributeKey.valueOf("player-entity-id");

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutPlayerJoinGame message) throws CodecException {
        context.getChannel().attr(PLAYER_ENTITY_ID).set(message.getEntityId());
        ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeInteger(message.getEntityId());
        byte gameMode = (byte) message.getGameMode().getInternalId();
        if (message.isHardcore()) {
            gameMode |= 0x8;
        }
        buf.writeByte(gameMode);
        buf.writeInteger(message.getDimensionType().getInternalId());
        buf.writeByte((byte) message.getDifficulty().getInternalId());
        buf.writeByte((byte) Math.min(message.getPlayerListSize(), 255));
        buf.writeString("default"); // Not used
        buf.writeBoolean(message.getReducedDebug());
        return buf;
    }
}
