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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.util.collect

import org.lanternpowered.api.ext.*

/**
 * A [MutableList] that doesn't allow null values to be passed into the backing list.
 */
open class NonNullMutableList<E>(private val backing: MutableList<E>) : MutableList<E> by backing {

    override fun add(element: E) = this.backing.add(checkElement(element))
    override fun add(index: Int, element: E) = this.backing.add(index, checkElement(element))
    override fun set(index: Int, element: E) = this.backing.set(index, checkElement(element))

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        elements.forEach { checkElement(it) }
        return this.backing.addAll(index, elements)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        elements.forEach { checkElement(it) }
        return this.backing.addAll(elements)
    }

    override fun listIterator(): MutableListIterator<E> = NonNullMutableListIterator(this.backing.listIterator())
    override fun listIterator(index: Int): MutableListIterator<E> = NonNullMutableListIterator(this.backing.listIterator(index))

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = NonNullMutableList(this.backing.subList(fromIndex, toIndex))

    class NonNullMutableListIterator<E>(private val backing: MutableListIterator<E>) : MutableListIterator<E> by backing {

        override fun add(element: E) = this.backing.add(checkElement(element))
        override fun set(element: E) = this.backing.set(checkElement(element))
    }

    companion object {

        internal inline fun <E> checkElement(element: E): E = checkNotNull(element as Any) { "element" }.uncheckedCast()
    }
}
