package org.lanternpowered.server.network.forge.message.processor;

import io.netty.handler.codec.CodecException;

import java.util.List;

import org.lanternpowered.server.network.forge.message.handshake.ServerHandshakePhase;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInOutAck;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

public final class ProcessorHandshakeOutAck implements Processor<MessageHandshakeInOutAck> {

    @Override
    public void process(CodecContext context, MessageHandshakeInOutAck message, List<Message> output) throws CodecException {
        output.add(new MessagePlayInOutChannelPayload("FML|HS", context.byteBufAlloc()
                .buffer(2)
                .writeByte(ProcessorPlayInChannelPayload.FML_HANDSHAKE_ACK)
                // Only the server state should be send to the client
                .writeByte(((ServerHandshakePhase) message.getPhase()).ordinal())));
    }
}
