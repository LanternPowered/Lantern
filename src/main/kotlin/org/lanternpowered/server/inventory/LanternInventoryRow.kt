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

import org.lanternpowered.api.item.inventory.ExtendedInventoryRow
import org.lanternpowered.api.item.inventory.Slot
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.util.collections.toImmutableList
import org.spongepowered.math.vector.Vector2i

class LanternInventoryRow : AbstractInventory2D(), ExtendedInventoryRow {

    override val height: Int
        get() = 1

    override fun init(children: List<AbstractMutableInventory>, slots: List<AbstractSlot>) =
            super.init(children, slots, width = slots.size, height = 1)

    override fun init(children: List<AbstractMutableInventory>) =
            this.init(children, children.asSequence().slots().toImmutableList())

    override fun slotOrNull(position: Vector2i): ExtendedSlot? {
        if (position.y != 0)
            return null
        return this.slotOrNull(position.x)
    }

    override fun slotPositionOrNull(slot: Slot): Vector2i? {
        val index = this.slotIndexOrNull(slot) ?: return null
        return Vector2i(index, 0)
    }
}
