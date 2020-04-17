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
