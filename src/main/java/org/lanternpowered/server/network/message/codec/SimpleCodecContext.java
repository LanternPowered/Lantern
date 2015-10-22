/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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
package org.lanternpowered.server.network.message.codec;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;

import org.lanternpowered.server.network.message.codec.object.serializer.AbstractObjectSerializerContext;
import org.lanternpowered.server.network.message.codec.object.serializer.ObjectSerializers;
import org.lanternpowered.server.network.session.Session;

public class SimpleCodecContext extends AbstractObjectSerializerContext implements CodecContext {

    private final Channel channel;
    private final Session session;

    public SimpleCodecContext(ObjectSerializers objectSerializers, Channel channel, Session session) {
        super(objectSerializers);

        this.channel = channel;
        this.session = session;
    }

    @Override
    public ByteBufAllocator byteBufAlloc() {
        return this.channel.alloc();
    }

    @Override
    public Channel channel() {
        return this.channel;
    }

    @Override
    public Session session() {
        return this.session;
    }
}
