package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;
import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeCommand;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeItemName;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeOffer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutBrand;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import com.flowpowered.math.vector.Vector3i;

public final class ProcessorPlayInChannelPayload extends AbstractPlayInChannelPayloadProcessor {

    @Override
    public void process0(CodecContext context, MessagePlayInOutChannelPayload message, List<Message> output) throws CodecException {
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
        } else if (channel.equals("FML|HS")) {
            throw new CodecException("Received and unexpected message with channel: " + channel);
        } else if (channel.equals("FML")) {
            // Fml channel
        } else if (channel.startsWith("FML")) {
            // A unknown/ignored fml channel
        } else {
            output.add(message);
        }
    }
}
