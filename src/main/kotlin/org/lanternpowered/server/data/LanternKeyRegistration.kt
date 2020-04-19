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

import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value

internal abstract class LanternKeyRegistration<V : Value<E>, E : Any>(
        override val key: Key<V>
) : KeyRegistration<V, E> {

    /**
     * The data provider that handles modifications and the
     * retrieval for keys of this registration.
     */
    internal abstract val dataProvider: IDataProvider<V, E>
}
