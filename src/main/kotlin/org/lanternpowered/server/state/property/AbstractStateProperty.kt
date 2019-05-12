/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.state.property

import com.google.common.collect.ImmutableCollection
import com.google.common.collect.Iterables
import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.state.IStateProperty
import org.lanternpowered.server.state.StateKeyValueTransformer
import org.spongepowered.api.data.key.Key
import org.spongepowered.api.data.value.Value
import java.util.function.Predicate

abstract class AbstractStateProperty<T : Comparable<T>, V>(
        key: CatalogKey,
        private val valueClass: Class<T>,
        private val possibleValues: ImmutableCollection<T>,
        override val valueKey: Key<out Value<V>>,
        override val keyValueTransformer: StateKeyValueTransformer<T, V>
) : DefaultCatalogType(key), IStateProperty<T, V> {

    private val predicate = Predicate<T> { this.possibleValues.contains(it) }

    override fun getPossibleValues() = this.possibleValues
    override fun getValueClass() = this.valueClass
    override fun getPredicate() = this.predicate

    override fun toStringHelper() = super.toStringHelper()
            .add("valueClass", this.valueClass)
            .add("possibleValues", Iterables.toString(this.possibleValues))
}
