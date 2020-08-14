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
package org.lanternpowered.server.network.attribute

import io.netty.util.Attribute

fun <T : Any> Attribute<T>.computeIfAbsent(fn: () -> T): T {
    var value = this.get()
    if (value != null)
        return value
    value = fn()
    return this.setIfAbsent(value) ?: value
}
