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

class LanternImmutableWeightedCollectionValue<E>(key: Key<out WeightedCollectionValue<E>>, value: WeightedTable<E>) :
        LanternCollectionValue.Immutable<TableEntry<E>, WeightedTable<E>, WeightedCollectionValue.Immutable<E>,
                WeightedCollectionValue.Mutable<E>>(key, value), WeightedCollectionValue.Immutable<E> {

    override fun get() = CopyHelper.copyWeightedTable(super.get())

    override fun getKey() = super.getKey().uncheckedCast<ValueKey<WeightedCollectionValue<E>, WeightedTable<E>>>()

    override fun withValue(value: WeightedTable<E>): WeightedCollectionValue.Immutable<E>
            = this.key.valueConstructor.getImmutable(value).asImmutable()

    override fun get(random: Random): List<E> = this.value.get(random)

    override fun asMutable() = LanternMutableWeightedCollectionValue(this.key, get())
}
