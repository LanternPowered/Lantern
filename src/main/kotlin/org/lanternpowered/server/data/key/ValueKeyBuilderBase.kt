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
import org.spongepowered.api.data.value.OptionalValue
import org.spongepowered.api.data.value.Value
import java.util.Optional

open class ValueKeyBuilderBase<E : Any, V : Value<E>, B : R, R : BaseBuilder<Key<V>, R>> : AbstractCatalogBuilder<Key<V>, R>() {

    private var typeToken: TypeToken<V>? = null

    private var minimumValueSupplier: (() -> E)? = null
    private var maximumValueSupplier: (() -> E)? = null
    private var comparator: (Comparator<in E>)? = null

    private var requiresExplicitRegistration = false

    protected fun setType(type: TypeToken<V>) {
        this.typeToken = type
    }

    override fun key(key: CatalogKey): B = apply { super.key(key) }.uncheckedCast()

    protected fun setMinValue(minValue: E) = setMinValueSupplier { CopyHelper.copy(minValue) }
    protected fun setMinValueSupplier(supplier: () -> E) { this.minimumValueSupplier = supplier }

    protected fun setMaxValue(maxValue: E) = setMaxValueSupplier { CopyHelper.copy(maxValue) }
    protected fun setMaxValueSupplier(supplier: () -> E) { this.maximumValueSupplier = supplier }

    protected fun setComparator(comparator: Comparator<in E>) { this.comparator = comparator }

    protected fun setRequireExplicitRegistration() { this.requiresExplicitRegistration = true }

    override fun build(key: CatalogKey): Key<V> {
        val valueType = checkNotNull(this.typeToken) { "The type must be set" }
        val elementType = valueType.resolveType(elementParameter).uncheckedCast<TypeToken<E>>()

        return when {
            valueType.isSubtypeOf(OptionalValue::class.java) -> OptionalValueKey(key, valueType.uncheckedCast(),
                    elementType.uncheckedCast<TypeToken<Optional<Any>>>(), this.requiresExplicitRegistration).uncheckedCast()
            valueType.isSubtypeOf(BoundedValue::class.java) -> {
                val minimumValueSupplier = this.minimumValueSupplier ?: getDefaultMin(elementType.rawType) ?: throw IllegalStateException(
                        "The minimal value supplier isn't set")
                val maximumValueSupplier = this.maximumValueSupplier ?: getDefaultMax(elementType.rawType) ?: throw IllegalStateException(
                        "The maximum value supplier isn't set")
                val comparator = this.comparator ?: run {
                    if (elementType.isSubtypeOf(Comparable::class.java)) {
                        comparableComparator.uncheckedCast<Comparator<in E>>()
                    } else throw IllegalStateException("The comparator must be set for non comparable types.")
                }
                BoundedValueKey(key, valueType.uncheckedCast<TypeToken<BoundedValue<E>>>(), elementType.uncheckedCast(),
                        this.requiresExplicitRegistration, minimumValueSupplier.uncheckedCast(), maximumValueSupplier.uncheckedCast(),
                        comparator.uncheckedCast()).uncheckedCast()
            }
            else -> ValueKey(key, valueType, elementType, this.requiresExplicitRegistration)
        }
    }

    private fun getDefaultMin(elementType: Class<*>): Any? {
        return when (elementType) {
            java.lang.Byte::class.java -> Byte.MIN_VALUE
            java.lang.Short::class.java -> Short.MIN_VALUE
            java.lang.Integer::class.java -> Int.MIN_VALUE
            java.lang.Long::class.java -> Long.MIN_VALUE
            java.lang.Float::class.java -> -Float.MAX_VALUE
            java.lang.Double::class.java -> -Double.MAX_VALUE
            java.lang.Character::class.java -> Char.MIN_VALUE
            java.lang.Boolean::class.java -> false
            UByte::class.java -> UByte.MIN_VALUE
            UShort::class.java -> UShort.MIN_VALUE
            UInt::class.java -> UInt.MIN_VALUE
            ULong::class.java -> ULong.MIN_VALUE
            else -> null
        }
    }

    private fun getDefaultMax(elementType: Class<*>): Any? {
        return when (elementType) {
            java.lang.Byte::class.java -> Byte.MAX_VALUE
            java.lang.Short::class.java -> Short.MAX_VALUE
            java.lang.Integer::class.java -> Int.MAX_VALUE
            java.lang.Long::class.java -> Long.MAX_VALUE
            java.lang.Float::class.java -> Float.MAX_VALUE
            java.lang.Double::class.java -> Double.MAX_VALUE
            java.lang.Character::class.java -> Char.MAX_VALUE
            java.lang.Boolean::class.java -> true
            UByte::class.java -> UByte.MAX_VALUE
            UShort::class.java -> UShort.MAX_VALUE
            UInt::class.java -> UInt.MAX_VALUE
            ULong::class.java -> ULong.MAX_VALUE
            else -> null
        }
    }

    override fun reset(): B = apply {
        super.reset()

        this.typeToken = null
        this.minimumValueSupplier = null
        this.maximumValueSupplier = null
        this.comparator = null
        this.requiresExplicitRegistration = false
    }.uncheckedCast()

    companion object {

        private val elementParameter = Value::class.java.typeParameters[0]
        private val comparableComparator = Comparable<*>::compareTo
    }
}
