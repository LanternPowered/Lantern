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
package org.lanternpowered.api.service.storage

/**
 * Represents a data key to store data using the [SimpleStorageService].
 */
data class SimpleStorageKey(val name: String)

/**
 * All the default simple storage keys.
 */
object SimpleStorageKeys {

    /**
     * The key used to store the server scoreboard.
     */
    val SERVER_SCOREBOARD = SimpleStorageKey("server-scoreboard")
}
