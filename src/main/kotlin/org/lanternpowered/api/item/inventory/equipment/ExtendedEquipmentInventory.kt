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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.item.inventory.equipment

import org.lanternpowered.api.item.inventory.ExtendedCarriedInventory

typealias EquipmentInventory = org.spongepowered.api.item.inventory.equipment.EquipmentInventory

/**
 * Gets the normal equipment inventory as an extended equipment inventory.
 */
inline fun EquipmentInventory.fix(): ExtendedEquipmentInventory {
    kotlin.contracts.contract { returns() implies (this@fix is ExtendedEquipmentInventory) }
    return this as ExtendedEquipmentInventory
}

/**
 * Gets the normal equipment inventory as an extended equipment inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedEquipmentInventory.fix(): ExtendedEquipmentInventory = this

/**
 * An extended version of [EquipmentInventory].
 */
interface ExtendedEquipmentInventory : ExtendedCarriedInventory<Equipable>, EquipmentInventory {

}
