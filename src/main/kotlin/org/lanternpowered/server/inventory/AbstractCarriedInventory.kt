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

import org.lanternpowered.api.item.inventory.CarriedInventory
import org.lanternpowered.api.item.inventory.Carrier
import org.lanternpowered.api.item.inventory.ExtendedSpongeCarriedInventory
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.server.inventory.carrier.CarrierReference
import java.util.Optional

interface AbstractCarriedInventory<C : Any> : CarriedInventory<C> {

    @Suppress("UNCHECKED_CAST")
    override fun carrierOrNull(): C? =
            ((this as AbstractInventory).carrierReference as? CarrierReference<C>)?.get()
}

interface AbstractSpongeCarriedInventory<C : Carrier> : AbstractCarriedInventory<C>, ExtendedSpongeCarriedInventory<C> {

    override fun getCarrier(): Optional<C> = this.carrierOrNull().asOptional()
}
