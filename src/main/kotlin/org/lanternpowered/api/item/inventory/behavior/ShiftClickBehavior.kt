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
package org.lanternpowered.api.item.inventory.behavior

import org.lanternpowered.api.item.inventory.container.ExtendedContainer
import org.lanternpowered.api.item.inventory.ExtendedInventory
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot

/**
 * A behavior used when shift-clicking in a container.
 */
interface ShiftClickBehavior {

    /**
     * Gets the [ExtendedInventory] that should be used when shift clicking
     * on the specified [ExtendedSlot]. `null` can be returned to disable
     * shift clicking.
     *
     * @param container The container that is currently viewed
     * @param slot The slot that was shift-clicked on
     * @return The target inventory, or null if none
     */
    fun getTarget(container: ExtendedContainer, slot: ExtendedSlot): Inventory?
}
