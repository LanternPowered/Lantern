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

import org.lanternpowered.api.util.collections.containsAll
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.CollectionValue
import org.spongepowered.api.data.value.Value
import java.util.function.Function
import java.util.function.Predicate

abstract class LanternCollectionValue<E, C : MutableCollection<E>> protected constructor(
        key: Key<out Value<C>>, value: C
) : LanternValue<C>(key, value), CollectionValue<E, C> {

    override fun size() = this.value.size

    override fun isEmpty() = this.value.isEmpty()

    override fun contains(element: E) = element in this.value

    override fun containsAll(iterable: Iterable<E>) = this.value.containsAll(iterable)

    override fun getAll(): C = CopyHelper.copy(this.value)

    override fun iterator() = get().iterator()

    abstract class Mutable<E, C : MutableCollection<E>, M : CollectionValue.Mutable<E, C, M, I>, I : CollectionValue.Immutable<E, C, I, M>>
            protected constructor(key: Key<out Value<C>>, value: C) : LanternCollectionValue<E, C>(key, value), CollectionValue.Mutable<E, C, M, I> {

        private inline fun also(fn: () -> Unit) = apply { fn() }.uncheckedCast<M>()

        override fun add(element: E) = also { this.value.add(element) }

        override fun addAll(elements: Iterable<E>) = also { this.value.addAll(elements) }

        override fun remove(element: E) = also { this.value.remove(element) }

        override fun removeAll(elements: Iterable<E>) = also { this.value.removeAll(elements) }

        override fun removeAll(predicate: Predicate<E>) = also { this.value.removeIf(predicate) }

        override fun set(value: C) = also { this.value = value }

        override fun transform(function: Function<C, C>) = set(function.apply(get()))
    }

    abstract class Immutable<E, C : MutableCollection<E>, I : CollectionValue.Immutable<E, C, I, M>, M : CollectionValue.Mutable<E, C, M, I>>
            protected constructor(key: Key<out Value<C>>, value: C) : LanternCollectionValue<E, C>(key, value), CollectionValue.Immutable<E, C, I, M> {

        override fun get(): C = CopyHelper.copy(super.get())

        override fun withElement(element: E): I {
            val collection = get()
            return if (collection.add(element)) withValue(collection) else uncheckedCast()
        }

        override fun withAll(elements: Iterable<E>): I {
            var change = false
            val collection = get()
            for (element in elements) {
                change = collection.add(element) || change
            }
            return if (change) withValue(collection) else uncheckedCast()
        }

        override fun without(element: E): I {
            if (element !in this) {
                return uncheckedCast()
            }
            val collection = get()
            collection.remove(element)
            return withValue(collection)
        }

        override fun withoutAll(elements: Iterable<E>): I {
            val collection = get()
            return if (collection.removeAll(elements)) withValue(collection) else uncheckedCast()
        }

        override fun withoutAll(predicate: Predicate<E>): I {
            val collection = get()
            return if (collection.removeIf(predicate)) withValue(collection) else uncheckedCast()
        }

        override fun with(value: C) = withValue(CopyHelper.copy(value))

        /**
         * Constructs a new [LanternCollectionValue.Immutable]
         * without copying the actual value.
         *
         * @param value The value element
         * @return The new immutable value
         */
        protected abstract fun withValue(value: C): I

        override fun transform(function: Function<C, C>) = with(function.apply(get()))
    }
}
