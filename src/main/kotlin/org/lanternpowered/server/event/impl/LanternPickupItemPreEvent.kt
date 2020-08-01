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
package org.lanternpowered.server.event.impl

import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.util.optional.asOptional
import org.spongepowered.api.data.Keys
import org.spongepowered.api.entity.Item
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import java.util.Optional

class LanternPickupItemPreEvent(
        cause: Cause,
        private val inventory: Inventory,
        private val item: Item
) : CancellableEvent(cause), ChangeInventoryEvent.Pickup.Pre {

    private var custom: List<ItemStackSnapshot>? = null

    override fun getInventory(): Inventory = this.inventory
    override fun getItem(): Item = this.item
    override fun getOriginalStack(): ItemStackSnapshot = this.item.get(Keys.ITEM_STACK_SNAPSHOT).orElse(ItemStackSnapshot.empty())

    override fun getFinal(): List<ItemStackSnapshot> {
        val custom = this.custom
        if (custom != null)
            return custom
        return listOf(this.originalStack)
    }

    override fun getCustom(): Optional<List<ItemStackSnapshot>> = this.custom.asOptional()
    override fun setCustom(items: List<ItemStackSnapshot>) { this.custom = items }
}
