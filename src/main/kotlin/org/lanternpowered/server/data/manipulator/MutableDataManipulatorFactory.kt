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

import org.spongepowered.api.data.DataManipulator
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer

object MutableDataManipulatorFactory : DataManipulator.Mutable.Factory {

    override fun of() = MutableDataManipulator()

    override fun of(values: Iterable<Value<*>>): DataManipulator.Mutable {
        val manipulator = MutableDataManipulator()
        values.forEach { value -> manipulator.set(value) }
        return manipulator
    }

    override fun of(valueContainer: ValueContainer) = of(valueContainer.values)
}
