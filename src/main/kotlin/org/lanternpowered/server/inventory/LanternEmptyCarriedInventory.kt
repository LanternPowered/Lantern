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

import org.spongepowered.api.item.inventory.Carrier

open class LanternEmptyCarriedInventory<C : Carrier>(carrier: C?) : LanternEmptyInventory(), AbstractCarriedInventory<C> {

    init {
        this.setCarrier(carrier)
    }

    override fun instantiateView(): InventoryView<LanternEmptyCarriedInventory<C>> = View(this)

    private class View<C : Carrier>(override val backing: LanternEmptyCarriedInventory<C>) :
            LanternEmptyCarriedInventory<C>(backing.carrierOrNull()), InventoryView<LanternEmptyCarriedInventory<C>>
}
