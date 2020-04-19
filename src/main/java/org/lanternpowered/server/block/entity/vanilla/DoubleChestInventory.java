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
package org.lanternpowered.server.block.entity.vanilla;

import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.lanternpowered.server.inventory.vanilla.block.ChestInventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;

public class DoubleChestInventory extends ChestInventory {

    @Override
    public InventoryArchetype getArchetype() {
        return VanillaInventoryArchetypes.DOUBLE_CHEST;
    }
}
