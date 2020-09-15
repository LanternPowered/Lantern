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

import org.lanternpowered.api.item.inventory.CarriedInventory
import org.lanternpowered.api.item.inventory.ExtendedInventory
import java.util.Optional
import kotlin.contracts.contract

typealias EquipmentInventory = org.spongepowered.api.item.inventory.equipment.EquipmentInventory

/**
 * Gets the normal equipment inventory as an extended equipment inventory.
 */
inline fun EquipmentInventory.fix(): ExtendedEquipmentInventory<Equipable> {
    contract { returns() implies (this@fix is ExtendedEquipmentInventory<*>) }
    @Suppress("UNCHECKED_CAST")
    return this as ExtendedEquipmentInventory<Equipable>
}

/**
 * Gets the normal equipment inventory as an extended equipment inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun <C : Equipable> ExtendedEquipmentInventory<C>.fix(): ExtendedEquipmentInventory<C> = this

/**
 * An extended version of [EquipmentInventory].
 */
interface ExtendedEquipmentInventory<C : Equipable> : ExtendedInventory, EquipmentInventory, CarriedInventory<C> {

    @Deprecated(message = "Prefer to use carrierOrNull()")
    override fun getCarrier(): Optional<Equipable>
}
