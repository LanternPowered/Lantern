package org.lanternpowered.server.network.forge.message.processor;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import java.util.List;
import java.util.Map;

import org.lanternpowered.server.network.forge.message.handshake.ClientHandshakePhase;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInOutAck;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInOutHello;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInOutModList;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.processor.play.AbstractPlayInChannelPayloadProcessor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

import com.google.common.collect.Maps;

public final class ProcessorPlayInCustomPayload extends AbstractPlayInChannelPayloadProcessor {

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
                    ClientHandshakePhase phase = ClientHandshakePhase.values()[content.readByte()];
                    output.add(new MessageHandshakeInOutAck(phase));
                    break;
                case Constants.FML_HANDSHAKE_SERVER_HELLO:
                    // server -> client message: ignore
                    break;
                case Constants.FML_HANDSHAKE_CLIENT_HELLO:
                    content.readByte(); // The forge protocol version on the client
                    output.add(new MessageHandshakeInOutHello());
                    break;
                case Constants.FML_HANDSHAKE_MOD_LIST:
                    int size = context.readVarInt(content);
                    Map<String, String> entries = Maps.newHashMapWithExpectedSize(size);
                    for (int i = 0; i < size; i++) {
                        entries.put(context.read(content, String.class), context.read(content, String.class));
                    }
                    output.add(new MessageHandshakeInOutModList(entries));
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