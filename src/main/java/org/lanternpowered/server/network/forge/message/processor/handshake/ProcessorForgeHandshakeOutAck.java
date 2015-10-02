package org.lanternpowered.server.network.forge.message.processor.handshake;

import io.netty.handler.codec.CodecException;

import java.util.List;

import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutAck;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

public final class ProcessorForgeHandshakeOutAck implements Processor<MessageForgeHandshakeInOutAck> {

    @Override
    public void process(CodecContext context, MessageForgeHandshakeInOutAck message, List<Message> output) throws CodecException {
        output.add(new MessagePlayInOutChannelPayload("FML|HS", context.byteBufAlloc()
                .buffer(2)
                .writeByte(Constants.FML_HANDSHAKE_ACK)
                // Only the server state should be send to the client
                .writeByte(((ForgeServerHandshakePhase) message.getPhase()).ordinal())));
    }
}
