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
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInCreativeWindowAction;
import org.spongepowered.api.item.inventory.ItemStack;

public final class CodecPlayInCreativeWindowAction implements Codec<MessagePlayInCreativeWindowAction> {

    @Override
    public MessagePlayInCreativeWindowAction decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final int slot = buf.readShort();
        final ItemStack item = context.read(buf, ContextualValueTypes.ITEM_STACK);
        return new MessagePlayInCreativeWindowAction(slot, item);
    }
}
