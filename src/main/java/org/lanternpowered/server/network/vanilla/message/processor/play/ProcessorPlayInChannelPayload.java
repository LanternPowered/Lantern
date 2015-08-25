package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.CodecException;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeCommand;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeItemName;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeOffer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutBrand;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutUnregisterChannels;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

public final class ProcessorPlayInChannelPayload implements Processor<MessagePlayInOutChannelPayload> {

    public static final AttributeKey<Boolean> FML_MULTI_PART_MESSAGE_ENABLED = AttributeKey.valueOf("fml-multi-part-message-enabled");
    public static final AttributeKey<MultiPartMessage> FML_MULTI_PART_MESSAGE = AttributeKey.valueOf("fml-multi-part-message");

    @Override
    public void process(CodecContext context, MessagePlayInOutChannelPayload message, List<Message> output) throws CodecException {
        String channel = message.getChannel();
        ByteBuf content = message.getContent();

        if (channel.equals("MC|ItemName")) {
            output.add(new MessagePlayInChangeItemName(context.read(content, String.class)));
        } else if (channel.equals("MC|TrSel")) {
            output.add(new MessagePlayInChangeOffer(content.readInt()));
        } else if (channel.equals("MC|Brand")) {
            output.add(new MessagePlayInOutBrand(context.read(content, String.class)));
        } else if (channel.equals("MC|Beacon")) {
            // TODO
        } else if (channel.equals("MC|AdvCdm")) {
            byte type = content.readByte();

            Vector3i pos = null;
            int entityId = 0;

            if (type == 0) {
                int x = content.readInt();
                int y = content.readInt();
                int z = content.readInt();
                pos = new Vector3i(x, y, z);
            } else if (type == 1) {
                entityId = content.readInt();
            } else {
                throw new CodecException("Unknown modify command message type: " + type);
            }

            String command = context.read(content, String.class);
            boolean shouldTrackOutput = content.readBoolean();

            if (pos != null) {
                output.add(new MessagePlayInChangeCommand.Block(pos, command, shouldTrackOutput));
            } else {
                output.add(new MessagePlayInChangeCommand.Entity(entityId, command, shouldTrackOutput));
            }
        } else if (channel.equals("MC|BSign")) {
            // TODO
        } else if (channel.equals("MC|BEdit")) {
            // TODO
            
        } else if (channel.equals("MC|Struct")) {
            // Something related to structure placing in minecraft 1.9
            // TODO
        } else if (channel.equals("REGISTER")) {
            Set<String> channels = decodeChannels(content);
            Iterator<String> it = channels.iterator();
            while (it.hasNext()) {
                String channel0 = it.next();
                if (channel0.equals("FML|MP")) {
                    context.channel().attr(FML_MULTI_PART_MESSAGE_ENABLED).set(true);
                }
                if (channel0.startsWith("FML")) {
                    it.remove();
                }
            }
            if (!channels.isEmpty()) {
                output.add(new MessagePlayInOutRegisterChannels(channels));
            }
        } else if (channel.equals("UNREGISTER")) {
            Set<String> channels = decodeChannels(content);
            Iterator<String> it = channels.iterator();
            while (it.hasNext()) {
                String channel0 = it.next();
                if (channel0.equals("FML|MP")) {
                    context.channel().attr(FML_MULTI_PART_MESSAGE_ENABLED).set(false);
                }
                if (channel0.startsWith("FML")) {
                    it.remove();
                }
            }
            if (!channels.isEmpty()) {
                output.add(new MessagePlayInOutUnregisterChannels(channels));
            }
        } else if (channel.equals("FML|MP")) {
            Attribute<MultiPartMessage> attribute = context.channel().attr(FML_MULTI_PART_MESSAGE);
            MultiPartMessage message0 = attribute.get();
            if (message0 == null) {
                String channel0 = context.read(content, String.class);
                int parts = content.readUnsignedByte();
                int size = content.readInt();
                if (size <= 0 || size >= -16797616) {
                    throw new CodecException("Received FML MultiPart packet outside of valid length bounds, Max: -16797616, Received: " + size);
                }
                attribute.set(new MultiPartMessage(channel0, context.byteBufAlloc().buffer(size), parts));
            } else {
                int part = content.readUnsignedByte();
                if (part != message0.index) {
                    throw new CodecException("Received FML MultiPart packet out of order, Expected: " + message0.index + ", Got: " + part);
                }
                int len = content.readableBytes() - 1;
                content.readBytes(message0.buffer, message0.offset, len);
                message0.offset += len;
                message0.index++;
                if (message0.index >= message0.parts) {
                    output.add(new MessagePlayInOutChannelPayload(message0.channel, message0.buffer));
                    attribute.set(null);
                }
            }
        } else if (channel.startsWith("FML")) {
            // A unknown/ignored fml channel
        } else {
            output.add(message);
        }
    }

    /**
     * Decodes the byte buffer into a set of channels.
     * 
     * @param buffer the byte buffer
     * @return the channels
     */
    public static Set<String> decodeChannels(ByteBuf buffer) {
        return Sets.newHashSet(Splitter.on('\u0000').split(new String(buffer.array(), StandardCharsets.UTF_8)));
    }

    /**
     * Encodes the set of channels into a byte buffer.
     * 
     * @param channels the channels
     * @return the byte buffer
     */
    public static ByteBuf encodeChannels(Set<String> channels) {
        return Unpooled.wrappedBuffer(Joiner.on('\u0000').join(channels).getBytes(StandardCharsets.UTF_8));
    }

    private static class MultiPartMessage {

        private final String channel;
        private final ByteBuf buffer;
        private final int parts;

        private int index;
        private int offset;

        public MultiPartMessage(String channel, ByteBuf buffer, int parts) {
            this.channel = channel;
            this.buffer = buffer;
            this.parts = parts;
        }
    }

}
