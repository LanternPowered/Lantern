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

typealias ViewableInventory = org.spongepowered.api.item.inventory.type.ViewableInventory

/**
 * Gets the normal viewable inventory as an extended viewable inventory.
 */
inline fun ViewableInventory.fix(): ExtendedViewableInventory {
    contract { returns() implies (this@fix is ExtendedViewableInventory) }
    return this as ExtendedViewableInventory
}

/**
 * Gets the normal viewable inventory as an extended viewable inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedViewableInventory.fix(): ExtendedViewableInventory = this

/**
 * An extended version of [ViewableInventory].
 */
interface ExtendedViewableInventory : ViewableInventory, ExtendedInventory
