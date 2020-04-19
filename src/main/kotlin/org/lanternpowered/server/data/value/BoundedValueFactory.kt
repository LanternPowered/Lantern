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

import org.lanternpowered.server.data.key.BoundedValueKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.BoundedValue

@Suppress("UNCHECKED_CAST")
object BoundedValueFactory : BoundedValue.Factory {

    override fun <V : BoundedValue<E>, E : Any> mutableOf(key: Key<V>, element: E, minimum: E, maximum: E)
            = (key as BoundedValueKey<V, E>).valueConstructor.getMutable(element, minimum, maximum)

    override fun <V : BoundedValue<E>, E : Any> mutableOf(key: Key<V>, element: E)
            = (key as BoundedValueKey<V, E>).valueConstructor.getMutable(element)

    override fun <V : BoundedValue<E>, E : Any> immutableOf(key: Key<V>, element: E, minimum: E, maximum: E)
            = (key as BoundedValueKey<V, E>).valueConstructor.getImmutable(element, minimum, maximum)

    override fun <V : BoundedValue<E>, E : Any> immutableOf(key: Key<V>, element: E)
            = (key as BoundedValueKey<V, E>).valueConstructor.getImmutable(element)
}
