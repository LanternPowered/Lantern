/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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

import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.NullMessage;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.codec.serializer.Types;
import org.lanternpowered.server.network.objects.RawItemStack;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInEditCommandBlock;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeItemName;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeOffer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInEditBook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutBrand;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInSignBook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenBook;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;

import java.util.List;

public final class CodecPlayInOutCustomPayload extends AbstractCodecPlayInOutCustomPayload {

    // TODO: Move these constants?
    private final static DataQuery PAGES = DataQuery.of("pages");
    private final static DataQuery AUTHOR = DataQuery.of("author");
    private final static DataQuery TITLE = DataQuery.of("title");

    private final static ByteBuf EMPTY = Unpooled.buffer(0);

    @Override
    protected MessageResult encode0(CodecContext context, Message message) throws CodecException {
        if (message instanceof MessagePlayInOutBrand) {
            return new MessageResult("MC|Brand", context.write(context.byteBufAlloc().buffer(), Types.STRING,
                    ((MessagePlayInOutBrand) message).getBrand()));
        } else if (message instanceof MessagePlayOutOpenBook) {
            return new MessageResult("MC|BOpen", EMPTY);
        }
        return null;
    }

    @Override
    protected Message decode0(CodecContext context, String channel, ByteBuf content) throws CodecException {
        if ("MC|ItemName".equals(channel)) {
            return new MessagePlayInChangeItemName(context.read(content, Types.STRING));
        } else if ("MC|TrSel".equals(channel)) {
            return new MessagePlayInChangeOffer(content.readInt());
        } else if ("MC|Brand".equals(channel)) {
            return new MessagePlayInOutBrand(context.read(content, Types.STRING));
        } else if ("MC|Beacon".equals(channel)) {
            // TODO
        } else if ("MC|AdvCdm".equals(channel)) {
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

            String command = context.read(content, Types.STRING);
            boolean shouldTrackOutput = content.readBoolean();

            if (pos != null) {
                return new MessagePlayInEditCommandBlock.Block(pos, command, shouldTrackOutput);
            } else {
                return new MessagePlayInEditCommandBlock.Entity(entityId, command, shouldTrackOutput);
            }
        } else if ("MC|AutoCmd".equals(channel)) {
            int x = content.readInt();
            int y = content.readInt();
            int z = content.readInt();
            String command = context.read(content, Types.STRING);
            boolean shouldTrackOutput = content.readBoolean();
            MessagePlayInEditCommandBlock.AdvancedBlock.Mode mode = MessagePlayInEditCommandBlock.AdvancedBlock.Mode.valueOf(
                    context.read(content, Types.STRING));
            boolean conditional = content.readBoolean();
            boolean automatic = content.readBoolean();
            return new MessagePlayInEditCommandBlock.AdvancedBlock(new Vector3i(x, y, z), command, shouldTrackOutput, mode, conditional, automatic);
        } else if ("MC|BSign".equals(channel)) {
            RawItemStack rawItemStack = context.read(content, Types.RAW_ITEM_STACK);
            if (rawItemStack == null) {
                throw new CodecException("Signed book may not be null!");
            }
            DataView dataView = rawItemStack.getDataView();
            if (dataView == null) {
                throw new CodecException("Signed book data view (nbt tag) may not be null!");
            }
            String author = dataView.getString(AUTHOR).orElseThrow(() -> new CodecException("Signed book author missing!"));
            String title = dataView.getString(TITLE).orElseThrow(() -> new CodecException("Signed book author missing!"));
            List<String> pages = dataView.getStringList(PAGES).orElseThrow(() -> new CodecException("Edited book pages missing!"));
            return new MessagePlayInSignBook(author, title, pages);
        } else if ("MC|BEdit".equals(channel)) {
            RawItemStack rawItemStack = context.read(content, Types.RAW_ITEM_STACK);
            if (rawItemStack == null) {
                throw new CodecException("Edited book may not be null!");
            }
            DataView dataView = rawItemStack.getDataView();
            if (dataView == null) {
                throw new CodecException("Edited book data view (nbt tag) may not be null!");
            }
            List<String> pages = dataView.getStringList(PAGES).orElseThrow(() -> new CodecException("Edited book pages missing!"));
            return new MessagePlayInEditBook(pages);
        } else if ("MC|Struct".equals(channel)) {
            // Something related to structure placing in minecraft 1.9,
            // seems like it's something mojang doesn't want to share with use,
            // they used this channel to build and save structures
        } else if ("MC|PickItem".equals(channel)) {
            // Also a new channel in 1.9
            // TODO
        } else if ("FML|HS".equals(channel)) {
            throw new CodecException("Received and unexpected message with channel: " + channel);
        } else if ("FML".equals(channel)) {
            // Fml channel
        } else if (channel.startsWith("FML")) {
            // A unknown/ignored fml channel
        } else {
            return new MessagePlayInOutChannelPayload(channel, content);
        }
        return NullMessage.INSTANCE;
    }
}
