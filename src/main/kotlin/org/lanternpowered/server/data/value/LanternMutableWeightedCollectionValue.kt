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
package org.lanternpowered.server.data.value

import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.data.key.ValueKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.WeightedCollectionValue
import org.spongepowered.api.util.weighted.TableEntry
import org.spongepowered.api.util.weighted.WeightedTable
import java.util.Random

class LanternMutableWeightedCollectionValue<E>(key: Key<out WeightedCollectionValue<E>>, value: WeightedTable<E>)
    : LanternCollectionValue.Mutable<TableEntry<E>, WeightedTable<E>, WeightedCollectionValue.Mutable<E>,
        WeightedCollectionValue.Immutable<E>>(key, value), WeightedCollectionValue.Mutable<E> {

    override fun getKey() = super.getKey().uncheckedCast<ValueKey<WeightedCollectionValue<E>, WeightedTable<E>>>()

    override fun copy() = LanternMutableWeightedCollectionValue(this.key, CopyHelper.copyWeightedTable(this.value))

    override fun get(random: Random): List<E> = this.value.get(random)

    override fun asImmutable(): WeightedCollectionValue.Immutable<E> = this.key.valueConstructor.getImmutable(this.value).asImmutable()
}
