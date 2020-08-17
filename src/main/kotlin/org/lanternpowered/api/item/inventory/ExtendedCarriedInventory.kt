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

package org.lanternpowered.api.item.inventory

import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.type.CarriedInventory
import kotlin.contracts.contract

/**
 * Gets the normal carried inventory as an extended carried inventory.
 */
inline fun <C : Carrier> CarriedInventory<C>.fix(): ExtendedCarriedInventory<C> {
    contract { returns() implies (this@fix is ExtendedCarriedInventory) }
    return this as ExtendedCarriedInventory
}

/**
 * Gets the normal carried inventory as an extended carried inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun <C : Carrier> ExtendedCarriedInventory<C>.fix(): ExtendedCarriedInventory<C> = this

/**
 * Gets the carrier of this inventory.
 */
inline fun <C : Carrier> CarriedInventory<C>.carrier(): C? {
    contract { returns() implies (this@carrier is ExtendedCarriedInventory) }
    return (this as ExtendedCarriedInventory).carrier()
}

/**
 * An extended version of [CarriedInventory].
 */
interface ExtendedCarriedInventory<C : Carrier> : ExtendedInventory, CarriedInventory<C> {

    /**
     * Gets the carrier of this inventory.
     */
    fun carrier(): C?
}
