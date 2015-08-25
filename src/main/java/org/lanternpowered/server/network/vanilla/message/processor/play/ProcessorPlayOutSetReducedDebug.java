package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.handler.codec.CodecException;

import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetReducedDebug;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayOutEntityStatus;

public final class ProcessorPlayOutSetReducedDebug implements Processor<MessagePlayOutSetReducedDebug> {

    @Override
    public void process(CodecContext context, MessagePlayOutSetReducedDebug message, List<Message> output) throws CodecException {
        int entityId = context.channel().attr(ProcessorPlayOutPlayerJoinGame.PLAYER_ENTITY_ID).get();
        int action = message.isReduced() ? 22 : 23;
        output.add(new MessagePlayOutEntityStatus(entityId, action));
    }

}
