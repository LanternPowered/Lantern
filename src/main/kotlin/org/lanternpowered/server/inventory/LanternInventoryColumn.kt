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

import org.lanternpowered.api.item.inventory.ExtendedInventoryColumn
import org.lanternpowered.api.item.inventory.slot.Slot
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.util.collections.toImmutableList
import org.spongepowered.math.vector.Vector2i

open class LanternInventoryColumn : AbstractInventory2D(), ExtendedInventoryColumn {

    override val width: Int
        get() = 1

    override fun init(children: List<AbstractMutableInventory>, slots: List<AbstractSlot>) =
            super.init(children, slots, width = 1, height = slots.size)

    override fun init(children: List<AbstractMutableInventory>) =
            this.init(children, children.asSequence().slots().toImmutableList())

    override fun slotOrNull(position: Vector2i): ExtendedSlot? {
        if (position.x != 0)
            return null
        return this.slot(position.y)
    }

    override fun slotPositionOrNull(slot: Slot): Vector2i? {
        val index = this.slotIndexOrNull(slot) ?: return null
        return Vector2i(0, index)
    }

    override fun instantiateView(): InventoryView<LanternInventoryColumn> = View(this)

    private class View(override val backing: LanternInventoryColumn) : LanternInventoryColumn(), InventoryView<LanternInventoryColumn> {

        init {
            this.init(this.backing.children().createViews(this).asInventories())
        }
    }
}
