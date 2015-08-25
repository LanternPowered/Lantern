package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.handler.codec.CodecException;

import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutUnregisterChannels;

import static org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInChannelPayload.encodeChannels;

public final class ProcessorPlayOutUnregisterChannels implements Processor<MessagePlayInOutUnregisterChannels> {

    @Override
    public void process(CodecContext context, MessagePlayInOutUnregisterChannels message, List<Message> output) throws CodecException {
        output.add(new MessagePlayInOutChannelPayload("UNREGISTER", encodeChannels(message.getChannels())));
    }

}
