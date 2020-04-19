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

import org.checkerframework.checker.nullness.qual.Nullable;

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
                final ItemStackSnapshot itemStackSnapshot = remaining.get(y * w + x);
                if (!itemStackSnapshot.isEmpty()) {
                    int quantity;
                    final boolean flag = LanternItemStack.areSimilar(itemStack, itemStackSnapshot);
                    if (itemStack.isEmpty() || flag) {
                        if (!flag) {
                            itemStack = itemStackSnapshot.createStack();
                            matrix.set(x, y, itemStack);
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
