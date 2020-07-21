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

import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.item.RawItemStack;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityEquipment;
import org.spongepowered.api.item.inventory.ItemStack;

public final class CodecPlayOutEntityEquipment implements Codec<PacketPlayOutEntityEquipment> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutEntityEquipment packet) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(packet.getEntityId());
        buf.writeVarInt(packet.getSlotIndex());
        final Object item = packet.getItem();
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
