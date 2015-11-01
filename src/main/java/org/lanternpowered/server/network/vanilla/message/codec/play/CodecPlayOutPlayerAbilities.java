/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerAbilities;

@Caching
public class CodecPlayOutPlayerAbilities implements Codec<MessagePlayOutPlayerAbilities> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutPlayerAbilities message) throws CodecException {
        byte bits = 0;
        // Ignore the invulnerable bit (0x1), it server side
        if (message.isFlying()) {
            bits |= 0x2;
        }
        if (message.canFly()) {
            bits |= 0x4;
        }
        // TODO: Not sure what to do with the creative bit (0x8)
        ByteBuf buf = context.byteBufAlloc().buffer();
        buf.writeByte(bits);
        buf.writeFloat(message.getFlySpeed());
        buf.writeFloat(calculateFieldOfView(message.getFieldOfView(), message.isFlying()));
        return buf;
    }

    @Override
    public MessagePlayOutPlayerAbilities decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }

    private static float calculateFieldOfView(float fov, boolean flying) {
        float x = Math.max(Math.min(fov, 1f), 0f) * 2.8f - 0.8f;
        float y = flying ? 1.1f : 1.0f; // Is this needed?
        float z = 0.1f; // movementSpeed - Just ignore this for now, it prevents sprinting.
        float w = ((y + 1f) * z) / (2 * x);
        return w;
    }
}
