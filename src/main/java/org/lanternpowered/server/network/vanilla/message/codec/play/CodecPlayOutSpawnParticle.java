package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayOutSpawnParticle;

import com.flowpowered.math.vector.Vector3f;

@Caching
public final class CodecPlayOutSpawnParticle implements Codec<MessagePlayOutSpawnParticle> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutSpawnParticle message) throws CodecException {
        Vector3f position = message.getPosition();
        Vector3f offset = message.getOffset();
        int[] extra = message.getExtra();
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.writeVarInt(buf, message.getParticleId());
        buf.writeBoolean(true);
        buf.writeFloat(position.getX());
        buf.writeFloat(position.getY());
        buf.writeFloat(position.getZ());
        buf.writeFloat(offset.getX());
        buf.writeFloat(offset.getY());
        buf.writeFloat(offset.getZ());
        buf.writeFloat(message.getData());
        buf.writeInt(message.getCount());
        for (int i = 0; i < extra.length; i++) {
            context.writeVarInt(buf, extra[i]);
        }
        return buf;
    }

    @Override
    public MessagePlayOutSpawnParticle decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
