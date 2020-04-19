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

import com.google.common.collect.ImmutableMap
import org.spongepowered.api.data.DataManipulator
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer

object ImmutableDataManipulatorFactory : DataManipulator.Immutable.Factory {

    private val empty = ImmutableDataManipulator(ImmutableMap.of())

    override fun of() = this.empty

    override fun of(values: Iterable<Value<*>>): DataManipulator.Immutable {
        val map = mutableMapOf<Key<*>, Any>()
        values.forEach { value -> map[value.key] = value.get() }
        return ImmutableDataManipulator(map)
    }

    override fun of(valueContainer: ValueContainer) = of(valueContainer.values)
}
