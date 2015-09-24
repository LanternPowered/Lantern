package org.lanternpowered.server.network.forge.message.processor;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInOutModList;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

public final class ProcessorHandshakeOutModList implements Processor<MessageHandshakeInOutModList> {

    @Override
    public void process(CodecContext context, MessageHandshakeInOutModList message, List<Message> output) throws CodecException {
        Map<String, String> entries = message.getEntries();
        ByteBuf buf = context.byteBufAlloc().buffer();
        buf.writeByte(ProcessorPlayInChannelPayload.FML_HANDSHAKE_MOD_LIST);
        context.writeVarInt(buf, entries.size());
        for (Entry<String, String> en : entries.entrySet()) {
            context.write(buf, String.class, en.getKey());
            context.write(buf, String.class, en.getValue());
        }
        output.add(new MessagePlayInOutChannelPayload("FML|HS", buf));
    }
}
