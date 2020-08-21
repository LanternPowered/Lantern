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
package org.lanternpowered.server.inventory.entity.player

import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.query
import org.lanternpowered.api.item.inventory.query.QueryTypes
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.item.inventory.slot.OutputSlot
import org.lanternpowered.server.inventory.PlayerTopBottomContainer
import org.lanternpowered.server.inventory.behavior.AbstractTopBottomShiftClickBehavior

object PlayerInventoryShiftClickBehavior : AbstractTopBottomShiftClickBehavior() {

    override fun getTarget(container: PlayerTopBottomContainer, slot: ExtendedSlot): Inventory {
        val main = container.playerInventory.primary
        // Check if the slot isn't in the main inventory
        return if (!main.containsInventory(slot)) {
            if (slot is OutputSlot) {
                main.query(QueryTypes.REVERSE)
            } else {
                main
            }
        } else {
            // Shift click from the hotbar to the main inventory
            val target = if (main.hotbar.containsInventory(slot)) {
                main.storage
            } else {
                main.hotbar
            }
            container.playerInventory.armor.union(target)
        }
    }
}
