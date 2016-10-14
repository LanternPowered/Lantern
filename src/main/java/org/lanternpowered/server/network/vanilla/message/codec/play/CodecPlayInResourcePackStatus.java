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
import io.netty.handler.codec.DecoderException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInResourcePackStatus;
import org.spongepowered.api.event.entity.living.humanoid.player.ResourcePackStatusEvent.ResourcePackStatus;

public final class CodecPlayInResourcePackStatus implements Codec<MessagePlayInResourcePackStatus> {

    private final Int2ObjectMap<ResourcePackStatus> status = new Int2ObjectOpenHashMap<>();

    {
        this.status.put(0, ResourcePackStatus.SUCCESSFULLY_LOADED);
        this.status.put(1, ResourcePackStatus.DECLINED);
        this.status.put(2, ResourcePackStatus.FAILED);
        this.status.put(3, ResourcePackStatus.ACCEPTED);
    }

    @Override
    public MessagePlayInResourcePackStatus decode(CodecContext context, ByteBuffer buf) throws CodecException {
        int status0 = buf.readVarInt();
        ResourcePackStatus status = this.status.get(status0);
        if (status == null) {
            throw new DecoderException("Unknown status: " + status0);
        }
        return new MessagePlayInResourcePackStatus(status);
    }
}
