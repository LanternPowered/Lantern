package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.handler.codec.CodecException;

import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;

import static org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInChannelPayload.encodeChannels;

public final class ProcessorPlayOutRegisterChannels implements Processor<MessagePlayInOutRegisterChannels> {

    @Override
    public void process(CodecContext context, MessagePlayInOutRegisterChannels message, List<Message> output) throws CodecException {
        output.add(new MessagePlayInOutChannelPayload("REGISTER", encodeChannels(message.getChannels())));
    }
}
