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

import org.lanternpowered.api.item.inventory.entity.ExtendedStandardInventory
import org.lanternpowered.api.item.inventory.entity.StandardInventory
import kotlin.contracts.contract

/**
 * Gets the normal standard inventory as an extended standard inventory.
 */
inline fun StandardInventory.fix(): ExtendedStandardInventory {
    contract { returns() implies (this@fix is ExtendedStandardInventory) }
    return this as ExtendedStandardInventory
}

/**
 * Gets the normal standard inventory as an extended standard inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedStandardInventory.fix(): ExtendedStandardInventory = this
