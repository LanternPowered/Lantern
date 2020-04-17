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
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.Value
import java.util.Comparator
import java.util.function.BiPredicate
import java.util.function.Supplier

class SpongeValueKeyBuilder<E : Any, V : Value<E>> : ValueKeyBuilderBase<E, V, SpongeValueKeyBuilder<E, V>, Key.Builder<E, V>>(),
        Key.Builder<E, V>, Key.Builder.BoundedBuilder<E, V> {

    override fun <T : Any, N : Value<T>> type(token: TypeToken<N>): SpongeValueKeyBuilder<T, N> =
            apply { setType(token.uncheckedCast()) }.uncheckedCast()

    override fun <T : Any, B : BoundedValue<T>> boundedType(token: TypeToken<B>): SpongeValueKeyBuilder<T, B> = type(token)

    override fun minValue(minValue: E) = apply { setMinValue(minValue) }
    override fun minValueSupplier(supplier: Supplier<out E>) = apply { setMinValueSupplier(supplier::get) }

    override fun maxValue(maxValue: E) = apply { setMaxValue(maxValue) }
    override fun maxValueSupplier(supplier: Supplier<out E>) = apply { setMaxValueSupplier(supplier::get) }

    override fun comparator(comparator: Comparator<in E>) = apply { setComparator(comparator) }
    override fun includesTester(predicate: BiPredicate<in E, in E>?) = apply { setIncludesTester(predicate) }
}
