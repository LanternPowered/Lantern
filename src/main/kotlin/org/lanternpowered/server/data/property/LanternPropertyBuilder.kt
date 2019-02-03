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
package org.lanternpowered.server.data.property

import com.google.common.reflect.TypeToken
import org.lanternpowered.server.catalog.AbstractCatalogBuilder
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.CatalogType
import org.spongepowered.api.data.property.Property
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import org.spongepowered.api.text.translation.Translation
import java.util.Comparator
import java.util.function.BiPredicate

@Suppress("UNCHECKED_CAST")
class LanternPropertyBuilder<V> : AbstractCatalogBuilder<Property<V>, Property.Builder<V>>(), Property.Builder<V> {

    private var valueType: TypeToken<V>? = null
    private var valueComparator: Comparator<V>? = null
    private var includesTester: BiPredicate<V, V>? = null

    override fun <NV> valueType(typeToken: TypeToken<NV>) = apply {
        this.valueType = typeToken as TypeToken<V>
        this.valueComparator = null
    } as LanternPropertyBuilder<NV>

    override fun valueComparator(comparator: Comparator<V>) = apply { this.valueComparator = comparator }
    override fun valueIncludesTester(predicate: BiPredicate<V, V>) = apply { this.includesTester = predicate }

    override fun build(key: CatalogKey, name: Translation): Property<V> {
        val valueType = checkNotNull(this.valueType) { "The value type must be set" }
        val raw = valueType.rawType

        val valueComparator = this.valueComparator ?: run {
            when {
                Comparable::class.java.isAssignableFrom(raw) -> Comparator(Comparable<*>::compareTo) as Comparator<V>
                CatalogType::class.java.isAssignableFrom(raw) -> Comparator.comparing<V, CatalogKey> { o -> (o as CatalogType).key }
                else -> Comparator.comparingInt { it.hashCode() }
            }
        }
        val includesTester = this.includesTester ?: run {
            when {
                Collection::class.java.isAssignableFrom(raw) -> BiPredicate { o1, o2 -> (o1 as Collection<*>).containsAll(o2 as Collection<*>) }
                EquipmentType::class.java.isAssignableFrom(raw) -> BiPredicate { o1, o2 -> (o1 as EquipmentType).includes(o2 as EquipmentType) }
                else -> BiPredicate<V, V> { _, _ -> false }
            }
        }
        return LanternProperty(key, valueType, valueComparator!!, includesTester)
    }

    override fun from(value: Property<V>): Property.Builder<V> {
        throw UnsupportedOperationException()
    }

    override fun reset() = apply {
        super.reset()
        this.valueType = null
        this.valueComparator = null
        this.includesTester = null
    }
}
