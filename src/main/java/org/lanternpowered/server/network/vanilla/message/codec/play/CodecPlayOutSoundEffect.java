package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSoundEffect;

import com.flowpowered.math.vector.Vector3d;

@Caching
public final class CodecPlayOutSoundEffect implements Codec<MessagePlayOutSoundEffect> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutSoundEffect message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.write(buf, String.class, message.getName());
        Vector3d pos = message.getPosition();
        buf.writeInt((int) (pos.getX() * 8d));
        buf.writeInt((int) (pos.getY() * 8d));
        buf.writeInt((int) (pos.getZ() * 8d));
        buf.writeFloat(message.getVolume());
        buf.writeByte((byte) Math.max(message.getPitch() * 63f, Byte.MAX_VALUE));
        return buf;
    }

    @Override
    public MessagePlayOutSoundEffect decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new UnsupportedOperationException();
    }
}
