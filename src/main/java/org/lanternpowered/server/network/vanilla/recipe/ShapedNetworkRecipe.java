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
package org.lanternpowered.server.network.vanilla.recipe;

import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.Nullable;

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
