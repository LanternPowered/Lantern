package org.lanternpowered.server.network.pipeline;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import static org.lanternpowered.server.network.message.codec.object.serializer.SimpleObjectSerializerContext.CONTEXT;

public final class MessageFramingHandler extends ByteToMessageCodec<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf buf0, ByteBuf output) throws Exception {
        CONTEXT.writeVarInt(output, buf0.readableBytes());
        output.writeBytes(buf0);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> output) throws Exception {
        buf.markReaderIndex();

        if (!readableVarInt(buf)) {
            return;
        }

        int length = CONTEXT.readVarInt(buf);
        if (buf.readableBytes() < length) {
            buf.resetReaderIndex();
            return;
        }

        ByteBuf buf1 = ctx.alloc().buffer(length);
        buf.readBytes(buf1, length);

        output.add(buf1);
    }

    private static boolean readableVarInt(ByteBuf buf) {
        if (buf.readableBytes() > 5) {
            return true;
        }

        int idx = buf.readerIndex();
        byte in;
        do {
            if (buf.readableBytes() < 1) {
                buf.readerIndex(idx);
                return false;
            }
            in = buf.readByte();
        } while ((in & 0x80) != 0);

        buf.readerIndex(idx);
        return true;
    }
}
