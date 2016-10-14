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
package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.network.forge.message.codec.handshake.CodecPlayInOutCustomPayload;
import org.lanternpowered.server.network.forge.message.codec.handshake.ProcessorForgeHandshakeOutRegistryData;
import org.lanternpowered.server.network.forge.message.handler.handshake.HandlerForgeHandshakeInAck;
import org.lanternpowered.server.network.forge.message.handler.handshake.HandlerForgeHandshakeInHello;
import org.lanternpowered.server.network.forge.message.handler.handshake.HandlerForgeHandshakeInModList;
import org.lanternpowered.server.network.forge.message.handler.handshake.HandlerForgeHandshakeInStart;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutAck;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutHello;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutModList;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInStart;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutRegistryData;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutReset;
import org.lanternpowered.server.network.message.CodecRegistration;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.MessageRegistry;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecInOutPing;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInChannelPayload;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInUnregisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageInOutKeepAlive;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutUnregisterChannels;

/**
 * A custom protocol instance used to handle the forge handshake messages,
 * the protocol is basically just a limited play protocol version with all
 * the message types and processors needed for the forge handshake.
 */
final class ProtocolForgeHandshake extends ProtocolBase {

    ProtocolForgeHandshake() {
        final MessageRegistry inbound = this.inbound();
        final MessageRegistry outbound = this.outbound();

        inbound.bind(0x0b, CodecInOutPing.class, MessageInOutKeepAlive.class);
        final CodecRegistration<Message, CodecPlayInOutCustomPayload> codecPlayInCustomPayloadRegistration =
                inbound.bind(0x09, CodecPlayInOutCustomPayload.class);
        codecPlayInCustomPayloadRegistration.bind(MessagePlayInOutChannelPayload.class).bindHandler(new HandlerPlayInChannelPayload());
        codecPlayInCustomPayloadRegistration.bind(MessageForgeHandshakeInOutAck.class).bindHandler(new HandlerForgeHandshakeInAck());
        codecPlayInCustomPayloadRegistration.bind(MessageForgeHandshakeInOutHello.class).bindHandler(new HandlerForgeHandshakeInHello());
        codecPlayInCustomPayloadRegistration.bind(MessageForgeHandshakeInOutModList.class).bindHandler(new HandlerForgeHandshakeInModList());
        codecPlayInCustomPayloadRegistration.bind(MessageForgeHandshakeInStart.class).bindHandler(new HandlerForgeHandshakeInStart());
        codecPlayInCustomPayloadRegistration.bind(MessagePlayInOutRegisterChannels.class).bindHandler(new HandlerPlayInRegisterChannels());
        codecPlayInCustomPayloadRegistration.bind(MessagePlayInOutUnregisterChannels.class).bindHandler(new HandlerPlayInUnregisterChannels());

        outbound.bind(0x1f, CodecInOutPing.class, MessageInOutKeepAlive.class);
        final CodecRegistration<Message, CodecPlayInOutCustomPayload> codecPlayOutCustomPayloadRegistration =
                outbound.bind(0x18, CodecPlayInOutCustomPayload.class);
        codecPlayOutCustomPayloadRegistration.bind(MessagePlayInOutChannelPayload.class);
        codecPlayOutCustomPayloadRegistration.bind(MessageForgeHandshakeInOutAck.class);
        codecPlayOutCustomPayloadRegistration.bind(MessageForgeHandshakeInOutHello.class);
        codecPlayOutCustomPayloadRegistration.bind(MessageForgeHandshakeInOutModList.class);
        codecPlayOutCustomPayloadRegistration.bind(MessageForgeHandshakeOutReset.class);
        codecPlayOutCustomPayloadRegistration.bind(MessagePlayInOutRegisterChannels.class);
        codecPlayOutCustomPayloadRegistration.bind(MessagePlayInOutUnregisterChannels.class);
        outbound.bind(0x1a, CodecOutDisconnect.class, MessageOutDisconnect.class);
        outbound.bindProcessor(MessageForgeHandshakeOutRegistryData.class, new ProcessorForgeHandshakeOutRegistryData());
    }
}
