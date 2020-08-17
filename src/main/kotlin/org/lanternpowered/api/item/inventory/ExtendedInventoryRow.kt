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

import org.lanternpowered.api.util.uncheckedCast
import kotlin.contracts.contract

typealias InventoryRow = org.spongepowered.api.item.inventory.type.InventoryRow

/**
 * Gets the normal row inventories as an extended row inventories.
 */
inline fun List<InventoryRow>.fix(): List<ExtendedInventoryRow> =
        this.uncheckedCast()

/**
 * Gets the normal row inventory as an extended row inventory.
 */
inline fun InventoryRow.fix(): ExtendedInventoryRow {
    contract { returns() implies (this@fix is ExtendedInventoryRow) }
    return this as ExtendedInventoryRow
}

/**
 * Gets the normal row inventory as an extended row inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedInventoryRow.fix(): ExtendedInventoryRow = this

/**
 * An extended version of [InventoryRow].
 */
interface ExtendedInventoryRow : InventoryRow, ExtendedInventory2D {

    @Deprecated(message = "Is always 1 for rows.", replaceWith = ReplaceWith("1"))
    override val height: Int
        get() = 1
}
