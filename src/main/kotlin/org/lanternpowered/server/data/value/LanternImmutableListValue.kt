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
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.ListValue

class LanternImmutableListValue<E>(key: Key<out ListValue<E>>, value: MutableList<E>) :
        LanternCollectionValue.Immutable<E, MutableList<E>, ListValue.Immutable<E>, ListValue.Mutable<E>>(key, value), ListValue.Immutable<E> {

    override fun getKey() = super.getKey().uncheckedCast<Key<out ListValue<E>>>()

    override fun withValue(value: MutableList<E>) = LanternImmutableListValue(this.key, value)

    override fun get(): MutableList<E> = CopyHelper.copyList(super.get())

    override fun get(index: Int) = this.value[index]

    override fun indexOf(element: E) = this.value.indexOf(element)

    override fun with(index: Int, value: E): ListValue.Immutable<E> {
        val list = get()
        list.add(index, value)
        return withValue(list)
    }

    override fun with(index: Int, values: Iterable<E>): ListValue.Immutable<E> {
        var i = index
        val list = get()
        for (value in values) {
            list.add(i++, value)
        }
        return withValue(list)
    }

    override fun without(index: Int): ListValue.Immutable<E> {
        val list = get()
        list.removeAt(index)
        return withValue(list)
    }

    override fun set(index: Int, element: E): ListValue.Immutable<E> {
        val list = get()
        list[index] = element
        return withValue(list)
    }

    override fun asMutable() = LanternMutableListValue(this.key, get())
}
