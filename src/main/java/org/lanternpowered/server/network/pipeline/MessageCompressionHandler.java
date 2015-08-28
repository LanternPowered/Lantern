package org.lanternpowered.server.network.pipeline;

import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.lanternpowered.server.network.session.Session;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.Attribute;

import static org.lanternpowered.server.network.message.codec.object.serializer.SimpleObjectSerializerContext.CONTEXT;

public final class MessageCompressionHandler extends MessageToMessageCodec<ByteBuf, ByteBuf> {

    private final Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION);
    private final Inflater inflater = new Inflater();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf0, List<Object> output) throws Exception {
        Attribute<Integer> attribute = ctx.channel().attr(Session.COMPRESSION_THRESHOLD);

        int threshold = attribute == null ? -1 : attribute.get();
        if (threshold == -1) {
            output.add(buf0);
        } else {
            ByteBuf buf1 = ctx.alloc().buffer();

            if (buf0.readableBytes() >= threshold) {
                int length = buf0.readableBytes();

                byte[] source = new byte[length];
                buf0.readBytes(source);

                this.deflater.setInput(source);
                this.deflater.finish();

                byte[] compressed = new byte[length];
                int compressedLength = this.deflater.deflate(compressed);

                this.deflater.reset();

                if (compressedLength == 0) {
                    throw new EncoderException("Failed to compress message of size " + length + "!");
                } else if (compressedLength >= length) {
                    // Compression increased the size, threshold is probably too low
                    // Send as an uncompressed packet
                    CONTEXT.writeVarInt(buf1, 0);
                    buf1.writeBytes(source);
                } else {
                    CONTEXT.writeVarInt(buf1, length);
                    buf1.writeBytes(compressed, 0, compressedLength);
                }
            } else {
                CONTEXT.writeVarInt(buf1, 0);
                buf1.writeBytes(buf0);
            }

            output.add(buf1);
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf buf0, List<Object> output) throws Exception {
        Attribute<Integer> attribute = ctx.channel().attr(Session.COMPRESSION_THRESHOLD);

        int threshold = attribute == null ? -1 : attribute.get();
        if (threshold == -1) {
            output.add(buf0);
        } else {
            int index = buf0.readerIndex();
            int uncompressedSize = CONTEXT.readVarInt(buf0);

            if (uncompressedSize == 0) {
                int length = buf0.readableBytes();

                if (length >= threshold) {
                    throw new DecoderException("Received uncompressed message of size " + length + " greater than threshold " + threshold);
                }

                ByteBuf buf1 = ctx.alloc().buffer(length);
                buf0.readBytes(buf1, length);

                output.add(buf1);
            } else {
                byte[] source = new byte[buf0.readableBytes()];
                buf0.readBytes(source);

                this.inflater.setInput(source);

                byte[] result = new byte[uncompressedSize];
                int resultLength = this.inflater.inflate(result);

                this.inflater.reset();

                if (resultLength == 0) {
                    buf0.readerIndex(index);
                    buf0.retain();

                    output.add(buf0);
                } else if (resultLength != uncompressedSize) {
                    throw new DecoderException("Received compressed message claiming to be of size " + uncompressedSize + " but actually "
                            + resultLength);
                } else {
                    output.add(Unpooled.wrappedBuffer(result));
                }
            }
        }
    }
}
