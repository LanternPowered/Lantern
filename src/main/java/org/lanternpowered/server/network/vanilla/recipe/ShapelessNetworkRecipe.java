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

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class ShapelessNetworkRecipe extends GroupedNetworkRecipe {

    private final List<NetworkIngredient> ingredients;

    // The result
    private final ItemStack result;

    ShapelessNetworkRecipe(String id, @Nullable String group, ItemStack result, List<NetworkIngredient> ingredients) {
        super(id, NetworkRecipeTypes.CRAFTING_SHAPELESS, group);
        this.ingredients = ingredients;
        this.result = result;
    }

    @Override
    void write(CodecContext ctx, ByteBuffer buf) {
        super.write(ctx, buf);
        buf.writeVarInt(this.ingredients.size());
        this.ingredients.forEach(ingredient -> ingredient.write(ctx, buf));
        ctx.write(buf, ContextualValueTypes.ITEM_STACK, this.result);
    }
}
