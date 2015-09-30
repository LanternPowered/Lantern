package org.lanternpowered.server.network.forge.message.processor;

import io.netty.handler.codec.CodecException;

import java.util.List;

import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInOutHello;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

public final class ProcessorHandshakeOutHello implements Processor<MessageHandshakeInOutHello> {

    // We will still use protocol 1, so we don't have to send
    // the overridden dimension id (maybe once we add support for that)
    // we could change it to 2 or higher future versions
    private final static int FORGE_PROTOCOL = 1;

    @Override
    public void process(CodecContext context, MessageHandshakeInOutHello message, List<Message> output) throws CodecException {
        output.add(new MessagePlayInOutChannelPayload("FML|HS", context.byteBufAlloc()
                .buffer(2)
                .writeByte(Constants.FML_HANDSHAKE_SERVER_HELLO)
                .writeByte(FORGE_PROTOCOL)));
    }
}
