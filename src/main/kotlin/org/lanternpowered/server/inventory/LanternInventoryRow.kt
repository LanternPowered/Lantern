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
import org.lanternpowered.api.item.inventory.slot.Slot
import org.lanternpowered.api.util.collections.toImmutableList
import org.spongepowered.math.vector.Vector2i

open class LanternInventoryRow : AbstractInventory2D(), ExtendedInventoryRow {

    override val height: Int
        get() = 1

    override fun init(children: List<AbstractInventory>, slots: List<AbstractSlot>) =
            super.init(children, slots, width = slots.size, height = 1)

    override fun init(children: List<AbstractInventory>) =
            this.init(children, children.asSequence().slots().toImmutableList())

    override fun slotOrNull(position: Vector2i): AbstractSlot? {
        if (position.y != 0)
            return null
        return this.slotOrNull(position.x)
    }

    override fun slotPositionOrNull(slot: Slot): Vector2i? {
        val index = this.slotIndexOrNull(slot) ?: return null
        return Vector2i(index, 0)
    }

    override fun instantiateView(): InventoryView<LanternInventoryRow> = View(this)

    private class View(override val backing: LanternInventoryRow) : LanternInventoryRow(), InventoryView<LanternInventoryRow> {

        init {
            this.init(this.backing.children().createViews(this).asInventories())
        }
    }
}
