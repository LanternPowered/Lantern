package org.lanternpowered.server.network.forge.message.processor.handshake;

import io.netty.handler.codec.CodecException;

import java.util.List;

import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutReset;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

public final class ProcessorForgeHandshakeOutReset implements Processor<MessageForgeHandshakeOutReset> {

    @Override
    public void process(CodecContext context, MessageForgeHandshakeOutReset message, List<Message> output) throws CodecException {
        output.add(new MessagePlayInOutChannelPayload("FML|HS", context.byteBufAlloc()
                .buffer(1).writeByte(Constants.FML_HANDSHAKE_RESET)));
    }
}
