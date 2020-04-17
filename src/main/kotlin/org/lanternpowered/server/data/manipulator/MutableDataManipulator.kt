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
package org.lanternpowered.server.data.manipulator

import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.data.value.CopyHelper
import org.spongepowered.api.data.DataManipulator
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer
import java.util.function.Predicate

class MutableDataManipulator(map: MutableMap<Key<*>, Any> = mutableMapOf()) : AbstractDataManipulator(map), DataManipulator.Mutable {

    override fun copyFrom(valueContainer: ValueContainer, overlap: MergeFunction, predicate: Predicate<Key<*>>) = apply {
        if (overlap == MergeFunction.REPLACEMENT_PREFERRED) {
            valueContainer.values.forEach { value ->
                if (predicate.test(value.key)) {
                    set(value.key.uncheckedCast(), value.get())
                }
            }
        } else {
            valueContainer.values.forEach { value ->
                val key = value.key.uncheckedCast<Key<Value<Any>>>()
                if (predicate.test(value.key)) {
                    val original = getValue(key).orNull()
                    val replacement = overlap.merge(original, value.uncheckedCast())
                    set(key, replacement.get())
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun copyFrom(valueContainer: ValueContainer, overlap: MergeFunction, keys: Iterable<Key<*>>) = apply {
        keys as Iterable<Key<Value<Any>>>

        if (overlap == MergeFunction.REPLACEMENT_PREFERRED) {
            keys.forEach { key ->
                set(key, valueContainer[key])
            }
        } else {
            keys.forEach { key ->
                val value = valueContainer.getValue(key)
                if (value != null) {
                    val original = getValue(key).orNull()
                    val replacement = overlap.merge(original, value.uncheckedCast())
                    set(key, replacement.get())
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun copyFrom(valueContainer: ValueContainer, overlap: MergeFunction) = apply {
        val values = valueContainer.values as Iterable<Value.Immutable<Any>>

        if (overlap == MergeFunction.REPLACEMENT_PREFERRED) {
            values.forEach { value ->
                set(value.key, value.get())
            }
        } else {
            values.forEach { value ->
                val key = value.key.uncheckedCast<Key<Value<Any>>>()

                val original = getValue(key).orNull()
                val replacement = overlap.merge(original, value.uncheckedCast())

                set(key, replacement.get())
            }
        }
    }

    override fun <E : Any> set(key: Key<out Value<E>>, value: E) = apply {
        this.map[key] = value
    }

    override fun remove(key: Key<*>) = apply {
        this.map.remove(key)
    }

    override fun copy() = MutableDataManipulator(CopyHelper.copyMap(this.map))

    override fun asMutableCopy() = copy()

    override fun asImmutable() = ImmutableDataManipulator(CopyHelper.copyMap(this.map))
}
