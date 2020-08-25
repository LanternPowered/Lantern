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
package org.lanternpowered.server.inventory.behavior;

import org.lanternpowered.api.item.inventory.behavior.ShiftClickBehavior;
import org.lanternpowered.server.inventory.AbstractContainer;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.PlayerTopBottomContainer;
import org.lanternpowered.server.inventory.transformation.InventoryTransforms;
import org.lanternpowered.server.inventory.vanilla.LanternPrimaryPlayerInventory;
import org.spongepowered.api.item.inventory.slot.InputSlot;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractShiftClickBehavior implements ShiftClickBehavior {

    /**
     * Gets the default target {@link IInventory}, this target will never
     * be to the top inventory, only from the top to the bottom one or shifting
     * between the hotbar and the grid. {@code null} can be returned if there
     * isn't a default shift click target.
     *
     * @param container The container
     * @param slot The slot
     * @return The default target inventory
     */
    @Nullable
    protected IInventory getDefaultTarget(AbstractContainer container, AbstractSlot slot) {
        if (!(container instanceof PlayerTopBottomContainer)) {
            return null;
        }
        return getDefaultTarget((PlayerTopBottomContainer) container, slot);
    }

    /**
     * Gets the default target {@link IInventory}, this target will never
     * be to the top inventory, only from the top to the bottom one or shifting
     * between the hotbar and the grid.
     *
     * @param container The container
     * @param slot The slot
     * @return The default target inventory
     */
    protected IInventory getDefaultTarget(PlayerTopBottomContainer container, AbstractSlot slot) {
        // Default top bottom inventory behavior
        final LanternPrimaryPlayerInventory primary = container.getPlayerInventory().getPrimary();
        // Check if the slot isn't in the main inventory
        if (!primary.containsInventory(slot)) {
            if (slot instanceof InputSlot) {
                // The input slots use a different insertion order to the default
                return primary;
            }
            // Check click to the main inventory
            return primary.transform(InventoryTransforms.REVERSE);
        // Shift click from the hotbar to the main inventory
        } else if (primary.getHotbar().containsInventory(slot)) {
            return primary.getStorage();
        } else {
            return primary.getHotbar();
        }
    }
}
