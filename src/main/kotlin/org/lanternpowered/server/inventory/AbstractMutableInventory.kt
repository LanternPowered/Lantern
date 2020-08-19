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

import org.lanternpowered.api.item.inventory.ExtendedInventory
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.util.uncheckedCast

abstract class AbstractMutableInventory : AbstractInventory() {

    abstract override fun instantiateView(): InventoryView<AbstractMutableInventory>

    private val empty by lazy {
        LanternEmptyInventory().also { inventory -> inventory.parent = this }
    }

    final override fun empty(): LanternEmptyInventory = this.empty

    // TODO: Consider wrapped slots?

    final override fun intersect(inventory: Inventory): ExtendedInventory {
        if (inventory == this || this.slots().isEmpty())
            return this.empty()
        inventory as AbstractInventory
        val slots = inventory.slots()
        if (slots.isEmpty())
            return this.empty()
        val intersectedSlots = slots.toMutableList()
        intersectedSlots.retainAll(this.slots())
        if (intersectedSlots.isEmpty())
            return this.empty()
        return LanternChildrenInventory(intersectedSlots.uncheckedCast())
    }

    final override fun union(inventory: Inventory): ExtendedInventory {
        if (inventory == this)
            return this
        inventory as AbstractInventory
        val slotsThis = this.slots()
        if (slotsThis.isEmpty())
            return inventory
        val slotsThat = inventory.slots()
        if (slotsThat.isEmpty())
            return this
        val unionSlots = slotsThat.toMutableList()
        // Add the slots of this inventory before that inventory
        unionSlots.removeAll(slotsThis)
        unionSlots.addAll(0, slotsThis)
        return LanternChildrenInventory(unionSlots.uncheckedCast())
    }
}
