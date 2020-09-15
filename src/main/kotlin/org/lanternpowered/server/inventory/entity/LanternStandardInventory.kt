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
package org.lanternpowered.server.inventory.entity

import org.lanternpowered.api.data.eq
import org.lanternpowered.api.item.inventory.entity.ExtendedPrimaryPlayerInventory
import org.lanternpowered.api.item.inventory.entity.ExtendedStandardInventory
import org.lanternpowered.api.item.inventory.equipment.ExtendedEquipmentInventory
import org.lanternpowered.api.item.inventory.query
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.item.inventory.slot.Slot
import org.lanternpowered.api.item.inventory.where
import org.lanternpowered.api.util.collections.contentEquals
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.inventory.AbstractChildrenInventory
import org.lanternpowered.server.inventory.AbstractInventory
import org.lanternpowered.server.inventory.AbstractMutableInventory
import org.lanternpowered.server.inventory.AbstractSlot
import org.lanternpowered.server.inventory.InventoryView
import org.lanternpowered.server.inventory.equipment.LanternEquipmentInventory
import org.spongepowered.api.data.Keys
import org.spongepowered.api.item.inventory.ArmorEquipable
import org.spongepowered.api.item.inventory.equipment.EquipmentGroups
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes

class LanternStandardInventory : AbstractChildrenInventory(), ExtendedStandardInventory {

    private lateinit var armor: ExtendedEquipmentInventory<ArmorEquipable>
    private lateinit var primary: ExtendedPrimaryPlayerInventory
    private lateinit var offhand: ExtendedSlot

    override fun init(children: List<AbstractInventory>) {
        super.init(children)
        this.init()
    }

    override fun init(children: List<AbstractInventory>, slots: List<AbstractSlot>) {
        super.init(children, slots)
        this.init()
    }

    private fun init() {
        this.primary = this.query<ExtendedPrimaryPlayerInventory>().first()
        this.offhand = this.slots().where { Keys.EQUIPMENT_TYPE eq EquipmentTypes.OFF_HAND }.first()
        val armorSlotsFilter: (Slot) -> Boolean = { slot -> slot.get(Keys.EQUIPMENT_TYPE).orNull()?.group eq EquipmentGroups.WORN }
        val armorSlots = this.slots().filter(armorSlotsFilter)
        // Find a dedicated inventory for armor, if there is any
        var armor = this.query<ExtendedEquipmentInventory<ArmorEquipable>>()
                .filter { inventory -> inventory.slots().contentEquals(armorSlots) }
                .firstOrNull()
        if (armor == null) {
            armor = LanternEquipmentInventory(armorSlots)
        }
        this.armor = armor
    }

    override fun getPrimary(): ExtendedPrimaryPlayerInventory = this.primary
    override fun getArmor(): ExtendedEquipmentInventory<ArmorEquipable> = this.armor
    override fun getOffhand(): ExtendedSlot = this.offhand

    override fun getEquipment(): ExtendedEquipmentInventory<ArmorEquipable> {
        TODO("Not yet implemented")
    }

    override fun instantiateView(): InventoryView<AbstractMutableInventory> {
        TODO("Not yet implemented")
    }
}
