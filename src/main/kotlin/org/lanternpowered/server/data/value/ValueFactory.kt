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

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.data.key.ValueKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer

@Suppress("UNCHECKED_CAST")
object ValueFactory : Value.Factory {

    override fun <V : Value<E>, E : Any> mutableOf(key: Key<V>, element: E) = (key as ValueKey<V, E>).valueConstructor.getMutable(element)
    override fun <V : Value<E>, E : Any> immutableOf(key: Key<V>, element: E) = (key as ValueKey<V, E>).valueConstructor.getImmutable(element)

    /**
     * Converts the [Value]s of the [ValueContainer] into a nicely
     * formatted `String`.
     *
     * @param valueContainer The value container
     * @return The string
     */
    fun toString(valueContainer: ValueContainer) = this.toString(valueContainer.values)

    /**
     * Converts the [Value]s into a nicely
     * formatted `String`.
     *
     * @param values The values
     * @return The string
     */
    fun toString(values: Iterable<Value<*>>) = ToStringHelper(brackets = ToStringHelper.Brackets.SQUARE)
            .apply {
                for (value in values)
                    this.add(value.key.key.formatted, value.get())
            }
            .toString()
}
