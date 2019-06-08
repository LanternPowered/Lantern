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
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.catalog.AbstractCatalogBuilder
import org.lanternpowered.server.data.value.CopyHelper
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.OptionalValue
import org.spongepowered.api.data.value.Value
import java.util.Comparator
import java.util.Optional
import java.util.function.Supplier

class ValueKeyBuilder<E : Any, V : Value<E>> : AbstractCatalogBuilder<Key<V>, Key.Builder<E, V>>(),
        Key.Builder<E, V>, Key.Builder.BoundedBuilder<E, V> {

    private var typeToken: TypeToken<V>? = null

    private var minimumValueSupplier: (() -> E)? = null
    private var maximumValueSupplier: (() -> E)? = null
    private var comparator: (Comparator<in E>)? = null

    override fun key(key: CatalogKey) = apply { super.key(key) }

    override fun minValue(minValue: E) = minValueSupplier { CopyHelper.copy(minValue) }
    override fun minValueSupplier(supplier: Supplier<out E>) = apply { this.minimumValueSupplier = supplier::get }

    override fun maxValue(maxValue: E) = maxValueSupplier { CopyHelper.copy(maxValue) }
    override fun maxValueSupplier(supplier: Supplier<out E>) = apply { this.maximumValueSupplier = supplier::get }

    override fun comparator(comparator: Comparator<in E>) = apply { this.comparator = comparator }

    override fun build(key: CatalogKey): Key<V> {
        val valueType = checkNotNull(this.typeToken) { "The type must be set" }
        val elementType = valueType.resolveType(elementParameter).uncheckedCast<TypeToken<E>>()

        return when {
            valueType.isSubtypeOf(OptionalValue::class.java) -> OptionalWrappedValueKey(
                    key, valueType.uncheckedCast(), elementType.uncheckedCast<TypeToken<Optional<Any>>>()).uncheckedCast()
            valueType.isSubtypeOf(BoundedValue::class.java) -> {
                val minimumValueSupplier = checkNotNull(this.minimumValueSupplier) { "The minimal value supplier must be set" }
                val maximumValueSupplier = checkNotNull(this.maximumValueSupplier) { "The maximum value supplier must be set" }
                val comparator = this.comparator ?: run {
                    if (elementType.isSubtypeOf(Comparable::class.java)) {
                        comparableComparator.uncheckedCast<Comparator<in E>>()
                    } else throw IllegalStateException("The comparator must be set for non comparable types.")
                }
                BoundedValueKey(key, valueType.uncheckedCast<TypeToken<BoundedValue<E>>>(), elementType.uncheckedCast(),
                        minimumValueSupplier.uncheckedCast(), maximumValueSupplier.uncheckedCast(), comparator.uncheckedCast()).uncheckedCast()
            }
            else -> ValueKey(key, valueType, elementType)
        }
    }

    override fun <T : Any, B : Value<T>> type(token: TypeToken<B>): ValueKeyBuilder<T, B> = apply {
        this.typeToken = token.uncheckedCast()
    }.uncheckedCast()

    override fun <T : Any, B : BoundedValue<T>> boundedType(token: TypeToken<B>) = type(token)

    override fun reset() = apply {
        super.reset()

        this.typeToken = null
        this.minimumValueSupplier = null
        this.maximumValueSupplier = null
        this.comparator = null
    }

    companion object {

        private val elementParameter = Value::class.java.typeParameters[0]
        private val comparableComparator = Comparable<*>::compareTo
    }
}
