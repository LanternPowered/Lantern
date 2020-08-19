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
package org.lanternpowered.server.inventory.entity.hotbar

import org.lanternpowered.api.item.inventory.ExtendedInventory
import org.lanternpowered.api.item.inventory.hotbar.ExtendedHotbar
import org.lanternpowered.api.item.inventory.query.Query
import org.lanternpowered.api.item.inventory.query.QueryTypes
import org.lanternpowered.server.inventory.AbstractInventory
import org.lanternpowered.server.inventory.AbstractSlot
import org.lanternpowered.server.inventory.LanternChildrenInventory
import org.lanternpowered.server.inventory.LanternInventoryRow
import org.lanternpowered.server.inventory.behavior.HotbarBehavior
import org.lanternpowered.server.inventory.behavior.SimpleHotbarBehavior

class LanternHotbar : LanternInventoryRow(), ExtendedHotbar {

    /**
     * The behavior of this hotbar.
     */
    lateinit var behavior: HotbarBehavior

    /**
     * The slot that is currently selected.
     */
    val selectedSlot: AbstractSlot
        get() = this.slot(this.selectedSlotIndex) as AbstractSlot

    /**
     * Cached inventory where the selected slot has the highest
     * priority, followed by the hotbar.
     */
    private lateinit var prioritySelectedSlotAndHotbar: ExtendedInventory

    override fun init(children: List<AbstractInventory>) {
        super.init(children)
        this.init()
    }

    override fun init(children: List<AbstractInventory>, slots: List<AbstractSlot>) {
        super.init(children, slots)
        this.init()
    }

    private fun init() {
        this.behavior = SimpleHotbarBehavior(this.slots().size)
        val selectedHotbarSlotView = SelectedHotbarSlotView(this)
        this.prioritySelectedSlotAndHotbar = LanternChildrenInventory(listOf(selectedHotbarSlotView, this))
    }

    override fun getSelectedSlotIndex(): Int = this.behavior.selectedSlotIndex

    override fun setSelectedSlotIndex(index: Int) {
        if (index < 0 || index >= this.slots().size)
            throw IndexOutOfBoundsException(index)
        this.behavior.selectedSlotIndex = index
    }

    override fun query(query: Query): ExtendedInventory {
        if (query == QueryTypes.PRIORITY_HOTBAR)
            return this
        if (query == QueryTypes.PRIORITY_SELECTED_SLOT_AND_HOTBAR)
            return this.prioritySelectedSlotAndHotbar
        return super<LanternInventoryRow>.query(query)
    }
}
