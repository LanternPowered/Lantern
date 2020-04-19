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

import org.lanternpowered.server.block.entity.ICarrierBlockEntity;
import org.lanternpowered.server.inventory.AbstractInventory;
import org.spongepowered.api.block.entity.carrier.CarrierBlockEntity;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.type.BlockEntityInventory;
import org.spongepowered.api.util.Direction;

public abstract class ContainerBlockEntity<I extends AbstractInventory & BlockEntityInventory<CarrierBlockEntity>>
        extends ContainerBlockEntityBase implements ICarrierBlockEntity {

    protected final I inventory;

    protected ContainerBlockEntity() {
        this.inventory = createInventory();
        this.inventory.addViewListener(this);
    }

    protected abstract I createInventory();

    @Override
    public I getInventory() {
        return this.inventory;
    }

    @Override
    public Inventory getInventory(Direction from) {
        return this.inventory;
    }
}
