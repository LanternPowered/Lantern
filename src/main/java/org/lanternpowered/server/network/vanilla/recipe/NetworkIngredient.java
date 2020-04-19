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
package org.lanternpowered.server.network.vanilla.recipe;

import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public final class NetworkIngredient {

    private final Collection<ItemStack> items;

    public NetworkIngredient(Collection<ItemStack> items) {
        this.items = items;
    }

    public NetworkIngredient(ItemStack... items) {
        this.items = Arrays.asList(items);
    }

    void write(CodecContext ctx, ByteBuffer buf) {
        buf.writeVarInt(this.items.size());
        for (ItemStack itemStack : this.items) {
            ctx.write(buf, ContextualValueTypes.ITEM_STACK, itemStack);
        }
    }
}
