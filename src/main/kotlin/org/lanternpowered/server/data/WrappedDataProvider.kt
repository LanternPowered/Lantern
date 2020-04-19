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

import org.spongepowered.api.data.DataProvider
import org.spongepowered.api.data.value.Value

/**
 * A wrapper to turn a [DataProvider] into a [IDataProvider].
 */
internal class WrappedDataProvider<V : Value<E>, E : Any>(val delegate: DataProvider<V, E>) :
        IDataProvider<V, E>, DataProvider<V, E> by delegate
