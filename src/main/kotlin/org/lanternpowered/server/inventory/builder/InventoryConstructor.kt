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
package org.lanternpowered.server.inventory.builder

import org.lanternpowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.Carrier

/**
 * A constructor of inventories.
 */
interface InventoryConstructor<T : Inventory> {

    /**
     * Constructs a new inventory with the given
     * parent inventory and carrier.
     */
    fun construct(parent: Inventory?, carrier: Carrier?): T
}
