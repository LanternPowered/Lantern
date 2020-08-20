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

import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.query.InventoryFilterBuilder
import org.lanternpowered.server.inventory.query.LanternInventoryFilterBuilder

object LanternInventoryFilterBuilderFactory : InventoryFilterBuilder.Factory {

    override fun <I : Inventory> of(): InventoryFilterBuilder<I> = LanternInventoryFilterBuilder()
}
