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
import org.lanternpowered.api.world.Location
import org.lanternpowered.server.inventory.LanternEmptyCarriedInventory
import org.spongepowered.api.item.inventory.BlockCarrier
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.type.CarriedInventory
import org.spongepowered.api.util.Direction
import org.spongepowered.api.world.Locatable

class LanternBlockCarrier<T : CarriedInventory<*>>(
        private val location: Location
) : AbstractCarrier<T>(), Locatable, BlockCarrier {

    override fun getLocation(): Location = this.location

    override fun getInventory(from: Direction): Inventory =
            LanternEmptyCarriedInventory(this)

    override fun toStringHelper(): ToStringHelper = super.toStringHelper()
            .add("location", this.location)
}
