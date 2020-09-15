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
package org.lanternpowered.server.inventory.equipment

import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.equipment.Equipable
import org.lanternpowered.api.item.inventory.equipment.EquipmentInventory
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.item.inventory.where
import org.lanternpowered.api.util.optional.asOptional
import org.spongepowered.api.data.Keys
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import java.util.Optional

interface AbstractEquipable : Equipable {

    override fun getEquipment(): EquipmentInventory

    fun slots(type: EquipmentType): List<ExtendedSlot> =
            this.equipment.slots().where { Keys.EQUIPMENT_TYPE eq type }.toList()

    override fun equip(type: EquipmentType, equipment: ItemStack): Boolean {
        for (slot in this.slots(type)) {
            if (!slot.canContain(equipment))
                continue
            slot.safeSetFast(equipment)
            return true
        }
        return false
    }

    override fun canEquip(type: EquipmentType): Boolean =
            this.slots(type).isNotEmpty()

    override fun canEquip(type: EquipmentType, equipment: ItemStack): Boolean =
            this.slots(type).any { slot -> slot.canContain(equipment) }

    override fun getEquipped(type: EquipmentType): Optional<ItemStack> =
            this.slots(type).asSequence().map { slot -> slot.peek() }.firstOrNull().asOptional()
}
