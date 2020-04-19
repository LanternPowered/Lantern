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
package org.lanternpowered.server.inventory;

import org.spongepowered.api.item.inventory.type.InventoryColumn;

import java.util.List;

public abstract class AbstractInventoryColumn extends AbstractInventory2D implements InventoryColumn {

    @Override
    void initWithSlots(List<AbstractMutableInventory> children, List<? extends AbstractSlot> slots) {
        super.initWithSlots(children, slots, 1, slots.size());
    }

    @Override
    void initWithChildren(List<AbstractMutableInventory> children, boolean lazy) {
        super.initWithChildren(children, 1, -1);
    }
}
