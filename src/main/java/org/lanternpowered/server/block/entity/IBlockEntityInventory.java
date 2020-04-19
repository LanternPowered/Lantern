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

import org.lanternpowered.server.inventory.ICarriedInventory;
import org.lanternpowered.server.inventory.IViewableInventory;
import org.spongepowered.api.block.entity.carrier.CarrierBlockEntity;
import org.spongepowered.api.item.inventory.type.BlockEntityInventory;

import java.util.Optional;

public interface IBlockEntityInventory extends BlockEntityInventory<CarrierBlockEntity>,
        ICarriedInventory<CarrierBlockEntity>, IViewableInventory {

    @Override
    default void markDirty() {
    }

    @Override
    default Optional<CarrierBlockEntity> getBlockEntity() {
        return getCarrier();
    }
}
