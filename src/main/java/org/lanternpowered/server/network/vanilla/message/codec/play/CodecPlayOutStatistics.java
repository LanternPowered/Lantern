package org.lanternpowered.server.network.vanilla.message.codec.play;

import java.util.Set;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutStatistics;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutStatistics.Entry;

public final class CodecPlayOutStatistics implements Codec<MessagePlayOutStatistics> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutStatistics message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        Set<Entry> entries = message.getEntries();
        context.writeVarInt(buf, entries.size());
        for (Entry entry : entries) {
            context.write(buf, String.class, entry.getName());
            context.writeVarInt(buf, entry.getValue());
        }
        return buf;
    }

    @Override
    public MessagePlayOutStatistics decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
