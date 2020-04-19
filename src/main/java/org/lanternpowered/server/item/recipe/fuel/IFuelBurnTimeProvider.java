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

@FunctionalInterface
public interface IFuelBurnTimeProvider {

    default int get(ItemStackSnapshot itemStackSnapshot) {
        return get(itemStackSnapshot.createStack());
    }

    int get(ItemStack itemStack);
}
