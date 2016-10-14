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
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerPositionAndLook;
import org.spongepowered.api.util.RelativePositions;

import java.util.Set;

public final class CodecPlayOutPlayerPositionAndLook implements Codec<MessagePlayOutPlayerPositionAndLook> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutPlayerPositionAndLook message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        buf.writeFloat(message.getYaw());
        buf.writeFloat(message.getPitch());
        Set<RelativePositions> relativePositions = message.getRelativePositions();
        byte flags = 0;
        if (relativePositions.contains(RelativePositions.X)) {
            flags |= 0x01;
        }
        if (relativePositions.contains(RelativePositions.Y)) {
            flags |= 0x02;
        }
        if (relativePositions.contains(RelativePositions.Z)) {
            flags |= 0x04;
        }
        if (relativePositions.contains(RelativePositions.PITCH)) {
            flags |= 0x08;
        }
        if (relativePositions.contains(RelativePositions.YAW)) {
            flags |= 0x10;
        }
        buf.writeByte(flags);
        buf.writeVarInt(message.getTeleportId());
        return buf;
    }
}
