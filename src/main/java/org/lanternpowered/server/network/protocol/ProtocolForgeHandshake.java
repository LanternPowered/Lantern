package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.network.forge.message.handler.handshake.HandlerForgeHandshakeInAck;
import org.lanternpowered.server.network.forge.message.handler.handshake.HandlerForgeHandshakeInHello;
import org.lanternpowered.server.network.forge.message.handler.handshake.HandlerForgeHandshakeInModList;
import org.lanternpowered.server.network.forge.message.handler.handshake.HandlerForgeHandshakeInStart;
import org.lanternpowered.server.network.forge.message.processor.handshake.ProcessorForgeHandshakeOutAck;
import org.lanternpowered.server.network.forge.message.processor.handshake.ProcessorForgeHandshakeOutComplete;
import org.lanternpowered.server.network.forge.message.processor.handshake.ProcessorForgeHandshakeOutHello;
import org.lanternpowered.server.network.forge.message.processor.handshake.ProcessorForgeHandshakeOutModList;
import org.lanternpowered.server.network.forge.message.processor.handshake.ProcessorForgeHandshakeOutRegistryData;
import org.lanternpowered.server.network.forge.message.processor.handshake.ProcessorForgeHandshakeOutReset;
import org.lanternpowered.server.network.forge.message.processor.handshake.ProcessorPlayOutCustomPayload;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutAck;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutHello;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutModList;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInStart;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutComplete;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutRegistryData;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutReset;
import org.lanternpowered.server.network.message.MessageRegistry;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecInOutPing;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInOutCustomPayload;
import org.lanternpowered.server.network.vanilla.message.handler.connection.HandlerInPing;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInChannelPayload;
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
        outbound.register(MessageForgeHandshakeOutComplete.class, new ProcessorForgeHandshakeOutComplete());
        outbound.register(MessagePlayInOutUnregisterChannels.class, new ProcessorPlayOutUnregisterChannels());
        outbound.register(MessagePlayInOutRegisterChannels.class, new ProcessorPlayOutRegisterChannels());
        outbound.register(MessagePlayInOutChannelPayload.class, new ProcessorPlayOutCustomPayload());

        inbound.register(0x00, MessageInOutPing.class, CodecInOutPing.class, new HandlerInPing());
        inbound.register(0x17, MessagePlayInOutChannelPayload.class, CodecPlayInOutCustomPayload.class);
        inbound.register(MessageForgeHandshakeInOutAck.class, new HandlerForgeHandshakeInAck());
        inbound.register(MessageForgeHandshakeInOutHello.class, new HandlerForgeHandshakeInHello());
        inbound.register(MessageForgeHandshakeInOutModList.class, new HandlerForgeHandshakeInModList());
        inbound.register(MessageForgeHandshakeInStart.class, new HandlerForgeHandshakeInStart());

        outbound.register(0x00, MessageInOutPing.class, CodecInOutPing.class);
        outbound.register(0x3f, MessagePlayInOutChannelPayload.class, CodecPlayInOutCustomPayload.class);
        outbound.register(0x40, MessageOutDisconnect.class, CodecOutDisconnect.class);
    }
}
