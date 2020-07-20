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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.server.data.value

import org.lanternpowered.server.data.key.BoundedValueKey
import org.lanternpowered.server.data.value.CopyHelper.copy
import org.spongepowered.api.data.value.BoundedValue

class BoundedValueConstructor<V : BoundedValue<E>, E : Any> internal constructor(
        private val key: BoundedValueKey<V, E>
) : ValueConstructor<V, E> {

    override fun getMutable(element: E)
            = getMutable(element, this.key.minimum, this.key.maximum)

    fun getMutable(element: E, minimum: E, maximum: E)
            = getMutable(element, CopyHelper.createSupplier(minimum), CopyHelper.createSupplier(maximum))

    fun getMutable(element: E, minimum: () -> E, maximum: () -> E)
            = LanternMutableBoundedValue(this.key, element, minimum, maximum) as V

    fun getImmutable(element: E, minimum: E, maximum: E)
            = getImmutable(element, CopyHelper.createSupplier(minimum), CopyHelper.createSupplier(maximum))

    fun getImmutable(element: E, minimum: () -> E, maximum: () -> E)
            = getRawImmutable(copy(element), minimum, maximum)

    override fun getRawImmutable(element: E)
            = getRawImmutable(element, this.key.minimum, this.key.maximum)

    fun getRawImmutable(element: E, minimum: () -> E, maximum: () -> E)
            = LanternImmutableBoundedValue(this.key, element, minimum, maximum) as V
}
