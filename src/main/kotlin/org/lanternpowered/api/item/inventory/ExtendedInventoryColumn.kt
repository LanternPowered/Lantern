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

typealias InventoryColumn = org.spongepowered.api.item.inventory.type.InventoryColumn

/**
 * Gets the normal column inventories as an extended column inventories.
 */
inline fun List<InventoryColumn>.fix(): List<ExtendedInventoryColumn> =
        this.uncheckedCast()

/**
 * Gets the normal column inventory as an extended column inventory.
 */
inline fun InventoryColumn.fix(): ExtendedInventoryColumn {
    contract { returns() implies (this@fix is ExtendedInventoryColumn) }
    return this as ExtendedInventoryColumn
}

/**
 * Gets the normal column inventory as an extended column inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedInventoryColumn.fix(): ExtendedInventoryColumn = this

/**
 * An extended version of [InventoryColumn].
 */
interface ExtendedInventoryColumn : InventoryColumn, ExtendedInventory2D {

    @Deprecated(message = "Is always 1 for columns.", replaceWith = ReplaceWith("1"))
    override val width: Int
        get() = 1
}
