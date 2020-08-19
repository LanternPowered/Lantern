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
package org.lanternpowered.server.inventory.slot

import org.lanternpowered.api.item.inventory.slot.FilteringSlot
import org.lanternpowered.server.inventory.InventoryView
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.inventory.ItemStack

open class LanternFilteringSlot : LanternSlot(), FilteringSlot {

    override fun isValidItem(stack: ItemStack): Boolean =
            this.filter?.test(stack) ?: true

    override fun isValidItem(type: ItemType): Boolean =
            this.filter?.test(type) ?: true

    override fun instantiateView(): InventoryView<LanternFilteringSlot> = View(this)

    private class View(override val backing: LanternFilteringSlot) : AbstractFilteringSlotView<LanternFilteringSlot>()
}
