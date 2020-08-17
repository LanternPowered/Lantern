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

import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.util.uncheckedCast
import kotlin.contracts.contract

/**
 * Gets the normal slots as an extended slots.
 */
inline fun List<Slot>.fix(): List<ExtendedSlot> =
        this.uncheckedCast()

/**
 * Gets the normal slot as an extended slot.
 */
inline fun Slot.fix(): ExtendedSlot {
    contract { returns() implies (this@fix is ExtendedSlot) }
    return this as ExtendedSlot
}

/**
 * Gets the slot inventory as an extended slot.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedSlot.fix(): ExtendedSlot = this
