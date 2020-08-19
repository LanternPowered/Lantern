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

import org.lanternpowered.api.item.inventory.crafting.CraftingGridInventory
import org.lanternpowered.api.item.inventory.crafting.ExtendedCraftingGridInventory
import kotlin.contracts.contract

/**
 * Gets the normal crafting grid inventory as an extended crafting grid inventory.
 */
inline fun CraftingGridInventory.fix(): ExtendedCraftingGridInventory {
    contract { returns() implies (this@fix is ExtendedCraftingGridInventory) }
    return this as ExtendedCraftingGridInventory
}

/**
 * Gets the normal crafting grid inventory as an extended crafting grid inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedCraftingGridInventory.fix(): ExtendedCraftingGridInventory = this
