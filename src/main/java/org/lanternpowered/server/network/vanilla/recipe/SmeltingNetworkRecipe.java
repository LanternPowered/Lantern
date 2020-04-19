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

import org.checkerframework.checker.nullness.qual.Nullable;

public final class SmeltingNetworkRecipe extends GroupedNetworkRecipe {

    private final ItemStack result;
    private final NetworkIngredient ingredient;
    private final double experience;
    private final int smeltingTime;

    public SmeltingNetworkRecipe(String id, @Nullable String group, ItemStack result,
            NetworkIngredient ingredient, double experience, int smeltingTime) {
        super(id, NetworkRecipeTypes.SMELTING, group);
        this.result = result;
        this.ingredient = ingredient;
        this.experience = experience;
        this.smeltingTime = smeltingTime;
    }

    @Override
    void write(CodecContext ctx, ByteBuffer buf) {
        super.write(ctx, buf);
        this.ingredient.write(ctx, buf);
        ctx.write(buf, ContextualValueTypes.ITEM_STACK, this.result);
        buf.writeFloat((float) this.experience);
        buf.writeVarInt(this.smeltingTime);
    }
}
