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

import kotlin.contracts.contract

typealias EmptyInventory = org.spongepowered.api.item.inventory.EmptyInventory

/**
 * Gets the normal empty inventory as an extended empty inventory.
 */
inline fun EmptyInventory.fix(): ExtendedEmptyInventory {
    contract { returns() implies (this@fix is ExtendedEmptyInventory) }
    return this as ExtendedEmptyInventory
}

/**
 * Gets the normal empty inventory as an extended empty inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedEmptyInventory.fix(): ExtendedEmptyInventory = this

/**
 * An extended version of [EmptyInventory].
 */
interface ExtendedEmptyInventory : EmptyInventory, ExtendedInventory
