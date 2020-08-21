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
package org.lanternpowered.server.inventory.builder

import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.archetype.SlotArchetype
import org.lanternpowered.api.item.inventory.filter.ItemFilter
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot

class LanternSlotArchetype<T : ExtendedSlot> : SlotArchetype<T> {

    override val stackSizeLimit: Int
        get() = TODO("Not yet implemented")

    override fun maxStackQuantityFor(snapshot: ItemStackSnapshot): Int {
        TODO("Not yet implemented")
    }

    override fun maxStackQuantityFor(type: ItemType): Int {
        TODO("Not yet implemented")
    }

    override val filter: ItemFilter?
        get() = TODO("Not yet implemented")
}
