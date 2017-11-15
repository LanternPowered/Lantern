/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardObjective;
import org.lanternpowered.server.scoreboard.LanternObjectiveDisplayMode;

public final class CodecPlayOutScoreboardObjective implements Codec<MessagePlayOutScoreboardObjective> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutScoreboardObjective message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeString(message.getObjectiveName());
        if (message instanceof MessagePlayOutScoreboardObjective.CreateOrUpdate) {
            buf.writeByte((byte) (message instanceof MessagePlayOutScoreboardObjective.Create ? 0 : 2));
            final MessagePlayOutScoreboardObjective.CreateOrUpdate message0 =
                    (MessagePlayOutScoreboardObjective.CreateOrUpdate) message;
            context.write(buf, ContextualValueTypes.TEXT, message0.getDisplayName());
            buf.writeVarInt(((LanternObjectiveDisplayMode) message0.getDisplayMode()).getInternalId());
        } else {
            buf.writeByte((byte) 1);
        }
        return buf;
    }
}
