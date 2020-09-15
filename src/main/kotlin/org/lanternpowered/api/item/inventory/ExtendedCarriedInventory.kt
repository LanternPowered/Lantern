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

import java.util.Optional
import kotlin.contracts.contract

typealias Carrier = org.spongepowered.api.item.inventory.Carrier
typealias SpongeCarriedInventory<C> = org.spongepowered.api.item.inventory.type.CarriedInventory<C>

/**
 * Gets the normal carried inventory as an extended carried inventory.
 */
inline fun <C : Carrier> SpongeCarriedInventory<C>.fix(): ExtendedSpongeCarriedInventory<C> {
    contract { returns() implies (this@fix is ExtendedSpongeCarriedInventory) }
    return this as ExtendedSpongeCarriedInventory
}

/**
 * Gets the normal carried inventory as an extended carried inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun <C : Carrier> ExtendedSpongeCarriedInventory<C>.fix(): ExtendedSpongeCarriedInventory<C> = this

/**
 * Gets the carrier of this inventory.
 */
inline fun <C : Carrier> SpongeCarriedInventory<C>.carrierOrNull(): C? {
    contract { returns() implies (this@carrierOrNull is ExtendedSpongeCarriedInventory) }
    return (this as ExtendedSpongeCarriedInventory).carrierOrNull()
}

/**
 * Represents an inventory carried by a carrier of type [C].
 */
interface CarriedInventory<C : Any> : ExtendedInventory {

    /**
     * Gets the carrier of this inventory. Returns
     * `null` if there is no carrier.
     */
    fun carrierOrNull(): C?
}

/**
 * An extended version of [SpongeCarriedInventory].
 */
interface ExtendedSpongeCarriedInventory<C : Carrier> : ExtendedInventory, CarriedInventory<C>, SpongeCarriedInventory<C> {

    @Deprecated(message = "Prefer to use carrierOrNull()")
    override fun getCarrier(): Optional<C>
}
