/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.message.codec.play;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.ByteBufferAllocator;
import org.lanternpowered.server.network.channel.LanternChannelRegistrar;
import org.lanternpowered.server.network.forge.ForgeProtocol;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.NullMessage;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutUnregisterChannels;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public abstract class AbstractCodecPlayInOutCustomPayload implements Codec<Message> {

    private static final AttributeKey<MultiPartMessage> FML_MULTI_PART_MESSAGE = AttributeKey.valueOf("fml-mpm");

    private static final String REGISTER_CHANNEL = "REGISTER";
    private static final String UNREGISTER_CHANNEL = "UNREGISTER";

    @Override
    public ByteBuffer encode(CodecContext context, Message message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        final String channel;
        final ByteBuffer content;
        if (message instanceof MessagePlayInOutChannelPayload) {
            final MessagePlayInOutChannelPayload message1 = (MessagePlayInOutChannelPayload) message;
            content = message1.getContent();
            channel = message1.getChannel();
        } else if (message instanceof MessagePlayInOutRegisterChannels) {
            content = encodeChannels(((MessagePlayInOutRegisterChannels) message).getChannels());
            channel = REGISTER_CHANNEL;
        } else if (message instanceof MessagePlayInOutUnregisterChannels) {
            content = encodeChannels(((MessagePlayInOutUnregisterChannels) message).getChannels());
            channel = UNREGISTER_CHANNEL;
        } else {
            final MessageResult result = encode0(context, message);
            channel = result.channel;
            content = result.byteBuf;
        }
        buf.writeString(channel);
        buf.writeBytes(content);
        return buf;
    }

    @Override
    public Message decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final String channel = buf.readLimitedString(LanternChannelRegistrar.MAX_NAME_LENGTH);
        final int length = buf.available();
        if (length > Short.MAX_VALUE) {
            throw new DecoderException("CustomPayload messages may not be longer then " + Short.MAX_VALUE + " bytes");
        }
        final ByteBuffer content = buf.slice();
        final Message message = decode0(context, content, channel);
        if (content.available() > 0) {
            Lantern.getLogger().warn("Trailing bytes {}b after decoding with custom payload message codec {} with channel {}!\n{}",
                    content.available(), getClass().getName(), channel, message);
        }
        // Skip all the bytes, we already processed them
        buf.setReadIndex(buf.readerIndex() + buf.available());
        return message;
    }

    private Message decode0(CodecContext context, ByteBuffer content, String channel) {
        if (REGISTER_CHANNEL.equals(channel)) {
            final Set<String> channels = decodeChannels(content).stream()
                    .filter(s -> !s.startsWith(ForgeProtocol.MAIN_CHANNEL))
                    .collect(ImmutableSet.toImmutableSet());
            if (!channels.isEmpty()) {
                return new MessagePlayInOutRegisterChannels(channels);
            }
        } else if (UNREGISTER_CHANNEL.equals(channel)) {
            final Set<String> channels = decodeChannels(content).stream()
                    .filter(s -> !s.startsWith(ForgeProtocol.MAIN_CHANNEL))
                    .collect(ImmutableSet.toImmutableSet());
            if (!channels.isEmpty()) {
                return new MessagePlayInOutUnregisterChannels(channels);
            }
        } else if (ForgeProtocol.MULTI_PART_MESSAGE_CHANNEL.equals(channel)) {
            final Attribute<MultiPartMessage> attribute = context.getChannel().attr(FML_MULTI_PART_MESSAGE);
            final MultiPartMessage message0 = attribute.get();
            if (message0 == null) {
                final String channel0 = content.readString();
                final int parts = content.readByte() & 0xff;
                final int size = content.readInteger();
                if (size <= 0 || size >= -16797616) {
                    throw new CodecException("Received FML MultiPart packet outside of valid length bounds, Max: -16797616, Received: " + size);
                }
                attribute.set(new MultiPartMessage(channel0, context.byteBufAlloc().buffer(size), parts));
            } else {
                final int part = content.readByte() & 0xff;
                if (part != message0.index) {
                    throw new CodecException("Received FML MultiPart packet out of order, Expected: " + message0.index + ", Got: " + part);
                }
                final int len = content.available();
                content.readBytes(message0.buffer, message0.offset, len);
                message0.offset += len;
                message0.index++;
                if (message0.index >= message0.parts) {
                    final Message message = decode0(context, message0.channel, message0.buffer);
                    attribute.set(null);
                    return message;
                }
            }
        } else {
            return decode0(context, channel, content);
        }
        return NullMessage.INSTANCE;
    }

    protected abstract MessageResult encode0(CodecContext context, Message message) throws CodecException;

    protected abstract Message decode0(CodecContext context, String channel, ByteBuffer content) throws CodecException;

    /**
     * Decodes the byte buffer into a set of channels.
     *
     * @param buffer the byte buffer
     * @return the channels
     */
    private static Set<String> decodeChannels(ByteBuffer buffer) {
        final byte[] bytes = new byte[buffer.available()];
        buffer.readBytes(bytes);
        return Sets.newHashSet(Splitter.on('\u0000').split(new String(bytes, StandardCharsets.UTF_8)));
    }

    /**
     * Encodes the set of channels into a byte buffer.
     *
     * @param channels the channels
     * @return the byte buffer
     */
    private static ByteBuffer encodeChannels(Set<String> channels) {
        return ByteBufferAllocator.unpooled().wrappedBuffer(Joiner.on('\u0000').join(channels).getBytes(StandardCharsets.UTF_8));
    }

    protected static class MessageResult {

        private final String channel;
        private final ByteBuffer byteBuf;

        public MessageResult(String channel, ByteBuffer byteBuf) {
            this.byteBuf = byteBuf;
            this.channel = channel;
        }
    }

    private static class MultiPartMessage {

        private final String channel;
        private final ByteBuffer buffer;
        private final int parts;

        private int index;
        private int offset;

        MultiPartMessage(String channel, ByteBuffer buffer, int parts) {
            this.channel = channel;
            this.buffer = buffer;
            this.parts = parts;
        }
    }

}
