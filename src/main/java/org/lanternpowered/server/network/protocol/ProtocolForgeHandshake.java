package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInOutAck;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInOutHello;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInOutModList;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInStart;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeOutComplete;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeOutRegistryData;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeOutReset;
import org.lanternpowered.server.network.forge.message.handshake.handler.HandlerHandshakeInAck;
import org.lanternpowered.server.network.forge.message.handshake.handler.HandlerHandshakeInHello;
import org.lanternpowered.server.network.forge.message.handshake.handler.HandlerHandshakeInModList;
import org.lanternpowered.server.network.forge.message.handshake.handler.HandlerHandshakeInStart;
import org.lanternpowered.server.network.forge.message.processor.ProcessorHandshakeOutAck;
import org.lanternpowered.server.network.forge.message.processor.ProcessorHandshakeOutComplete;
import org.lanternpowered.server.network.forge.message.processor.ProcessorHandshakeOutHello;
import org.lanternpowered.server.network.forge.message.processor.ProcessorHandshakeOutModList;
import org.lanternpowered.server.network.forge.message.processor.ProcessorHandshakeOutRegistryData;
import org.lanternpowered.server.network.forge.message.processor.ProcessorHandshakeOutReset;
import org.lanternpowered.server.network.forge.message.processor.ProcessorPlayOutCustomPayload;
import org.lanternpowered.server.network.message.MessageRegistry;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecInOutPing;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInOutCustomPayload;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInChannelPayload;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutUnregisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageInOutPing;
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

        outbound.register(MessageHandshakeInOutAck.class, new ProcessorHandshakeOutAck());
        outbound.register(MessageHandshakeInOutHello.class, new ProcessorHandshakeOutHello());
        outbound.register(MessageHandshakeInOutModList.class, new ProcessorHandshakeOutModList());
        outbound.register(MessageHandshakeOutRegistryData.class, new ProcessorHandshakeOutRegistryData());
        outbound.register(MessageHandshakeOutReset.class, new ProcessorHandshakeOutReset());
        outbound.register(MessageHandshakeOutComplete.class, new ProcessorHandshakeOutComplete());
        outbound.register(MessagePlayInOutUnregisterChannels.class, new ProcessorPlayOutUnregisterChannels());
        outbound.register(MessagePlayInOutRegisterChannels.class, new ProcessorPlayOutRegisterChannels());
        outbound.register(MessagePlayInOutChannelPayload.class, new ProcessorPlayOutCustomPayload());

        inbound.register(0x17, MessagePlayInOutChannelPayload.class, CodecPlayInOutCustomPayload.class);
        inbound.register(MessageHandshakeInOutAck.class, new HandlerHandshakeInAck());
        inbound.register(MessageHandshakeInOutHello.class, new HandlerHandshakeInHello());
        inbound.register(MessageHandshakeInOutModList.class, new HandlerHandshakeInModList());
        inbound.register(MessageHandshakeInStart.class, new HandlerHandshakeInStart());

        outbound.register(0x00, MessageInOutPing.class, CodecInOutPing.class);
        outbound.register(0x3f, MessagePlayInOutChannelPayload.class, CodecPlayInOutCustomPayload.class);
    }
}
