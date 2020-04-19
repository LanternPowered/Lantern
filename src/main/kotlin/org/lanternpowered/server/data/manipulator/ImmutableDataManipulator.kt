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

import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.data.value.CopyHelper
import org.spongepowered.api.data.DataManipulator
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.function.Function

class ImmutableDataManipulator(map: MutableMap<Key<*>, Any>) : AbstractDataManipulator(map), DataManipulator.Immutable {

    override fun <E : Any> with(value: Value<E>): DataManipulator.Immutable {
        val mapCopy = CopyHelper.copyMap(this.map)
        mapCopy[value.key] = value.get()
        return ImmutableDataManipulator(mapCopy)
    }

    override fun <E : Any> with(key: Key<out Value<E>>, value: E): DataManipulator.Immutable {
        val mapCopy = CopyHelper.copyMap(this.map)
        mapCopy[key] = value
        return ImmutableDataManipulator(mapCopy)
    }

    override fun without(key: Key<*>): DataManipulator.Immutable {
        if (key !in this.map) {
            return this
        }
        val mapCopy = CopyHelper.copyMap(this.map)
        mapCopy.remove(key)
        return ImmutableDataManipulator(mapCopy)
    }

    override fun <E : Any> transform(key: Key<out Value<E>>, function: Function<E, E>): DataManipulator.Immutable {
        if (key !in this.map) {
            return this
        }
        val mapCopy = CopyHelper.copyMap(this.map)
        mapCopy[key] = function.apply(this.map[key].uncheckedCast())
        return ImmutableDataManipulator(mapCopy)
    }

    override fun copy() = this

    override fun asMutableCopy() = MutableDataManipulator(CopyHelper.copyMap(this.map))

    override fun asMutable() = asMutableCopy()
}
