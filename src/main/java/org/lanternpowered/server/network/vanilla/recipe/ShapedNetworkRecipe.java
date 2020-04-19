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

public final class ShapedNetworkRecipe extends GroupedNetworkRecipe {

    private final ItemStack result;
    // first index = x
    // second index = y
    private final NetworkIngredient[][] ingredients;

    public ShapedNetworkRecipe(String id, @Nullable String group, ItemStack result,
            NetworkIngredient[][] ingredients) {
        super(id, NetworkRecipeTypes.CRAFTING_SHAPED, group);
        this.ingredients = ingredients;
        this.result = result;
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    void write(CodecContext ctx, ByteBuffer buf) {
        buf.writeVarInt(this.ingredients.length);
        buf.writeVarInt(this.ingredients[0].length);
        super.write(ctx, buf);
        for (int j = 0; j < this.ingredients[0].length; j++) {
            for (int i = 0; i < this.ingredients.length; i++) {
                this.ingredients[i][j].write(ctx, buf);
            }
        }
        ctx.write(buf, ContextualValueTypes.ITEM_STACK, this.result);
    }
}
