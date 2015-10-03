package org.lanternpowered.server.network.forge.message.processor.handshake;

import io.netty.handler.codec.CodecException;

import java.util.List;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.processor.play.AbstractPlayInChannelPayloadProcessor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

public final class ProcessorPlayOutChannelPayload extends AbstractPlayInChannelPayloadProcessor {

    @Override
    public void process0(CodecContext context, MessagePlayInOutChannelPayload message, List<Message> output) throws CodecException {
        throw new CodecException("Attempt to send a unexpected channel payload message: " + message.getChannel());
    }
}