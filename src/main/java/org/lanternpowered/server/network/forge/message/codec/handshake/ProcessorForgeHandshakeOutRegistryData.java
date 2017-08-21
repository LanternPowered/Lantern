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
package org.lanternpowered.server.network.forge.message.codec.handshake;

import static org.lanternpowered.server.network.forge.ForgeProtocol.HANDSHAKE_CHANNEL;

import io.netty.handler.codec.CodecException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutRegistryData;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutRegistryData.Entry;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ProcessorForgeHandshakeOutRegistryData implements Processor<MessageForgeHandshakeOutRegistryData> {

    @Override
    public void process(CodecContext context, MessageForgeHandshakeOutRegistryData message, List<Message> output) throws CodecException {
        final Iterator<Entry> it = message.getEntries().iterator();
        if (!it.hasNext()) {
            throw new CodecException("There must be at least one entry present!");
        }
        while (it.hasNext()) {
            final Entry entry = it.next();
            final ByteBuffer buf = context.byteBufAlloc().buffer();
            buf.writeByte((byte) CodecPlayInOutCustomPayload.FML_HANDSHAKE_REGISTRY_DATA);
            buf.writeBoolean(it.hasNext());
            buf.writeString(entry.getName());
            final Object2IntMap<String> ids = entry.getIds();
            buf.writeVarInt(ids.size());
            for (Object2IntMap.Entry<String> en : ids.object2IntEntrySet()) {
                buf.writeString(en.getKey());
                buf.writeVarInt(en.getIntValue());
            }
            final Set<String> dummies = entry.getDummies();
            buf.writeVarInt(dummies.size());
            dummies.forEach(buf::writeString);
            final Map<String, String> overrides = entry.getOverrides();
            buf.writeVarInt(overrides.size());
            for (Map.Entry<String, String> en : overrides.entrySet()) {
                buf.writeString(en.getKey());
                buf.writeString(en.getValue());
            }
            output.add(new MessagePlayInOutChannelPayload(HANDSHAKE_CHANNEL, buf));
        }
    }
}
