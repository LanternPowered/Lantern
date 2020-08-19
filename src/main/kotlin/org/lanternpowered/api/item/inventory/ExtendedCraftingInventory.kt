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

import org.lanternpowered.api.item.inventory.crafting.CraftingInventory
import org.lanternpowered.api.item.inventory.crafting.ExtendedCraftingInventory
import kotlin.contracts.contract

/**
 * Gets the normal crafting inventory as an extended crafting inventory.
 */
inline fun CraftingInventory.fix(): ExtendedCraftingInventory {
    contract { returns() implies (this@fix is ExtendedCraftingInventory) }
    return this as ExtendedCraftingInventory
}

/**
 * Gets the normal crafting inventory as an extended crafting inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedCraftingInventory.fix(): ExtendedCraftingInventory = this
