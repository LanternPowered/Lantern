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
package org.lanternpowered.server.inventory

open class LanternChildrenInventory : AbstractChildrenInventory {

    constructor(children: List<AbstractMutableInventory>) {
        this.init(children)
    }

    private constructor()

    override fun instantiateView(): InventoryView<LanternChildrenInventory> = View(this)

    private class View(
            override val backing: LanternChildrenInventory
    ) : LanternChildrenInventory(), InventoryView<LanternChildrenInventory> {

        init {
            this.init(this.backing.children().createViews(this).asInventories())
        }
    }
}
