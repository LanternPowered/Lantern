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

import org.lanternpowered.api.item.inventory.ExtendedInventory
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.uncheckedCast

object LanternInventoryFactory : ExtendedInventory.Factory {

    override fun union(inventories: Iterable<Inventory>): ExtendedInventory =
            LanternChildrenInventory(inventories.toImmutableList().uncheckedCast())

    override fun union(inventories: Sequence<Inventory>): ExtendedInventory =
            LanternChildrenInventory(inventories.toImmutableList().uncheckedCast())
}
