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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldTime;
import org.lanternpowered.server.world.TimeUniverse;

public final class CodecPlayOutWorldTime implements Codec<MessagePlayOutWorldTime> {

    private final static int LENGTH = Long.BYTES * 2;

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutWorldTime message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer(LENGTH);

        // The time also uses a negative tag
        long time = message.getTime();
        while (time < 0) {
            time += TimeUniverse.TICKS_IN_A_DAY;
        }
        time %= TimeUniverse.TICKS_IN_A_DAY;
        time += message.getMoonPhase().ordinal() * TimeUniverse.TICKS_IN_A_DAY;
        if (!message.getEnabled()) {
            time = time == 0 ? -1 : -time;
        }

        buf.writeLong(message.getAge());
        buf.writeLong(time);

        return buf;
    }
}
