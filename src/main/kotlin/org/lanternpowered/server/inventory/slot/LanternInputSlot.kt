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

import org.lanternpowered.api.item.inventory.slot.InputSlot
import org.lanternpowered.server.inventory.InventoryView

class LanternInputSlot : LanternFilteringSlot(), InputSlot {

    override fun instantiateView(): InventoryView<LanternInputSlot> = View(this)

    private class View(override val backing: LanternInputSlot) : AbstractInputSlotView<LanternInputSlot>()
}
