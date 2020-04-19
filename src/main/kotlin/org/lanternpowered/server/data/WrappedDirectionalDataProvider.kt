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
package org.lanternpowered.server.data

import org.spongepowered.api.data.DirectionRelativeDataProvider
import org.spongepowered.api.data.value.Value

/**
 * A wrapper to turn a [DirectionRelativeDataProvider] into a [IDirectionalDataProvider].
 */
internal class WrappedDirectionalDataProvider<V : Value<E>, E : Any>(val delegate: DirectionRelativeDataProvider<V, E>) :
        IDirectionalDataProvider<V, E>, DirectionRelativeDataProvider<V, E> by delegate
