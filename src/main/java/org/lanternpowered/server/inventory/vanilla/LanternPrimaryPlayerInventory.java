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
package org.lanternpowered.server.inventory.vanilla;

import org.lanternpowered.server.inventory.AbstractChildrenInventory;
import org.lanternpowered.server.inventory.AbstractGridInventory;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.transformation.InventoryTransforms;
import org.spongepowered.api.item.inventory.InventoryTransformation;
import org.spongepowered.api.item.inventory.entity.PrimaryPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

public class LanternPrimaryPlayerInventory extends AbstractChildrenInventory implements PrimaryPlayerInventory {

    private static final class Holder {

        private static final QueryOperation<?> STORAGE_INVENTORY_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(AbstractGridInventory.class);
        private static final QueryOperation<?> HOTBAR_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(LanternHotbarInventory.class);
    }

    private LanternHotbarInventory hotbar;
    private AbstractGridInventory storage;
    private AbstractGridInventory grid;

    private AbstractGridInventory priorityHotbar;
    private AbstractChildrenInventory prioritySelectedSlotAndHotbar;
    private AbstractChildrenInventory reverse;

    @Override
    protected void init() {
        super.init();

        this.storage = (AbstractGridInventory) query(Holder.STORAGE_INVENTORY_OPERATION).first();
        this.hotbar = (LanternHotbarInventory) query(Holder.HOTBAR_OPERATION).first();

        this.priorityHotbar = AbstractGridInventory.rowsViewBuilder()
                .row(0, this.hotbar) // Higher priority for the hotbar
                .grid(this.hotbar.getRows(), this.storage)
                .build();
        this.prioritySelectedSlotAndHotbar = AbstractChildrenInventory.viewBuilder()
                .inventory(this.hotbar.transform(InventoryTransforms.PRIORITY_SELECTED_SLOT_AND_HOTBAR)) // Higher priority for the hotbar
                .inventory(this.storage)
                .build();
        this.grid = AbstractGridInventory.rowsViewBuilder()
                .grid(0, this.storage)
                .row(this.storage.getRows(), this.hotbar)
                .build();
        this.reverse = (AbstractChildrenInventory) InventoryTransforms.REVERSE.transform(this);
    }

    @Override
    public IInventory transform(InventoryTransformation transformation) {
        // Cache some transformations that will be used often
        if (transformation == InventoryTransforms.PRIORITY_HOTBAR) {
            return this.priorityHotbar;
        } else if (transformation == InventoryTransforms.PRIORITY_SELECTED_SLOT_AND_HOTBAR) {
            return this.prioritySelectedSlotAndHotbar;
        } else if (transformation == InventoryTransforms.REVERSE) {
            return this.reverse;
        }
        return super.transform(transformation);
    }

    @Override
    public LanternHotbarInventory getHotbar() {
        return this.hotbar;
    }

    @Override
    public AbstractGridInventory getStorage() {
        return this.storage;
    }

    @Override
    public AbstractGridInventory asGrid() {
        return this.grid;
    }
}
