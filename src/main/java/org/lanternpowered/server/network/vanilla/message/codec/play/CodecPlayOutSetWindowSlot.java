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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.item.RawItemStack;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetWindowSlot;
import org.spongepowered.api.item.inventory.ItemStack;

public final class CodecPlayOutSetWindowSlot implements Codec<MessagePlayOutSetWindowSlot> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutSetWindowSlot message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeByte((byte) message.getWindow());
        buf.writeShort((short) message.getIndex());
        final Object item = message.getItem();
        if (item instanceof ItemStack) {
            context.write(buf, ContextualValueTypes.ITEM_STACK, (ItemStack) item);
        } else if (item instanceof RawItemStack || item == null) {
            buf.writeRawItemStack((RawItemStack) item);
        } else {
            throw new EncoderException("Invalid item type:" + item.getClass().getName());
        }
        return buf;
    }
}
