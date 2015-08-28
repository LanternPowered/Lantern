package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetWindowSlot;
import org.spongepowered.api.item.inventory.ItemStack;

@Caching(CodecUtils.LocaleCachingHash.class)
public final class CodecPlayOutWindowSetSlot implements Codec<MessagePlayOutSetWindowSlot> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutSetWindowSlot message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        buf.writeByte((byte) message.getWindow());
        buf.writeShort((short) message.getIndex());
        context.write(buf, ItemStack.class, message.getItem());
        return buf;
    }

    @Override
    public MessagePlayOutSetWindowSlot decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
