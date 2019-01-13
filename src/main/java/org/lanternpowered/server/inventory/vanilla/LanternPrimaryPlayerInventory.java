/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
