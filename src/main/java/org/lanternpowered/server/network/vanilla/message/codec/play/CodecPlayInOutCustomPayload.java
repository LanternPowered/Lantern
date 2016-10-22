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

import static org.lanternpowered.server.data.io.store.item.BookItemTypeObjectSerializer.AUTHOR;
import static org.lanternpowered.server.data.io.store.item.BookItemTypeObjectSerializer.PAGES;
import static org.lanternpowered.server.data.io.store.item.BookItemTypeObjectSerializer.TITLE;

import com.flowpowered.math.vector.Vector3i;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.objects.Types;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.NullMessage;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.objects.RawItemStack;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeItemName;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeOffer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInEditBook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInEditCommandBlock;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutBrand;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPickItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInSignBook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenBook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutStopSound;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.type.HandTypes;

import java.util.List;

public final class CodecPlayInOutCustomPayload extends AbstractCodecPlayInOutCustomPayload {

    @Override
    protected MessageResult encode0(CodecContext context, Message message) throws CodecException {
        if (message instanceof MessagePlayInOutBrand) {
            return new MessageResult("MC|Brand", context.byteBufAlloc().buffer().writeString(((MessagePlayInOutBrand) message).getBrand()));
        } else if (message instanceof MessagePlayOutOpenBook) {
            final ByteBuffer buf = context.byteBufAlloc().buffer();
            buf.writeVarInt(((MessagePlayOutOpenBook) message).getHandType() == HandTypes.MAIN_HAND ? 0 : 1);
            return new MessageResult("MC|BOpen", buf);
        } else if (message instanceof MessagePlayOutStopSound) {
            final MessagePlayOutStopSound message0 = (MessagePlayOutStopSound) message;
            final ByteBuffer buf = context.byteBufAlloc().buffer();
            buf.writeString(message0.getSound() == null ? "" : message0.getSound());
            buf.writeString(message0.getCategory() == null ? "" : message0.getCategory().getId());
            return new MessageResult("MC|StopSound", buf);
        }
        throw new EncoderException("Unsupported message type: " + message);
    }

    @Override
    protected Message decode0(CodecContext context, String channel, ByteBuffer content) throws CodecException {
        if ("MC|ItemName".equals(channel)) {
            return new MessagePlayInChangeItemName(content.readString());
        } else if ("MC|TrSel".equals(channel)) {
            return new MessagePlayInChangeOffer(content.readInteger());
        } else if ("MC|Brand".equals(channel)) {
            return new MessagePlayInOutBrand(content.readString());
        } else if ("MC|Beacon".equals(channel)) {
            // TODO
        } else if ("MC|AdvCdm".equals(channel)) {
            final byte type = content.readByte();

            Vector3i pos = null;
            int entityId = 0;

            if (type == 0) {
                int x = content.readInteger();
                int y = content.readInteger();
                int z = content.readInteger();
                pos = new Vector3i(x, y, z);
            } else if (type == 1) {
                entityId = content.readInteger();
            } else {
                throw new CodecException("Unknown modify command message type: " + type);
            }

            final String command = content.readString();
            final boolean shouldTrackOutput = content.readBoolean();

            if (pos != null) {
                return new MessagePlayInEditCommandBlock.Block(pos, command, shouldTrackOutput);
            } else {
                return new MessagePlayInEditCommandBlock.Entity(entityId, command, shouldTrackOutput);
            }
        } else if ("MC|AutoCmd".equals(channel)) {
            final int x = content.readInteger();
            final int y = content.readInteger();
            final int z = content.readInteger();
            final String command = content.readString();
            final boolean shouldTrackOutput = content.readBoolean();
            final MessagePlayInEditCommandBlock.AdvancedBlock.Mode mode =
                    MessagePlayInEditCommandBlock.AdvancedBlock.Mode.valueOf(content.readString());
            final boolean conditional = content.readBoolean();
            final boolean automatic = content.readBoolean();
            return new MessagePlayInEditCommandBlock.AdvancedBlock(new Vector3i(x, y, z), command, shouldTrackOutput, mode, conditional, automatic);
        } else if ("MC|BSign".equals(channel)) {
            final RawItemStack rawItemStack = content.read(Types.RAW_ITEM_STACK);
            //noinspection ConstantConditions
            if (rawItemStack == null) {
                throw new CodecException("Signed book may not be null!");
            }
            final DataView dataView = rawItemStack.getDataView();
            if (dataView == null) {
                throw new CodecException("Signed book data view (nbt tag) may not be null!");
            }
            final String author = dataView.getString(AUTHOR).orElseThrow(() -> new CodecException("Signed book author missing!"));
            final String title = dataView.getString(TITLE).orElseThrow(() -> new CodecException("Signed book title missing!"));
            final List<String> pages = dataView.getStringList(PAGES).orElseThrow(() -> new CodecException("Signed book pages missing!"));
            return new MessagePlayInSignBook(author, title, pages);
        } else if ("MC|BEdit".equals(channel)) {
            final RawItemStack rawItemStack = content.read(Types.RAW_ITEM_STACK);
            //noinspection ConstantConditions
            if (rawItemStack == null) {
                throw new CodecException("Edited book may not be null!");
            }
            final DataView dataView = rawItemStack.getDataView();
            if (dataView == null) {
                throw new CodecException("Edited book data view (nbt tag) may not be null!");
            }
            final List<String> pages = dataView.getStringList(PAGES).orElseThrow(() -> new CodecException("Edited book pages missing!"));
            return new MessagePlayInEditBook(pages);
        } else if ("MC|Struct".equals(channel)) {
            // Something related to structure placing in minecraft 1.9,
            // seems like it's something mojang doesn't want to share with use,
            // they used this channel to build and save structures
        } else if ("MC|PickItem".equals(channel)) {
            return new MessagePlayInPickItem(content.readVarInt());
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
