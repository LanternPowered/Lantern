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

import com.google.common.collect.Maps;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.forge.handshake.ForgeClientHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutAck;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutHello;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutModList;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutReset;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.codec.play.AbstractCodecPlayInOutCustomPayload;

import java.util.Map;

public final class CodecPlayInOutCustomPayload extends AbstractCodecPlayInOutCustomPayload {

    private static final int FML_HANDSHAKE_SERVER_HELLO = 0;
    private static final int FML_HANDSHAKE_CLIENT_HELLO = 1;
    private static final int FML_HANDSHAKE_MOD_LIST = 2;
    static final int FML_HANDSHAKE_REGISTRY_DATA = 3;
    private static final int FML_HANDSHAKE_ACK = -1;
    private static final int FML_HANDSHAKE_RESET = -2;

    // The protocol version, the dimension field is only send when protocol >= 2
    private final static int FORGE_PROTOCOL = 2;
    // Don't override the dimension, a 0 is send in this case
    private final static int FORGE_OVERRIDDEN_DIMENSION = 0;

    @Override
    protected MessageResult encode0(CodecContext context, Message message) throws CodecException {
        if (message instanceof MessageForgeHandshakeInOutAck) {
            return new MessageResult(HANDSHAKE_CHANNEL, context.byteBufAlloc()
                    .buffer(2)
                    .writeByte((byte) FML_HANDSHAKE_ACK)
                    // Only the server state should be send to the client
                    .writeByte((byte) ((ForgeServerHandshakePhase) ((MessageForgeHandshakeInOutAck) message).getPhase()).ordinal()));
        } else if (message instanceof MessageForgeHandshakeInOutHello) {
            return new MessageResult(HANDSHAKE_CHANNEL, context.byteBufAlloc()
                    .buffer(3)
                    .writeByte((byte) FML_HANDSHAKE_SERVER_HELLO)
                    .writeByte((byte) FORGE_PROTOCOL)
                    .writeInteger(FORGE_OVERRIDDEN_DIMENSION));
        } else if (message instanceof MessageForgeHandshakeInOutModList) {
            final Map<String, String> entries = ((MessageForgeHandshakeInOutModList) message).getEntries();
            final ByteBuffer buf = context.byteBufAlloc().buffer();
            buf.writeByte((byte) FML_HANDSHAKE_MOD_LIST);
            buf.writeVarInt(entries.size());
            for (Map.Entry<String, String> en : entries.entrySet()) {
                buf.writeString(en.getKey());
                buf.writeString(en.getValue());
            }
            return new MessageResult(HANDSHAKE_CHANNEL, buf);
        } else if (message instanceof MessageForgeHandshakeOutReset) {
            return new MessageResult(HANDSHAKE_CHANNEL, context.byteBufAlloc()
                    .buffer(1).writeByte((byte) FML_HANDSHAKE_RESET));
        }
        throw new EncoderException("Unsupported message type: " + message);
    }

    @Override
    protected Message decode0(CodecContext context, String channel, ByteBuffer content) throws CodecException {
        if (HANDSHAKE_CHANNEL.equals(channel)) {
            int type = content.readByte();
            switch (type) {
                case FML_HANDSHAKE_RESET:
                    // server -> client message: ignore
                    break;
                case FML_HANDSHAKE_ACK:
                    final ForgeClientHandshakePhase phase = ForgeClientHandshakePhase.values()[content.readByte()];
                    return new MessageForgeHandshakeInOutAck(phase);
                case FML_HANDSHAKE_SERVER_HELLO:
                    // server -> client message: ignore
                    break;
                case FML_HANDSHAKE_CLIENT_HELLO:
                    content.readByte(); // The forge protocol version on the client
                    return new MessageForgeHandshakeInOutHello();
                case FML_HANDSHAKE_MOD_LIST:
                    final int size = content.readVarInt();
                    final Map<String, String> entries = Maps.newHashMapWithExpectedSize(size);
                    for (int i = 0; i < size; i++) {
                        entries.put(content.readString(), content.readString());
                    }
                    return new MessageForgeHandshakeInOutModList(entries);
                case FML_HANDSHAKE_REGISTRY_DATA:
                    // server -> client message: ignore
                    break;
                default:
                    throw new DecoderException("Unknown forge handshake message with opcode: " + type);
            }
            throw new DecoderException("Received an unexpected forge message with opcode: " + type);
        } else {
            throw new DecoderException("Received an unexpected message with channel: " + channel);
        }
    }

}
