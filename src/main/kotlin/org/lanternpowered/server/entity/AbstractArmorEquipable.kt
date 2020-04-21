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
package org.lanternpowered.server.entity

import org.lanternpowered.api.data.type.hand.getEquipmentType
import org.lanternpowered.server.game.registry.type.item.inventory.equipment.EquipmentTypeRegistryModule
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.item.inventory.ArmorEquipable
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes

interface AbstractArmorEquipable : AbstractEquipable, ArmorEquipable {

    @JvmDefault
    override fun getHelmet(): ItemStack = getEquipped(EquipmentTypes.HEADWEAR).orElseGet { ItemStack.empty() }

    @JvmDefault
    override fun setHelmet(helmet: ItemStack) {
        equip(EquipmentTypes.HEADWEAR, helmet)
    }

    @JvmDefault
    override fun getChestplate(): ItemStack = getEquipped(EquipmentTypes.CHESTPLATE).orElseGet { ItemStack.empty() }

    @JvmDefault
    override fun setChestplate(chestplate: ItemStack) {
        equip(EquipmentTypes.CHESTPLATE, chestplate)
    }

    @JvmDefault
    override fun getLeggings(): ItemStack = getEquipped(EquipmentTypes.LEGGINGS).orElseGet { ItemStack.empty() }

    @JvmDefault
    override fun setLeggings(leggings: ItemStack) {
        equip(EquipmentTypes.LEGGINGS, leggings)
    }

    @JvmDefault
    override fun getBoots(): ItemStack = getEquipped(EquipmentTypes.BOOTS).orElseGet { ItemStack.empty() }

    @JvmDefault
    override fun setBoots(boots: ItemStack) {
        equip(EquipmentTypes.BOOTS, boots)
    }

    @JvmDefault
    override fun getItemInHand(handType: HandType): ItemStack =
            getEquipped(handType.getEquipmentType()).orElseGet { ItemStack.empty() }

    @JvmDefault
    override fun setItemInHand(handType: HandType, itemInHand: ItemStack) {
        equip(handType.getEquipmentType(), itemInHand)
    }
}
