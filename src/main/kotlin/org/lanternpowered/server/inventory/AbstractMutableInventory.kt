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

abstract class AbstractMutableInventory : AbstractInventory() {

    abstract override fun instantiateView(): InventoryView<AbstractMutableInventory>

    private val empty by lazy {
        LanternEmptyInventory().also { inventory -> inventory.parent = this }
    }

    final override fun empty(): LanternEmptyInventory = this.empty
}
