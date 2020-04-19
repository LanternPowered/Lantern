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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.server.data

import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.value.Value

/**
 * Gets the [IDataProvider] from the target [KeyRegistration].
 */
internal inline fun <V : Value<E>, E : Any> KeyRegistration<*,*>.dataProvider() =
        uncheckedCast<LanternKeyRegistration<V, E>>().dataProvider

/**
 * Gets the [IDataProvider] from the target [KeyRegistration].
 */
internal inline fun KeyRegistration<*,*>.anyDataProvider() =
        uncheckedCast<LanternKeyRegistration<Value<Any>, Any>>().dataProvider
