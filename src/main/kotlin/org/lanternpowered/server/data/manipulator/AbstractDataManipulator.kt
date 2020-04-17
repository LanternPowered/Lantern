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

import com.google.common.collect.ImmutableSet
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.data.ValueContainerBase
import org.lanternpowered.server.data.value.ValueFactory
import org.spongepowered.api.data.DataManipulator
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.Optional

abstract class AbstractDataManipulator(
        protected val map: MutableMap<Key<*>, Any>
) : ValueContainerBase, DataManipulator {

    override fun <E : Any> get(key: Key<out Value<E>>): Optional<E> = this.map[key].uncheckedCast<E?>().optional()

    override fun <E : Any, V : Value<E>> getValue(key: Key<V>): Optional<V> {
        val element = this.map[key].uncheckedCast<E?>()
        return if (element == null) emptyOptional() else ValueFactory.mutableOf(key, element).optional()
    }

    override fun getKeys() = this.map.keys.toImmutableSet()

    override fun getValues(): Set<Value.Immutable<*>> {
        val builder = ImmutableSet.builder<Value.Immutable<*>>()
        for ((key, element) in this.map.entries) {
            builder.add(ValueFactory.immutableOf(key.uncheckedCast(), element).asImmutable())
        }
        return builder.build()
    }

    override fun supports(key: Key<*>) = true
}
