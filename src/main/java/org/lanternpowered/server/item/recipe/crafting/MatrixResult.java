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

import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

public final class MatrixResult {

    private final CraftingMatrix craftingMatrix;
    private final List<ItemStack> restItems;

    public MatrixResult(CraftingMatrix craftingMatrix, List<ItemStack> restItems) {
        this.craftingMatrix = craftingMatrix;
        this.restItems = restItems;
    }

    /**
     * Gets the {@link CraftingMatrix} with all the
     * updated {@link ItemStack}s.
     *
     * @return The crafting matrix
     */
    public CraftingMatrix getCraftingMatrix() {
        return this.craftingMatrix;
    }

    /**
     * Gets a {@link List} with all the rest {@link ItemStack}s, these rest items are
     * "remainingItems" that couldn't be put back into into the crafting matrix.
     *
     * @return The rest item stacks
     */
    public List<ItemStack> getRest() {
        return this.restItems;
    }
}
