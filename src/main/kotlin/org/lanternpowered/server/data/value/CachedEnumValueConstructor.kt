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

import org.spongepowered.api.data.value.Value

internal class CachedEnumValueConstructor<V : Value<E>, E : Any>(
        private val original: ValueConstructor<V, E>, enumType: Class<E>
) : ValueConstructor<V, E> {

    private val immutableValues = enumType.enumConstants.asSequence()
            .map { this.original.getImmutable(it) }
            .toList()

    override fun getMutable(element: E): V = this.original.getMutable(element)
    override fun getImmutable(element: E) = getRawImmutable(element)
    override fun getRawImmutable(element: E) = this.immutableValues[(element as Enum<*>).ordinal]
}
