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

import org.lanternpowered.api.item.inventory.entity.ExtendedPlayerInventory
import org.lanternpowered.api.item.inventory.entity.PlayerInventory
import kotlin.contracts.contract

/**
 * Gets the normal player inventory as an extended player inventory.
 */
inline fun PlayerInventory.fix(): ExtendedPlayerInventory {
    contract { returns() implies (this@fix is ExtendedPlayerInventory) }
    return this as ExtendedPlayerInventory
}

/**
 * Gets the normal player inventory as an extended player inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedPlayerInventory.fix(): ExtendedPlayerInventory = this
