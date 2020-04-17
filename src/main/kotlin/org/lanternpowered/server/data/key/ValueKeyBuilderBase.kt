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
package org.lanternpowered.server.data.key

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.ext.uncheckedCast
import org.lanternpowered.api.util.builder.BaseBuilder
import org.lanternpowered.server.catalog.AbstractCatalogBuilder
import org.lanternpowered.server.data.value.CopyHelper
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.ListValue
import org.spongepowered.api.data.value.SetValue
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.WeightedCollectionValue
import org.spongepowered.api.util.weighted.WeightedTable
import java.lang.Class
import java.util.ArrayList
import java.util.HashSet
import java.util.function.BiPredicate

open class ValueKeyBuilderBase<E : Any, V : Value<E>, B : R, R : BaseBuilder<Key<V>, R>> : AbstractCatalogBuilder<Key<V>, R>() {

    private var valueType: TypeToken<V>? = null

    private var minValueSupplier: (() -> E)? = null
    private var maxValueSupplier: (() -> E)? = null
    private var comparator: (Comparator<in E>)? = null
    private var includesTester: BiPredicate<in E, in E>? = null

    private var requiresExplicitRegistration = false

    protected fun setType(type: TypeToken<V>) {
        this.valueType = type
    }

    override fun key(key: CatalogKey): B = apply { super.key(key) }.uncheckedCast()

    protected fun setMinValue(minValue: E) = setMinValueSupplier { CopyHelper.copy(minValue) }
    protected fun setMinValueSupplier(supplier: () -> E) { this.minValueSupplier = supplier }

    protected fun setMaxValue(maxValue: E) = setMaxValueSupplier { CopyHelper.copy(maxValue) }
    protected fun setMaxValueSupplier(supplier: () -> E) { this.maxValueSupplier = supplier }

    protected fun setComparator(comparator: Comparator<in E>) { this.comparator = comparator }
    protected fun setIncludesTester(includesTester: BiPredicate<in E, in E>?) { this.includesTester = includesTester }

    protected fun setRequireExplicitRegistration() { this.requiresExplicitRegistration = true }

    override fun build(key: CatalogKey): Key<V> {
        val valueType = checkNotNull(this.valueType) { "The type must be set" }
        val elementType = valueType.resolveType(elementParameter).uncheckedCast<TypeToken<E>>()
        val comparator: Comparator<in E> = this.comparator ?: run {
            if (elementType.isSubtypeOf(Comparable::class.java)) {
                comparableComparator.uncheckedCast()
            } else {
                Comparator { o1: E, o2: E ->
                    if (o1 == o2) 0 else if (o1.hashCode() > o2.hashCode()) 1 else -1
                }
            }
        }
        val includesTester: BiPredicate<in E, in E> = this.includesTester ?: BiPredicate { _, _ -> false }

        var defaultElementSupplier: () -> E? = { null }
        when {
            BoundedValue::class.java.isAssignableFrom(valueType.rawType) -> {
                var minValueSupplier = this.minValueSupplier
                var maxValueSupplier = this.maxValueSupplier
                @Suppress("UNCHECKED_CAST")
                val bounds = defaultBounds[elementType.rawType] as? Pair<E, E>
                if (minValueSupplier == null && bounds != null) {
                    val minimum: E = bounds.first
                    minValueSupplier = { minimum }
                }
                if (maxValueSupplier == null && bounds != null) {
                    val maximum: E = bounds.second
                    maxValueSupplier = { maximum }
                }
                checkNotNull(minValueSupplier) { "The minimum value supplier must be set" }
                checkNotNull(maxValueSupplier) { "The maximum value supplier must be set" }
                return BoundedValueKey(key.uncheckedCast(), valueType.uncheckedCast(), elementType.uncheckedCast(),
                        comparator, includesTester, defaultElementSupplier, this.requiresExplicitRegistration,
                        minValueSupplier, maxValueSupplier).uncheckedCast()
            }
            ListValue::class.java.isAssignableFrom(valueType.rawType) -> {
                defaultElementSupplier = { ArrayList<Any?>().uncheckedCast() }
            }
            SetValue::class.java.isAssignableFrom(valueType.rawType) -> {
                defaultElementSupplier = { HashSet<Any?>().uncheckedCast() }
            }
            WeightedCollectionValue::class.java.isAssignableFrom(valueType.rawType) -> {
                defaultElementSupplier = { WeightedTable<Any?>().uncheckedCast() }
            }
        }

        return ValueKey(key.uncheckedCast(), valueType.uncheckedCast(), elementType.uncheckedCast(),
                comparator, includesTester, defaultElementSupplier, this.requiresExplicitRegistration).uncheckedCast()
    }

    override fun reset(): B = apply {
        super.reset()

        this.valueType = null
        this.minValueSupplier = null
        this.maxValueSupplier = null
        this.comparator = null
        this.requiresExplicitRegistration = false
    }.uncheckedCast()

    companion object {

        private val elementParameter = Value::class.java.typeParameters[0]
        private val comparableComparator = Comparable<*>::compareTo
        private val defaultBounds = mutableMapOf<Class<*>, Pair<Any, Any>>()

        private inline fun <reified T : Any> setDefaultBounds(min: T, max: T) {
            this.defaultBounds[T::class.java] = min to max
        }

        init {
            setDefaultBounds(Byte.MIN_VALUE, Byte.MAX_VALUE)
            setDefaultBounds(Short.MIN_VALUE, Short.MAX_VALUE)
            setDefaultBounds(Int.MIN_VALUE, Int.MAX_VALUE)
            setDefaultBounds(Long.MIN_VALUE, Long.MAX_VALUE)
            setDefaultBounds(-Float.MAX_VALUE, Float.MAX_VALUE)
            setDefaultBounds(-Double.MAX_VALUE, Double.MAX_VALUE)
            setDefaultBounds(Char.MIN_VALUE, Char.MAX_VALUE)
            setDefaultBounds(UByte.MIN_VALUE, UByte.MAX_VALUE)
            setDefaultBounds(UShort.MIN_VALUE, UShort.MAX_VALUE)
            setDefaultBounds(UInt.MIN_VALUE, UInt.MAX_VALUE)
            setDefaultBounds(ULong.MIN_VALUE, ULong.MAX_VALUE)
            setDefaultBounds(min = false, max = true)
        }
    }
}
