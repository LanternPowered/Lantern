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
package org.lanternpowered.server.block.entity;

import org.lanternpowered.server.inventory.LanternEmptyCarriedInventory;
import org.spongepowered.api.block.entity.carrier.CarrierBlockEntity;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.util.Direction;

public interface ICarrierBlockEntity extends CarrierBlockEntity {

    @Override
    default Inventory getInventory(Direction from) {
        return new LanternEmptyCarriedInventory(this);
    }
}
