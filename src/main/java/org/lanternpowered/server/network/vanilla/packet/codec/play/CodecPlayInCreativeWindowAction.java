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
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInCreativeWindowAction;
import org.spongepowered.api.item.inventory.ItemStack;

public final class CodecPlayInCreativeWindowAction implements Codec<PacketPlayInCreativeWindowAction> {

    @Override
    public PacketPlayInCreativeWindowAction decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final int slot = buf.readShort();
        final ItemStack item = context.read(buf, ContextualValueTypes.ITEM_STACK);
        return new PacketPlayInCreativeWindowAction(slot, item);
    }
}
