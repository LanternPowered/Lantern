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

import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayOutSpawnParticle;

import com.flowpowered.math.vector.Vector3f;

@Caching
public final class CodecPlayOutSpawnParticle implements Codec<MessagePlayOutSpawnParticle> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutSpawnParticle message) throws CodecException {
        Vector3f position = message.getPosition();
        Vector3f offset = message.getOffset();
        int[] extra = message.getExtra();
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.writeVarInt(buf, message.getParticleId());
        buf.writeBoolean(true);
        buf.writeFloat(position.getX());
        buf.writeFloat(position.getY());
        buf.writeFloat(position.getZ());
        buf.writeFloat(offset.getX());
        buf.writeFloat(offset.getY());
        buf.writeFloat(offset.getZ());
        buf.writeFloat(message.getData());
        buf.writeInt(message.getCount());
        for (int i = 0; i < extra.length; i++) {
            context.writeVarInt(buf, extra[i]);
        }
        return buf;
    }

    @Override
    public MessagePlayOutSpawnParticle decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
