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

import org.lanternpowered.api.util.uncheckedCast

fun <T : AbstractInventory> List<InventoryView<T>>.asInventories(): List<T> =
        this.uncheckedCast()

fun <T : AbstractInventory> InventoryView<T>.asInventory(): T = this.uncheckedCast()

fun <T : AbstractInventory> List<T>.createViews(parent: AbstractInventory?): List<InventoryView<T>> =
        this.map { inventory -> inventory.createView(parent) }

fun <T : AbstractInventory> T.createView(parent: AbstractInventory?): InventoryView<T> {
    if (this.parent == parent)
        return this.uncheckedCast()
    val view = this.original().instantiateView()
    (view as AbstractInventory).parent = parent
    return view.uncheckedCast()
}

fun <T : AbstractInventory> T.original(): T =
        if (this is InventoryView<*>) this.backing.uncheckedCast() else this

interface InventoryView<out T : AbstractInventory> {

    val backing: T
}
