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
package org.lanternpowered.server.inventory.vanilla.block;

import org.lanternpowered.server.inventory.ICarriedInventory;
import org.lanternpowered.server.inventory.IViewableInventory;
import org.lanternpowered.server.inventory.type.LanternCraftingInventory;
import org.spongepowered.api.item.inventory.Carrier;

public class CraftingTableInventory extends LanternCraftingInventory implements ICarriedInventory<Carrier>, IViewableInventory {
}
