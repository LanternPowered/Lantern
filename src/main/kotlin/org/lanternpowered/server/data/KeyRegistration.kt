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

/**
 * Represents a key that was registered to a collection.
 */
interface KeyRegistration<V : Value<E>, E : Any> {

    /**
     * The key of the registration.
     */
    val key: Key<V>
}
