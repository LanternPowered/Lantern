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

import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.AbstractTopBottomShiftClickBehavior;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.PlayerTopBottomContainer;
import org.lanternpowered.server.inventory.transformation.InventoryTransforms;
import org.spongepowered.api.item.inventory.slot.OutputSlot;

public class PlayerInventoryShiftClickBehavior extends AbstractTopBottomShiftClickBehavior {

    public static final PlayerInventoryShiftClickBehavior INSTANCE = new PlayerInventoryShiftClickBehavior();

    @Override
    public IInventory getTarget(PlayerTopBottomContainer container, AbstractSlot slot) {
        final LanternPrimaryPlayerInventory main = container.getPlayerInventory().getPrimary();
        // Check if the slot isn't in the main inventory
        if (!main.containsInventory(slot)) {
            if (slot instanceof OutputSlot) {
                return main.transform(InventoryTransforms.REVERSE);
            }
            return main;
        // Shift click from the hotbar to the main inventory
        } else {
            IInventory target;
            if (main.getHotbar().containsInventory(slot)) {
                target = main.getStorage();
            } else {
                target = main.getHotbar();
            }
            return container.getPlayerInventory().getArmor().union(target);
        }
    }
}
