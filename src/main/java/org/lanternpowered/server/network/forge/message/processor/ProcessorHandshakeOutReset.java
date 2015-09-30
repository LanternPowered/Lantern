package org.lanternpowered.server.network.forge.message.processor;

import io.netty.handler.codec.CodecException;

import java.util.List;

import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeOutReset;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

public final class ProcessorHandshakeOutReset implements Processor<MessageHandshakeOutReset> {

    @Override
    public void process(CodecContext context, MessageHandshakeOutReset message, List<Message> output) throws CodecException {
        output.add(new MessagePlayInOutChannelPayload("FML|HS", context.byteBufAlloc()
                .buffer(1).writeByte(Constants.FML_HANDSHAKE_RESET)));
    }
}
