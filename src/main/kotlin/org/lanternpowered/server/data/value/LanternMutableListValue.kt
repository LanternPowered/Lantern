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
package org.lanternpowered.server.data.value

import org.lanternpowered.api.ext.*
import org.lanternpowered.server.data.key.ValueKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.ListValue

class LanternMutableListValue<E>(key: Key<out ListValue<E>>, value: MutableList<E>) :
        LanternCollectionValue.Mutable<E, MutableList<E>, ListValue.Mutable<E>, ListValue.Immutable<E>>(key, value), ListValue.Mutable<E> {

    override fun getKey() = super.getKey().uncheckedCast<ValueKey<ListValue<E>, List<E>>>()

    override fun get(index: Int) = this.value[index]

    override fun indexOf(element: E) = this.value.indexOf(element)

    override fun add(index: Int, value: E) = apply { this.value.add(index, value) }

    override fun add(index: Int, values: Iterable<E>) = apply {
        var i = index
        for (value in values) {
            add(i++, value)
        }
    }

    override fun remove(index: Int) = apply { this.value.removeAt(index) }

    override fun set(index: Int, element: E) = apply { this.value[index] = element }

    override fun asImmutable(): ListValue.Immutable<E> = this.key.valueConstructor.getImmutable(this.value).asImmutable()

    override fun copy() = LanternMutableListValue(this.key, CopyHelper.copyList(this.value))
}
