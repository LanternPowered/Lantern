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
package org.lanternpowered.server.inventory.carrier

import org.lanternpowered.api.util.ToStringHelper
import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.type.CarriedInventory

abstract class AbstractCarrier<T : CarriedInventory<*>> : Carrier {

    private var inventory: T? = null

    fun setInventory(inventory: T) {
        this.inventory = inventory
    }

    override fun getInventory(): T = this.inventory ?: error("The inventory is not initialized yet")

    override fun toString(): String =
            this.toStringHelper().toString()

    protected open fun toStringHelper(): ToStringHelper = ToStringHelper(this)
}
