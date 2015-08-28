package org.lanternpowered.server.network.rcon;

import java.nio.ByteOrder;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

public class RconFramingHandler extends ByteToMessageCodec<ByteBuf> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        out.order(ByteOrder.LITTLE_ENDIAN).writeInt(msg.readableBytes());
        out.writeBytes(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }

        in.markReaderIndex();
        int length = in.order(ByteOrder.LITTLE_ENDIAN).readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        ByteBuf buf = ctx.alloc().buffer(length);
        in.readBytes(buf, length);
        out.add(buf);
    }

}
