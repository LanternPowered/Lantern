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
package org.lanternpowered.server.item.recipe.crafting;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.recipe.crafting.CraftingResult;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public final class ExtendedCraftingResult {

    private final CraftingResult result;
    private final CraftingMatrix matrix;
    @Nullable final int[][] itemQuantities;
    private final int maxTimes;

    public ExtendedCraftingResult(CraftingResult result, CraftingMatrix matrix, int maxTimes) {
        this(result, matrix, maxTimes, null);
    }

    ExtendedCraftingResult(CraftingResult result, CraftingMatrix matrix, int maxTimes, @Nullable int[][] itemQuantities) {
        this.itemQuantities = itemQuantities;
        this.maxTimes = maxTimes;
        this.matrix = matrix;
        this.result = result;
    }

    /**
     * Gets the {@link CraftingResult}.
     *
     * @return The crafting result
     */
    public CraftingResult getResult() {
        return this.result;
    }

    /**
     * Gets the result {@link ItemStack}.
     *
     * @return The result item stack
     */
    public ItemStack getResultItem(int times) {
        checkArgument(times <= this.maxTimes, "times cannot exceed getMaxTimes");
        final ItemStack itemStack = this.result.getResult().createStack();
        itemStack.setQuantity(itemStack.getQuantity() * times);
        return itemStack;
    }
    
    /**
     * Gets the {@link MatrixResult}.
     *
     * @return The matrix result
     */
    public MatrixResult getMatrixResult(int times) {
        checkArgument(times <= this.maxTimes, "times cannot exceed getMaxTimes");
        final ICraftingMatrix matrix = (ICraftingMatrix) this.matrix.copy();
        final List<ItemStackSnapshot> remaining = this.result.getRemainingItems();
        final List<ItemStack> rest = new ArrayList<>();
        final int w = matrix.width();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < matrix.height(); y++) {
                ItemStack itemStack = matrix.get(x, y);
                if (!itemStack.isEmpty()) {
                    itemStack.setQuantity(itemStack.getQuantity() -
                            (this.itemQuantities == null ? 1 : this.itemQuantities[x][y]) * times);
                }
                final ItemStackSnapshot itemStackSnapshot = remaining.get(y + w * x);
                if (!itemStackSnapshot.isEmpty()) {
                    int quantity;
                    final boolean flag = LanternItemStack.areSimilar(itemStack, itemStackSnapshot);
                    if (itemStack.isEmpty() || flag) {
                        if (!flag) {
                            itemStack = itemStackSnapshot.createStack();
                        }
                        final int max = itemStack.getMaxStackQuantity();
                        quantity = itemStack.getQuantity() * times;
                        if (quantity > max) {
                            itemStack.setQuantity(max);
                            itemStack = itemStack.copy();
                            itemStack.setQuantity(quantity - max);
                        } else {
                            itemStack = null;
                        }
                    } else {
                        itemStack = itemStackSnapshot.createStack();
                        itemStack.setQuantity(itemStack.getQuantity() * times);
                    }
                    if (itemStack != null) {
                        rest.add(itemStack);
                    }
                }
            }
        }
        return new MatrixResult(matrix, rest);
    }

    /**
     * Gets the amount of times that the {@link CraftingResult}
     * can be crafted from a {@link GridInventory}.
     *
     * @return The times
     */
    public int getMaxTimes() {
        return this.maxTimes;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("result", this.result)
                .add("maxTimes", this.maxTimes)
                .toString();
    }
}
