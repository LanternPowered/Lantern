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
package org.lanternpowered.server.item.recipe;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

final class ConstantIngredientQuantityProvider implements IIngredientQuantityProvider {

    private final int quantity;

    ConstantIngredientQuantityProvider(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int get(ItemStackSnapshot itemStackSnapshot) {
        return this.quantity;
    }

    @Override
    public int get(ItemStack itemStack) {
        return this.quantity;
    }
}
