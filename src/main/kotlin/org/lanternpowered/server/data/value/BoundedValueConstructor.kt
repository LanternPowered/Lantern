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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.server.data.value

import org.lanternpowered.server.data.key.BoundedValueKey
import org.lanternpowered.server.data.value.CopyHelper.copy
import org.spongepowered.api.data.value.BoundedValue

class BoundedValueConstructor<V : BoundedValue<E>, E : Any> internal constructor(
        private val key: BoundedValueKey<V, E>
) : ValueConstructor<V, E> {

    override fun getMutable(element: E)
            = getMutable(element, this.key.minimum, this.key.maximum)

    fun getMutable(element: E, minimum: E, maximum: E)
            = getMutable(element, CopyHelper.createSupplier(minimum), CopyHelper.createSupplier(maximum))

    fun getMutable(element: E, minimum: () -> E, maximum: () -> E)
            = LanternMutableBoundedValue(this.key, element, minimum, maximum) as V

    fun getImmutable(element: E, minimum: E, maximum: E)
            = getImmutable(element, CopyHelper.createSupplier(minimum), CopyHelper.createSupplier(maximum))

    fun getImmutable(element: E, minimum: () -> E, maximum: () -> E)
            = getRawImmutable(copy(element), minimum, maximum)

    override fun getRawImmutable(element: E)
            = getRawImmutable(element, this.key.minimum, this.key.maximum)

    fun getRawImmutable(element: E, minimum: () -> E, maximum: () -> E)
            = LanternImmutableBoundedValue(this.key, element, minimum, maximum) as V
}