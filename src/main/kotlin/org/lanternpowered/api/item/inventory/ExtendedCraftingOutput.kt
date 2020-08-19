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

import org.lanternpowered.api.item.inventory.crafting.CraftingOutput
import org.lanternpowered.api.item.inventory.crafting.ExtendedCraftingOutput
import kotlin.contracts.contract

/**
 * Gets the normal crafting output as an extended crafting output.
 */
inline fun CraftingOutput.fix(): ExtendedCraftingOutput {
    contract { returns() implies (this@fix is ExtendedCraftingOutput) }
    return this as ExtendedCraftingOutput
}

/**
 * Gets the normal crafting output as an extended crafting output.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedCraftingOutput.fix(): ExtendedCraftingOutput = this
