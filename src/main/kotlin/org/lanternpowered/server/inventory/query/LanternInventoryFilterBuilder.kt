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
package org.lanternpowered.server.inventory.query

import org.lanternpowered.api.data.Key
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.query.InventoryFilter
import org.lanternpowered.api.item.inventory.query.InventoryFilterBuilder
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.value.Value

class LanternInventoryFilterBuilder<I : Inventory> : InventoryFilterBuilder<I> {

    override fun <V : Any> Key<out Value<V>>.eq(value: V?): InventoryFilter<I> =
            { inventory -> compare(inventory, this, value) == 0 }

    override fun <V : Any> Key<out Value<V>>.neq(value: V?): InventoryFilter<I> =
            { inventory -> compare(inventory, this, value) != 0 }

    override fun <V : Any> Key<out Value<V>>.greater(value: V?): InventoryFilter<I> =
            { inventory -> compare(inventory, this, value) > 0 }

    override fun <V : Any> Key<out Value<V>>.greaterEq(value: V?): InventoryFilter<I> =
            { inventory -> compare(inventory, this, value) >= 0 }

    override fun <V : Any> Key<out Value<V>>.less(value: V?): InventoryFilter<I> =
            { inventory -> compare(inventory, this, value) < 0 }

    override fun <V : Any> Key<out Value<V>>.lessEq(value: V?): InventoryFilter<I> =
            { inventory -> compare(inventory, this, value) <= 0 }

    override fun InventoryFilter<I>.and(filter: InventoryFilter<I>): InventoryFilter<I> =
            { inventory -> this(inventory) && filter(inventory) }

    override fun InventoryFilter<I>.or(filter: InventoryFilter<I>): InventoryFilter<I> =
            { inventory -> this(inventory) || filter(inventory) }

    private fun <V> compare(inventory: Inventory, key: Key<out Value<V>>, value: V?): Int {
        val thatValue = inventory.get(key).orNull()
        return when {
            value == null && thatValue == null -> 0
            value != null && thatValue == null -> 1
            value == null -> -1
            else -> {
                val elementComparator: Comparator<V> = key.elementComparator.uncheckedCast()
                -elementComparator.compare(value, thatValue)
            }
        }
    }
}
