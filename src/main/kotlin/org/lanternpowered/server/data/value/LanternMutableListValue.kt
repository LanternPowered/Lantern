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
