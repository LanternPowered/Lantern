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
import org.lanternpowered.server.inventory.view.FancyTopBottomGridViewContainer;

/**
 * Represents a player inventory container which can be viewed.
 *
 * <p>All the top inventory slots will be put into a chest inventory.</p>
 */
public class ViewedPlayerInventory extends FancyTopBottomGridViewContainer {

    public ViewedPlayerInventory(LanternPlayerInventory playerInventory, AbstractChildrenInventory openInventory) {
        super(playerInventory, openInventory);
    }

    @Override
    protected Layout buildLayout() {
        final LanternPlayerInventory playerInventory = getPlayerInventory();
        final LanternPlayerArmorInventory armorInventory = playerInventory.getArmor();

        final Layout layout = new Layout.Chest(4);
        // Armor
        for (int i = 0; i < armorInventory.capacity(); i++) {
            layout.bind(0, i, armorInventory.getSlot(i).get());
        }
        // Off hand
        layout.bind(2, 3, playerInventory.getOffhand());
        return layout;
    }
}
