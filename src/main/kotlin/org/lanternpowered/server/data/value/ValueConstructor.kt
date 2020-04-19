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

interface ValueConstructor<V : Value<E>, E> {

    fun getMutable(element: E): V

    fun getImmutable(element: E): V = getRawImmutable(CopyHelper.copy(element))

    fun getRawImmutable(element: E): V
}