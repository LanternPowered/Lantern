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
package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.network.forge.message.handler.handshake.HandlerForgeHandshakeInAck;
import org.lanternpowered.server.network.forge.message.handler.handshake.HandlerForgeHandshakeInHello;
import org.lanternpowered.server.network.forge.message.handler.handshake.HandlerForgeHandshakeInModList;
import org.lanternpowered.server.network.forge.message.handler.handshake.HandlerForgeHandshakeInStart;
import org.lanternpowered.server.network.forge.message.processor.handshake.ProcessorForgeHandshakeOutAck;
import org.lanternpowered.server.network.forge.message.processor.handshake.ProcessorForgeHandshakeOutHello;
import org.lanternpowered.server.network.forge.message.processor.handshake.ProcessorForgeHandshakeOutModList;
import org.lanternpowered.server.network.forge.message.processor.handshake.ProcessorForgeHandshakeOutRegistryData;
import org.lanternpowered.server.network.forge.message.processor.handshake.ProcessorForgeHandshakeOutReset;
import org.lanternpowered.server.network.forge.message.processor.handshake.ProcessorPlayInChannelPayload;
import org.lanternpowered.server.network.forge.message.processor.handshake.ProcessorPlayOutChannelPayload;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutAck;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutHello;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutModList;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInStart;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutRegistryData;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutReset;
import org.lanternpowered.server.network.message.MessageRegistry;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecInOutPing;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInOutCustomPayload;
import org.lanternpowered.server.network.vanilla.message.handler.connection.HandlerInPing;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInUnregisterChannels;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutUnregisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageInOutPing;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutUnregisterChannels;

/**
 * A custom protocol instance used to handle the forge handshake messages,
 * the protocol is basically just a limited play protocol version with all
 * the message types and processors needed for the forge handshake.
 */
public final class ProtocolForgeHandshake extends ProtocolBase {

    ProtocolForgeHandshake() {
        MessageRegistry inbound = this.inbound();
        MessageRegistry outbound = this.outbound();

        inbound.register(MessagePlayInOutChannelPayload.class, new ProcessorPlayInChannelPayload());

        outbound.register(MessageForgeHandshakeInOutAck.class, new ProcessorForgeHandshakeOutAck());
        outbound.register(MessageForgeHandshakeInOutHello.class, new ProcessorForgeHandshakeOutHello());
        outbound.register(MessageForgeHandshakeInOutModList.class, new ProcessorForgeHandshakeOutModList());
        outbound.register(MessageForgeHandshakeOutRegistryData.class, new ProcessorForgeHandshakeOutRegistryData());
        outbound.register(MessageForgeHandshakeOutReset.class, new ProcessorForgeHandshakeOutReset());
        outbound.register(MessagePlayInOutUnregisterChannels.class, new ProcessorPlayOutUnregisterChannels());
        outbound.register(MessagePlayInOutRegisterChannels.class, new ProcessorPlayOutRegisterChannels());
        outbound.register(MessagePlayInOutChannelPayload.class, new ProcessorPlayOutChannelPayload());

        inbound.register(0x00, MessageInOutPing.class, CodecInOutPing.class, new HandlerInPing());
        inbound.register(0x17, MessagePlayInOutChannelPayload.class, CodecPlayInOutCustomPayload.class);
        inbound.register(MessageForgeHandshakeInOutAck.class, new HandlerForgeHandshakeInAck());
        inbound.register(MessageForgeHandshakeInOutHello.class, new HandlerForgeHandshakeInHello());
        inbound.register(MessageForgeHandshakeInOutModList.class, new HandlerForgeHandshakeInModList());
        inbound.register(MessageForgeHandshakeInStart.class, new HandlerForgeHandshakeInStart());
        inbound.register(MessagePlayInOutRegisterChannels.class, new HandlerPlayInRegisterChannels());
        inbound.register(MessagePlayInOutUnregisterChannels.class, new HandlerPlayInUnregisterChannels());

        outbound.register(0x00, MessageInOutPing.class, CodecInOutPing.class);
        outbound.register(0x3f, MessagePlayInOutChannelPayload.class, CodecPlayInOutCustomPayload.class);
        outbound.register(0x40, MessageOutDisconnect.class, CodecOutDisconnect.class);
    }
}
