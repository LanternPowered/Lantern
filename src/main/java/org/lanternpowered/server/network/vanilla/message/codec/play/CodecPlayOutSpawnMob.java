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

import com.flowpowered.math.vector.Vector3d;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnMob;

public final class CodecPlayOutSpawnMob implements Codec<MessagePlayOutSpawnMob> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutSpawnMob message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(message.getEntityId());
        buf.writeUniqueId(message.getUniqueId());
        buf.writeVarInt(message.getMobType());
        buf.writeVector3d(message.getPosition());
        buf.writeByte(message.getYaw());
        buf.writeByte(message.getPitch());
        buf.writeByte(message.getHeadPitch());
        final Vector3d velocity = message.getVelocity();
        buf.writeShort((short) Math.min(velocity.getX() * 8000.0, Short.MAX_VALUE));
        buf.writeShort((short) Math.min(velocity.getY() * 8000.0, Short.MAX_VALUE));
        buf.writeShort((short) Math.min(velocity.getZ() * 8000.0, Short.MAX_VALUE));
        context.write(buf, ContextualValueTypes.PARAMETER_LIST, message.getParameterList());
        return buf;
    }
}
