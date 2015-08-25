package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.handler.codec.CodecException;

import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetGameMode;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayOutChangeGameState;

public final class ProcessorPlayOutSetGameMode implements Processor<MessagePlayOutSetGameMode> {

    @Override
    public void process(CodecContext context, MessagePlayOutSetGameMode message, List<Message> output) throws CodecException {
        output.add(new MessagePlayOutChangeGameState(3, message.getGameMode().getId()));
    }

}
