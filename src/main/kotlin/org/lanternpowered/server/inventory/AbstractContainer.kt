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

import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.item.inventory.container.ExtendedContainer
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.slot.Slot
import org.spongepowered.api.item.inventory.ContainerType
import java.util.Optional

class AbstractContainer : AbstractChildrenInventory(), ExtendedContainer {

    override fun getType(): ContainerType {
        TODO("Not yet implemented")
    }

    override fun setCursor(item: ItemStack): Boolean {
        TODO("Not yet implemented")
    }

    override fun getCursor(): Optional<ItemStack> {
        TODO("Not yet implemented")
    }

    override fun isOpen(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getViewer(): Player {
        TODO("Not yet implemented")
    }

    override fun getViewed(): List<Inventory> {
        TODO("Not yet implemented")
    }

    override fun isViewedSlot(slot: Slot): Boolean {
        TODO("Not yet implemented")
    }

    override fun instantiateView(): InventoryView<AbstractContainer> {
        TODO("Not yet implemented")
    }
}
