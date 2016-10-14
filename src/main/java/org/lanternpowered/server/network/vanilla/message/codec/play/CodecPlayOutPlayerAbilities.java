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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerAbilities;

public class CodecPlayOutPlayerAbilities implements Codec<MessagePlayOutPlayerAbilities> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutPlayerAbilities message) throws CodecException {
        byte bits = 0;
        if (message.isInvulnerable()) {
            bits |= 0x1;
        }
        if (message.isFlying()) {
            bits |= 0x2;
        }
        if (message.canFly()) {
            bits |= 0x4;
        }
        if (message.isCreative()) {
            bits |= 0x8;
        }
        ByteBuffer buf = context.byteBufAlloc().buffer(9);
        buf.writeByte(bits);
        buf.writeFloat(message.getFlySpeed());
        buf.writeFloat(message.getFieldOfView());
        return buf;
    }
}
