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

final class ConstantSmeltingTimeProvider implements ISmeltingTimeProvider {

    private final int time;

    ConstantSmeltingTimeProvider(int time) {
        this.time = time;
    }

    @Override
    public int get(ItemStackSnapshot itemStackSnapshot) {
        return this.time;
    }

    @Override
    public int get(ItemStack itemStack) {
        return this.time;
    }
}
