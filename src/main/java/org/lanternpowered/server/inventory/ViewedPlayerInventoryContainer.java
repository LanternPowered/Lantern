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

import org.lanternpowered.server.inventory.vanilla.LanternPlayerInventory;
import org.lanternpowered.server.inventory.vanilla.ViewedPlayerInventory;
import org.spongepowered.api.item.inventory.crafting.CraftingInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

/**
 * Represents a player inventory container which can be viewed.
 *
 * <p>All the top inventory slots will be put into a chest inventory.</p>
 */
final class ViewedPlayerInventoryContainer extends ViewedPlayerInventory {

    ViewedPlayerInventoryContainer(LanternPlayerInventory playerInventory, AbstractChildrenInventory openInventory) {
        super(playerInventory, openInventory);
    }

    @Override
    protected Layout buildLayout() {
        final CraftingInventory craftingInventory = (CraftingInventory) getOpenInventory()
                .query(QueryOperationTypes.TYPE.of(CraftingInventory.class)).first();
        final AbstractGridInventory craftingGridInventory = (AbstractGridInventory) craftingInventory.getCraftingGrid();

        // Expand the player inventory layout with crafting grid and result
        final Layout layout = super.buildLayout();
        // Crafting grid
        for (int x = 0; x < craftingGridInventory.getColumns(); x++) {
            for (int y = 0; y < craftingGridInventory.getRows(); y++) {
                layout.bind(4 + x, 1 + y, craftingGridInventory.getSlot(x, y).get());
            }
        }
        // Crafting result
        layout.bind(7, 1, craftingInventory.getResult());
        return layout;
    }
}
