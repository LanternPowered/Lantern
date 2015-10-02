package org.lanternpowered.server.network.forge.message.processor.handshake;

import io.netty.handler.codec.CodecException;

import java.util.List;

import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutComplete;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

public final class ProcessorForgeHandshakeOutComplete implements Processor<MessageForgeHandshakeOutComplete> {

    private final static int SERVER = 1;

    @Override
    public void process(CodecContext context, MessageForgeHandshakeOutComplete message, List<Message> output) throws CodecException {
        output.add(new MessagePlayInOutChannelPayload("FML", context.byteBufAlloc()
                .buffer(2)
                .writeByte(Constants.FML_HANDSHAKE_COMPLETE)
                .writeByte(SERVER)));
    }
}
