package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockBreakAnimation;

import com.flowpowered.math.vector.Vector3i;

@Caching
public final class CodecPlayOutBlockBreakAnimation implements Codec<MessagePlayOutBlockBreakAnimation> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutBlockBreakAnimation message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.writeVarInt(buf, message.getId());
        context.write(buf, Vector3i.class, message.getPosition());
        // Make sure that the state fits in the byte
        int state = message.getState();
        buf.writeByte((byte) (state >= 0 && state <= 9 ? state : 10));
        return null;
    }

    @Override
    public MessagePlayOutBlockBreakAnimation decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
