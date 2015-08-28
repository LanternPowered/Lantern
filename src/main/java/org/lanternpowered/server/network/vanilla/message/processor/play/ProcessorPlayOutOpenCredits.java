package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.handler.codec.CodecException;

import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenCredits;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayOutChangeGameState;

@Caching
public final class ProcessorPlayOutOpenCredits implements Processor<MessagePlayOutOpenCredits> {

    @Override
    public void process(CodecContext context, MessagePlayOutOpenCredits message, List<Message> output) throws CodecException {
        output.add(new MessagePlayOutChangeGameState(4, 0f));
    }
}
