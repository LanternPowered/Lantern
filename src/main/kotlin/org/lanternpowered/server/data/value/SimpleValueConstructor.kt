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

import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value

internal class SimpleValueConstructor<V : Value<E>, E : Any>(
        private val key: Key<V>,
        private val mutableConstructor: (Key<V>, E) -> V,
        private val immutableConstructor: (Key<V>, E) -> V
) : ValueConstructor<V, E> {

    override fun getMutable(element: E) = this.mutableConstructor(this.key, element)
    override fun getRawImmutable(element: E) = this.immutableConstructor(this.key, element)
}
