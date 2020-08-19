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
package org.lanternpowered.server.inventory.crafting

import org.lanternpowered.api.item.inventory.crafting.ExtendedCraftingOutput
import org.lanternpowered.server.inventory.InventoryView
import org.lanternpowered.server.inventory.slot.AbstractOutputSlotView
import org.lanternpowered.server.inventory.slot.LanternOutputSlot

class LanternCraftingOutput : LanternOutputSlot(), ExtendedCraftingOutput {

    override fun instantiateView(): InventoryView<LanternCraftingOutput> = View(this)

    private class View(override val backing: LanternCraftingOutput) :
            AbstractOutputSlotView<LanternCraftingOutput>(), ExtendedCraftingOutput
}
