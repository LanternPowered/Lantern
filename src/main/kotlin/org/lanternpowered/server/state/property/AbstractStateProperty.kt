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
package org.lanternpowered.server.state.property

import com.google.common.collect.ImmutableCollection
import com.google.common.collect.Iterables
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.state.IStateProperty
import org.lanternpowered.server.state.StateKeyValueTransformer
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.function.Predicate

abstract class AbstractStateProperty<T : Comparable<T>, V>(
        key: NamespacedKey,
        private val valueClass: Class<T>,
        private val possibleValues: ImmutableCollection<T>,
        override val valueKey: Key<out Value<V>>,
        override val keyValueTransformer: StateKeyValueTransformer<T, V>
) : DefaultCatalogType(key), IStateProperty<T, V> {

    private val predicate = Predicate<T> { this.possibleValues.contains(it) }

    override fun getName(): String = this.key.value
    override fun getPossibleValues() = this.possibleValues
    override fun getValueClass() = this.valueClass
    override fun getPredicate() = this.predicate

    override fun toStringHelper() = super.toStringHelper()
            .add("valueClass", this.valueClass)
            .add("possibleValues", Iterables.toString(this.possibleValues))
}
