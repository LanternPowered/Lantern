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
package org.lanternpowered.server.inventory.crafting

import org.lanternpowered.api.item.inventory.crafting.ExtendedCraftingGridInventory
import org.lanternpowered.api.item.inventory.crafting.ExtendedCraftingInventory
import org.lanternpowered.api.item.inventory.crafting.ExtendedCraftingOutput
import org.lanternpowered.api.item.inventory.query
import org.lanternpowered.server.inventory.AbstractChildrenInventory
import org.lanternpowered.server.inventory.AbstractInventory
import org.lanternpowered.server.inventory.InventoryView
import org.lanternpowered.server.inventory.asInventories
import org.lanternpowered.server.inventory.createViews

open class LanternCraftingInventory : AbstractChildrenInventory(), ExtendedCraftingInventory {

    private lateinit var craftingGrid: ExtendedCraftingGridInventory
    private lateinit var craftingOutput: ExtendedCraftingOutput

    override fun init(children: List<AbstractInventory>) {
        super.init(children)

        this.craftingGrid = this.query<ExtendedCraftingGridInventory>().first()
        this.craftingOutput = this.query<ExtendedCraftingOutput>().first()
    }

    override fun getCraftingGrid(): ExtendedCraftingGridInventory = this.craftingGrid
    override fun getCraftingOutput(): ExtendedCraftingOutput = this.craftingOutput

    override fun instantiateView(): InventoryView<LanternCraftingInventory> = View(this)

    private class View(
            override val backing: LanternCraftingInventory
    ) : LanternCraftingInventory(), InventoryView<LanternCraftingInventory> {

        init {
            this.init(this.backing.children().createViews(this).asInventories())
        }
    }
}
