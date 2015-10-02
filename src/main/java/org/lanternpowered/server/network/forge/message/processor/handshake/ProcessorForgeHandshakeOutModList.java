package org.lanternpowered.server.network.forge.message.processor.handshake;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutModList;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

public final class ProcessorForgeHandshakeOutModList implements Processor<MessageForgeHandshakeInOutModList> {

    @Override
    public void process(CodecContext context, MessageForgeHandshakeInOutModList message, List<Message> output) throws CodecException {
        Map<String, String> entries = message.getEntries();
        ByteBuf buf = context.byteBufAlloc().buffer();
        buf.writeByte(Constants.FML_HANDSHAKE_MOD_LIST);
        context.writeVarInt(buf, entries.size());
        for (Entry<String, String> en : entries.entrySet()) {
            context.write(buf, String.class, en.getKey());
            context.write(buf, String.class, en.getValue());
        }
        output.add(new MessagePlayInOutChannelPayload("FML|HS", buf));
    }
}
