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

import org.lanternpowered.api.item.inventory.InventoryTransactionResult
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.PollInventoryTransactionResult
import org.lanternpowered.api.item.inventory.equipment.Equipable
import org.lanternpowered.api.item.inventory.equipment.ExtendedEquipmentInventory
import org.lanternpowered.api.item.inventory.join
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.item.inventory.slot.Slot
import org.lanternpowered.api.item.inventory.where
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.server.inventory.AbstractCarriedInventory
import org.lanternpowered.server.inventory.InventoryTransactionResults
import org.spongepowered.api.data.Keys
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import java.util.Optional

interface AbstractEquipmentInventory<C : Equipable> : AbstractCarriedInventory<C>, ExtendedEquipmentInventory<C> {

    override fun getCarrier(): Optional<Equipable> = this.carrierOrNull().asOptional()

    fun slots(type: EquipmentType): Sequence<ExtendedSlot> =
            this.slots().asSequence().where { Keys.EQUIPMENT_TYPE eq type }

    override fun poll(type: EquipmentType): PollInventoryTransactionResult =
            this.slots(type).join().poll()

    override fun poll(type: EquipmentType, limit: Int): PollInventoryTransactionResult =
            this.slots(type).join().poll(limit)

    override fun peek(type: EquipmentType): Optional<ItemStack> {
        val slots = this.slots(type).toList()
        if (slots.isEmpty())
            return emptyOptional()
        return slots.join().peek().asOptional()
    }

    fun peek(type: EquipmentType, limit: Int): Optional<ItemStack> {
        val slots = this.slots(type).toList()
        if (slots.isEmpty())
            return emptyOptional()
        return slots.join().peek(limit).asOptional()
    }

    override fun set(type: EquipmentType, stack: ItemStack): InventoryTransactionResult {
        val slots = this.slots(type).toList()
        if (slots.isEmpty())
            return InventoryTransactionResults.rejectNoSlot(stack)
        return slots.asSequence()
                .map { slot -> slot.forceSet(stack) }
                .filter { result -> result.slotTransactions.isNotEmpty() } // Was set or partially set
                .firstOrNull() ?: InventoryTransactionResults.reject(stack)
    }

    override fun getSlot(type: EquipmentType): Optional<Slot> =
            this.slots(type).firstOrNull().asOptional()
}
