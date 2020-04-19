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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.server.data.key

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.data.KeyBuilder
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.Value
import java.util.function.BiPredicate

class ValueKeyBuilder<E : Any, V : Value<E>> : ValueKeyBuilderBase<E, V, ValueKeyBuilder<E, V>, KeyBuilder<V>>(), KeyBuilder<V> {

    override fun <V : BoundedValue<E>, E : Comparable<E>> KeyBuilder<V>.range(range: ClosedRange<E>) = apply {
        this as ValueKeyBuilder<E, V>
        setMinValue(range.start)
        setMaxValue(range.endInclusive)
    }

    override fun <V : BoundedValue<E>, E : Any> KeyBuilder<V>.maximum(value: E)
            = apply { (this as ValueKeyBuilder<E, V>).setMaxValue(value) }

    override fun <V : BoundedValue<E>, E : Any> KeyBuilder<V>.maximum(supplier: () -> E)
            = apply { (this as ValueKeyBuilder<E, V>).setMaxValueSupplier(supplier) }

    override fun <V : BoundedValue<E>, E : Any> KeyBuilder<V>.minimum(value: E)
            = apply { (this as ValueKeyBuilder<E, V>).setMinValue(value) }

    override fun <V : BoundedValue<E>, E : Any> KeyBuilder<V>.minimum(supplier: () -> E)
            = apply { (this as ValueKeyBuilder<E, V>).setMinValueSupplier(supplier) }

    override fun <V : Value<E>, E : Any> KeyBuilder<V>.comparator(comparator: Comparator<in E>)
            = apply { (this as ValueKeyBuilder<E, V>).setComparator(comparator) }

    override fun <V : Value<E>, E : Any> KeyBuilder<V>.includesTester(tester: (E, E) -> Boolean)
            = apply { (this as ValueKeyBuilder<E, V>).setIncludesTester(BiPredicate(tester)) }

    override fun <N : Value<*>> type(token: TypeToken<N>): KeyBuilder<N> = apply { setType(token.uncheckedCast()) }.uncheckedCast()

    override fun requireExplicitRegistration() = apply { setRequireExplicitRegistration() }
}
