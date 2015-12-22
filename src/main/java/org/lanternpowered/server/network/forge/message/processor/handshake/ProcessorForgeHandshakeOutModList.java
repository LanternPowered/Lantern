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
package org.lanternpowered.server.network.forge.message.processor.handshake;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutModList;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

public final class ProcessorForgeHandshakeOutModList implements Processor<MessageForgeHandshakeInOutModList> {

    @Override
    public void process(CodecContext context, MessageForgeHandshakeInOutModList message, List<Message> output) throws CodecException {
        Map<String, String> entries = message.getEntries();
        ByteBuf buf = context.byteBufAlloc().buffer();
        buf.writeByte(Constants.FML_HANDSHAKE_MOD_LIST);
        context.writeVarInt(buf, entries.size());
        for (Entry<String, String> en : entries.entrySet()) {
            context.write(buf, String.class, en.getKey());
            context.write(buf, String.class, en.getValue());
        }
        output.add(new MessagePlayInOutChannelPayload("FML|HS", buf));
    }
}
