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
package org.lanternpowered.server.data.value

import org.lanternpowered.api.util.uncheckedCast
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
