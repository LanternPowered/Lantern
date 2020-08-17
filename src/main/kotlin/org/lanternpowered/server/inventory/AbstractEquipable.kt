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

import org.lanternpowered.api.item.inventory.fix
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.util.optional.asOptional
import org.spongepowered.api.data.KeyValueMatcher
import org.spongepowered.api.data.Keys
import org.spongepowered.api.item.inventory.Equipable
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import java.util.Optional

interface AbstractEquipable : Equipable {

    fun slots(type: EquipmentType): List<ExtendedSlot> =
            this.inventory.query(KeyValueMatcher.of(Keys.EQUIPMENT_TYPE, type)).slots().fix()

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
