package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.CodecException;

import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenBook;

@Caching
public final class ProcessorPlayOutOpenBook implements Processor<MessagePlayOutOpenBook> {

    private final static ByteBuf EMPTY = Unpooled.buffer(0);

    @Override
    public void process(CodecContext context, MessagePlayOutOpenBook message, List<Message> output) throws CodecException {
        output.add(new MessagePlayInOutChannelPayload("MC|BOpen", EMPTY));
    }
}
