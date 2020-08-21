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

import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.server.inventory.PlayerTopBottomContainer

object SimpleTopBottomShiftClickBehavior : AbstractTopBottomShiftClickBehavior() {

    override fun getTarget(container: PlayerTopBottomContainer, slot: ExtendedSlot): Inventory? {
        // Just shift click to the top inventory as well by default, and
        // block shift clicking when the top is full
        return if (container.playerInventory.primary.containsInventory(slot)) {
            container.openInventory
        } else {
            this.getDefaultTarget(container, slot)
        }
    }
}
