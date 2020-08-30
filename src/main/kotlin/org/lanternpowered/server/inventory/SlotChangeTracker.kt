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
package org.lanternpowered.server.inventory

import org.lanternpowered.api.item.inventory.slot.Slot

/**
 * Can be used to track the changes of a [Slot].
 */
interface SlotChangeTracker {

    /**
     * Queues a silent slot change for the specified [Slot].
     *
     * @param slot The slot
     */
    fun queueSlotChange(slot: Slot)

    /**
     * Queues a slot change for the specified slot index.
     *
     * @param index The slot index
     */
    fun queueSlotChange(index: Int)

    /**
     * Queues a silent slot change for the specified [Slot].
     *
     * @param slot The slot
     */
    fun queueSilentSlotChange(slot: Slot)

    /**
     * Queues a silent slot change for the specified slot index.
     *
     * @param index The slot index
     */
    fun queueSilentSlotChange(index: Int)
}
