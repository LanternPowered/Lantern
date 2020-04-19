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
package org.lanternpowered.server.data.manipulator

import org.lanternpowered.api.util.optional.orNull
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
