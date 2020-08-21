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
package org.lanternpowered.server.inventory.behavior

import org.lanternpowered.api.item.inventory.ExtendedContainer
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.query
import org.lanternpowered.api.item.inventory.query.QueryTypes
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.item.inventory.slot.InputSlot
import org.lanternpowered.server.inventory.PlayerTopBottomContainer

abstract class AbstractShiftClickBehavior : ShiftClickBehavior {

    /**
     * Gets the default target [Inventory], this target will never be to
     * the top inventory, only from the top to the bottom one or shifting
     * between the hotbar and the grid. `null` can be returned if there
     * isn't a default shift click target.
     *
     * @param container The container
     * @param slot The slot
     * @return The default target inventory
     */
    protected fun getDefaultTarget(container: ExtendedContainer, slot: ExtendedSlot): Inventory? =
            if (container is PlayerTopBottomContainer) this.getDefaultTarget(container as PlayerTopBottomContainer, slot) else null

    /**
     * Gets the default target [Inventory], this target will never be to the
     * top inventory, only from the top to the bottom one or shifting between
     * the hotbar and the grid.
     *
     * @param container The container
     * @param slot The slot
     * @return The default target inventory
     */
    protected fun getDefaultTarget(container: PlayerTopBottomContainer, slot: ExtendedSlot): Inventory {
        // Default top bottom inventory behavior
        val primary = container.playerInventory.primary
        // Check if the slot isn't in the main inventory
        return if (!primary.containsInventory(slot)) {
            if (slot is InputSlot) {
                // The input slots use a different insertion order to the default
                primary
            } else {
                // Shift click to the main inventory
                primary.query(QueryTypes.REVERSE)
            }
        // Shift click from the hotbar to the main inventory
        } else if (primary.hotbar.containsInventory(slot)) {
            primary.storage
        } else {
            primary.hotbar
        }
    }
}
