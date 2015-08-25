package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.handler.codec.CodecException;
import io.netty.util.AttributeKey;

import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerJoinGame;

public class ProcessorPlayOutPlayerJoinGame implements Processor<MessagePlayOutPlayerJoinGame> {

    public final static AttributeKey<Integer> PLAYER_ENTITY_ID = AttributeKey.valueOf("player-entity-id");

    @Override
    public void process(CodecContext context, MessagePlayOutPlayerJoinGame message, List<Message> output) throws CodecException {
        context.channel().attr(PLAYER_ENTITY_ID).set(message.getEntityId());
        output.add(message);
    }

}
