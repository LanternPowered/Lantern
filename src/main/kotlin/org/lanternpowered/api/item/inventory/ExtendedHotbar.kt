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

import org.lanternpowered.api.item.inventory.hotbar.ExtendedHotbar
import org.lanternpowered.api.item.inventory.hotbar.Hotbar
import kotlin.contracts.contract

/**
 * Gets the normal hotbar as an extended hotbar.
 */
inline fun Hotbar.fix(): ExtendedHotbar {
    contract { returns() implies (this@fix is ExtendedHotbar) }
    return this as ExtendedHotbar
}

/**
 * Gets the normal hotbar as an extended hotbar.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedHotbar.fix(): ExtendedHotbar = this
