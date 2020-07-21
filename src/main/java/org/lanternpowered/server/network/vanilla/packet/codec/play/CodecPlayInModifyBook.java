/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.network.vanilla.packet.codec.play;

import static org.lanternpowered.server.data.io.store.item.WritableBookItemTypeObjectSerializer.PAGES;
import static org.lanternpowered.server.data.io.store.item.WrittenBookItemTypeObjectSerializer.AUTHOR;
import static org.lanternpowered.server.data.io.store.item.WrittenBookItemTypeObjectSerializer.TITLE;

import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.item.RawItemStack;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInModifyBook;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;

import java.util.List;

public final class CodecPlayInModifyBook implements Codec<PacketPlayInModifyBook> {

    @Override
    public PacketPlayInModifyBook decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final RawItemStack rawItemStack = buf.readRawItemStack();
        final boolean sign = buf.readBoolean();
        final HandType handType = buf.readVarInt() == 0 ? HandTypes.MAIN_HAND : HandTypes.OFF_HAND;
        if (rawItemStack == null) {
            throw new DecoderException("Modified book may not be null!");
        }
        final DataView dataView = rawItemStack.getDataView();
        if (dataView == null) {
            throw new DecoderException("Modified book data view (nbt tag) may not be null!");
        }
        final List<String> pages = dataView.getStringList(PAGES).orElseThrow(() -> new DecoderException("Edited book pages missing!"));
        if (sign) {
            final String author = dataView.getString(AUTHOR).orElseThrow(() -> new CodecException("Signed book author missing!"));
            final String title = dataView.getString(TITLE).orElseThrow(() -> new CodecException("Signed book title missing!"));
            return new PacketPlayInModifyBook.Sign(handType, pages, author, title);
        }
        return new PacketPlayInModifyBook.Edit(handType, pages);
    }
}
