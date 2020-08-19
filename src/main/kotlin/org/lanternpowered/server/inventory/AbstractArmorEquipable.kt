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

import org.lanternpowered.api.data.type.hand.getEquipmentType
import org.lanternpowered.api.item.inventory.emptyItemStack
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.item.inventory.ArmorEquipable
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes

interface AbstractArmorEquipable : AbstractEquipable, ArmorEquipable {

    @JvmDefault
    override fun getHead(): ItemStack =
            this.getEquipped(EquipmentTypes.HEAD).orElseGet(::emptyItemStack)

    @JvmDefault
    override fun setHead(helmet: ItemStack) {
        this.equip(EquipmentTypes.HEAD, helmet)
    }

    @JvmDefault
    override fun getChest(): ItemStack =
            this.getEquipped(EquipmentTypes.CHEST).orElseGet(::emptyItemStack)

    @JvmDefault
    override fun setChest(chestplate: ItemStack) {
        this.equip(EquipmentTypes.CHEST, chestplate)
    }

    @JvmDefault
    override fun getLegs(): ItemStack =
            this.getEquipped(EquipmentTypes.LEGS).orElseGet(::emptyItemStack)

    @JvmDefault
    override fun setLegs(leggings: ItemStack) {
        this.equip(EquipmentTypes.LEGS, leggings)
    }

    @JvmDefault
    override fun getFeet(): ItemStack =
            this.getEquipped(EquipmentTypes.FEET).orElseGet(::emptyItemStack)

    @JvmDefault
    override fun setFeet(boots: ItemStack) {
        this.equip(EquipmentTypes.FEET, boots)
    }

    @JvmDefault
    override fun getItemInHand(handType: HandType): ItemStack =
            this.getEquipped(handType.getEquipmentType()).orElseGet(::emptyItemStack)

    @JvmDefault
    override fun setItemInHand(handType: HandType, itemInHand: ItemStack) {
        this.equip(handType.getEquipmentType(), itemInHand)
    }
}
