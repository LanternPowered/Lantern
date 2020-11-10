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

import org.lanternpowered.api.data.type.hand.getEquipmentType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.emptyItemStack
import org.lanternpowered.api.item.inventory.equipment.ArmorEquipable
import org.lanternpowered.api.item.inventory.equipment.EquipmentInventory
import org.lanternpowered.api.item.inventory.equipment.EquipmentTypes
import org.spongepowered.api.data.type.HandType

interface AbstractArmorEquipable : AbstractEquipable, ArmorEquipable {

    override fun getEquipment(): EquipmentInventory

    override fun getHead(): ItemStack =
            this.getEquipped(EquipmentTypes.HEAD).orElseGet(::emptyItemStack)

    override fun setHead(helmet: ItemStack) {
        this.equip(EquipmentTypes.HEAD, helmet)
    }

    override fun getChest(): ItemStack =
            this.getEquipped(EquipmentTypes.CHEST).orElseGet(::emptyItemStack)

    override fun setChest(chestplate: ItemStack) {
        this.equip(EquipmentTypes.CHEST, chestplate)
    }

    override fun getLegs(): ItemStack =
            this.getEquipped(EquipmentTypes.LEGS).orElseGet(::emptyItemStack)

    override fun setLegs(leggings: ItemStack) {
        this.equip(EquipmentTypes.LEGS, leggings)
    }

    override fun getFeet(): ItemStack =
            this.getEquipped(EquipmentTypes.FEET).orElseGet(::emptyItemStack)

    override fun setFeet(boots: ItemStack) {
        this.equip(EquipmentTypes.FEET, boots)
    }

    override fun getItemInHand(handType: HandType): ItemStack =
            this.getEquipped(handType.getEquipmentType()).orElseGet(::emptyItemStack)

    override fun setItemInHand(handType: HandType, itemInHand: ItemStack) {
        this.equip(handType.getEquipmentType(), itemInHand)
    }
}
