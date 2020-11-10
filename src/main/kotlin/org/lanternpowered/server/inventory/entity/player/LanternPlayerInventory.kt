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
package org.lanternpowered.server.inventory.entity.player

import org.lanternpowered.api.entity.player.BasePlayer
import org.lanternpowered.api.item.inventory.entity.ExtendedPlayerInventory
import org.lanternpowered.api.item.inventory.entity.ExtendedPrimaryPlayerInventory
import org.lanternpowered.api.item.inventory.equipment.ArmorEquipable
import org.lanternpowered.api.item.inventory.equipment.ExtendedEquipmentInventory
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.server.inventory.AbstractChildrenInventory
import org.lanternpowered.server.inventory.AbstractMutableInventory
import org.lanternpowered.server.inventory.AbstractSpongeCarriedInventory
import org.lanternpowered.server.inventory.InventoryView

class LanternPlayerInventory : AbstractChildrenInventory(), ExtendedPlayerInventory, AbstractSpongeCarriedInventory<BasePlayer> {

    override fun getArmor(): ExtendedEquipmentInventory<ArmorEquipable> {
        TODO("Not yet implemented")
    }

    override fun getEquipment(): ExtendedEquipmentInventory<ArmorEquipable> {
        TODO("Not yet implemented")
    }

    override fun getOffhand(): ExtendedSlot {
        TODO("Not yet implemented")
    }

    override fun getPrimary(): ExtendedPrimaryPlayerInventory {
        TODO("Not yet implemented")
    }

    override fun instantiateView(): InventoryView<AbstractMutableInventory> {
        TODO("Not yet implemented")
    }
}
