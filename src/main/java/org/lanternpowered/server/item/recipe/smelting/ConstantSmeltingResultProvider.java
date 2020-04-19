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
package org.lanternpowered.server.item.recipe.smelting;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.smelting.SmeltingResult;

final class ConstantSmeltingResultProvider implements ISmeltingResultProvider {

    private final SmeltingResult result;

    ConstantSmeltingResultProvider(SmeltingResult result) {
        this.result = result;
    }

    @Override
    public SmeltingResult get(ItemStackSnapshot ingredient) {
        return this.result;
    }

    @Override
    public SmeltingResult get(ItemStack ingredient) {
        return this.result;
    }
}
