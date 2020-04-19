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

final class EmptyCraftingMatrix implements ICraftingMatrix {

    static final EmptyCraftingMatrix INSTANCE = new EmptyCraftingMatrix();

    @Override
    public ItemStack get(int x, int y) {
        return ItemStack.empty();
    }

    @Override
    public int width() {
        return 1;
    }

    @Override
    public int height() {
        return 1;
    }

    @Override
    public CraftingMatrix copy() {
        return this;
    }

    @Override
    public void set(int x, int y, ItemStack itemStack) {
    }
}
