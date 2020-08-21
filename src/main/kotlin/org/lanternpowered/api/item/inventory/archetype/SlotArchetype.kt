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
package org.lanternpowered.api.item.inventory.archetype

import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.filter.ItemFilter
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot

/**
 * Represents an archetype of an [ExtendedSlot].
 */
interface SlotArchetype<T : ExtendedSlot> : InventoryArchetype<T> {

    /**
     * The hard stack size limit that can be stored in the slot.
     */
    val stackSizeLimit: Int

    /**
     * The stack size limit that applies to the
     * given [ItemStackSnapshot].
     */
    fun maxStackQuantityFor(snapshot: ItemStackSnapshot): Int

    /**
     * The stack size limit that applies to the
     * given [ItemStackSnapshot].
     */
    fun maxStackQuantityFor(type: ItemType): Int

    /**
     * The item filter that is applied to this slot, or
     * `null` if this slot accepts everything.
     */
    val filter: ItemFilter?
}
