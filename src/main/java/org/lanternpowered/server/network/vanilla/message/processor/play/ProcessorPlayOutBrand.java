package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.handler.codec.CodecException;

import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutBrand;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

public final class ProcessorPlayOutBrand implements Processor<MessagePlayInOutBrand> {

    @Override
    public void process(CodecContext context, MessagePlayInOutBrand message, List<Message> output) throws CodecException {
        output.add(new MessagePlayInOutChannelPayload("MC|Brand",
                context.write(context.byteBufAlloc().buffer(), String.class, message.getBrand())));
    }

}
