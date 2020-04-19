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
package org.lanternpowered.server.item.recipe.fuel;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

final class ConstantFuelBurnTimeProvider implements IFuelBurnTimeProvider {

    private final int time;

    ConstantFuelBurnTimeProvider(int time) {
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
