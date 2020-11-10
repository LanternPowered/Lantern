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

import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.slot.ExtendedFilteringSlot
import org.lanternpowered.server.inventory.InventoryView

open class LanternFilteringSlot : LanternSlot(), ExtendedFilteringSlot {

    override fun isValidItem(stack: ItemStack): Boolean =
            this.filter?.invoke(stack) ?: true

    override fun isValidItem(type: ItemType): Boolean =
            this.filter?.invoke(type) ?: true

    override fun instantiateView(): InventoryView<LanternFilteringSlot> = View(this)

    private class View(override val backing: LanternFilteringSlot) : AbstractFilteringSlotView<LanternFilteringSlot>()
}
