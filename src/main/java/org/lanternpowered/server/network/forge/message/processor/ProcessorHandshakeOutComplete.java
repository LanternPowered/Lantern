package org.lanternpowered.server.network.forge.message.processor;

import io.netty.handler.codec.CodecException;

import java.util.List;

import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeOutComplete;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

public final class ProcessorHandshakeOutComplete implements Processor<MessageHandshakeOutComplete> {

    private final static int SERVER = 1;

    @Override
    public void process(CodecContext context, MessageHandshakeOutComplete message, List<Message> output) throws CodecException {
        output.add(new MessagePlayInOutChannelPayload("FML", context.byteBufAlloc()
                .buffer(2)
                .writeByte(Constants.FML_HANDSHAKE_COMPLETE)
                .writeByte(SERVER)));
    }
}
