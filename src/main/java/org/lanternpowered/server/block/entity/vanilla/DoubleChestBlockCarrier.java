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

import org.lanternpowered.server.inventory.carrier.LanternMultiBlockCarrier;
import org.spongepowered.api.item.inventory.BlockCarrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.util.Direction;

import java.util.List;

public class DoubleChestBlockCarrier extends LanternMultiBlockCarrier<DoubleChestInventory> {

    public DoubleChestBlockCarrier(List<BlockCarrier> carriers) {
        super(carriers);
    }

    @Override
    public Inventory getInventory(Direction from) {
        return getInventory();
    }
}
