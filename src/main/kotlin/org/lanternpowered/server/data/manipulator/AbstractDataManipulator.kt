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

import com.google.common.collect.ImmutableSet
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.optional
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
