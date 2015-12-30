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

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.forge.handshake.ForgeClientHandshakePhase;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutAck;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutHello;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutModList;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.processor.play.AbstractPlayInChannelPayloadProcessor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

import java.util.List;
import java.util.Map;

public final class ProcessorPlayInChannelPayload extends AbstractPlayInChannelPayloadProcessor {

    @Override
    public void process0(CodecContext context, MessagePlayInOutChannelPayload message, List<Message> output) throws CodecException {
        String channel = message.getChannel();
        ByteBuf content = message.getContent();

        if (channel.equals("FML|HS")) {
            int type = content.readByte();
            switch (type) {
                case Constants.FML_HANDSHAKE_RESET:
                    // server -> client message: ignore
                    break;
                case Constants.FML_HANDSHAKE_ACK:
                    ForgeClientHandshakePhase phase = ForgeClientHandshakePhase.values()[content.readByte()];
                    output.add(new MessageForgeHandshakeInOutAck(phase));
                    break;
                case Constants.FML_HANDSHAKE_SERVER_HELLO:
                    // server -> client message: ignore
                    break;
                case Constants.FML_HANDSHAKE_CLIENT_HELLO:
                    content.readByte(); // The forge protocol version on the client
                    output.add(new MessageForgeHandshakeInOutHello());
                    break;
                case Constants.FML_HANDSHAKE_MOD_LIST:
                    int size = context.readVarInt(content);
                    Map<String, String> entries = Maps.newHashMapWithExpectedSize(size);
                    for (int i = 0; i < size; i++) {
                        entries.put(context.read(content, String.class), context.read(content, String.class));
                    }
                    output.add(new MessageForgeHandshakeInOutModList(entries));
                    break;
                case Constants.FML_HANDSHAKE_REGISTRY_DATA:
                    // server -> client message: ignore
                    break;
                default:
                    throw new CodecException("Unknown forge handshake message with opcode: " + type);
            }
        } else {
            throw new CodecException("Received and unexpected message with channel: " + channel);
        }
    }
}
