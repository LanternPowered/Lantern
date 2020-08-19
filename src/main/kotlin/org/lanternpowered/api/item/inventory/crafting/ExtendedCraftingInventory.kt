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
package org.lanternpowered.api.item.inventory.crafting

import org.lanternpowered.api.item.inventory.ExtendedInventory

typealias CraftingInventory = org.spongepowered.api.item.inventory.crafting.CraftingInventory

/**
 * An extended version of [CraftingInventory].
 */
interface ExtendedCraftingInventory : CraftingInventory, ExtendedInventory {

    /**
     * Gets the crafting grid.
     */
    override fun getCraftingGrid(): ExtendedCraftingGridInventory

    /**
     * Gets the crafting output slot.
     */
    fun getCraftingOutput(): ExtendedCraftingOutput

    @Deprecated(message = "Prefer to use getCraftingOutput()", replaceWith = ReplaceWith("this.getCraftingOutput()"))
    override fun getResult(): ExtendedCraftingOutput =
            this.getCraftingOutput()
}
