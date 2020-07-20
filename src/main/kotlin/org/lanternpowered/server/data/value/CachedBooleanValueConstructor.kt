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

internal class CachedBooleanValueConstructor(
        private val original: ValueConstructor<Value<Boolean>, Boolean>
) : ValueConstructor<Value<Boolean>, Boolean> {

    private val immutableValueTrue = this.original.getImmutable(true)
    private val immutableValueFalse = this.original.getImmutable(false)

    override fun getMutable(element: Boolean) = this.original.getMutable(element)
    override fun getImmutable(element: Boolean) = getRawImmutable(element)
    override fun getRawImmutable(element: Boolean) = if (element) this.immutableValueTrue else this.immutableValueFalse
}
