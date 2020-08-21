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

import org.lanternpowered.api.item.inventory.entity.ExtendedPrimaryPlayerInventory
import org.lanternpowered.api.item.inventory.entity.PrimaryPlayerInventory
import kotlin.contracts.contract

/**
 * Gets the normal primary player inventory as an extended primary player inventory.
 */
inline fun PrimaryPlayerInventory.fix(): ExtendedPrimaryPlayerInventory {
    contract { returns() implies (this@fix is ExtendedPrimaryPlayerInventory) }
    return this as ExtendedPrimaryPlayerInventory
}

/**
 * Gets the normal primary player inventory as an extended primary player inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedPrimaryPlayerInventory.fix(): ExtendedPrimaryPlayerInventory = this
