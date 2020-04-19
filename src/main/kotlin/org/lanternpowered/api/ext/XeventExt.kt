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
package org.lanternpowered.api.ext

import org.lanternpowered.api.xevent.Xevent
import org.lanternpowered.api.xevent.XeventBus
import org.lanternpowered.api.xevent.XeventHandler

inline fun <reified T : Xevent> XeventBus.registerHandler(handler: XeventHandler<T>) = register(T::class, handler)
inline fun <reified T : Xevent> XeventBus.post(noinline supplier: () -> T) = post(T::class, supplier)
