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

import org.lanternpowered.api.data.persistence.DataContainer
import org.lanternpowered.api.data.persistence.DataView

/**
 * A storage service which can be used to store simple pieces of
 * data, for example the server scoreboard.
 */
interface SimpleStorageService {

    /**
     * Saves the data for the simple storage key.
     */
    fun save(key: SimpleStorageKey, data: DataView)

    /**
     * Loads the data for the simple storage key, if it exists.
     */
    fun load(key: SimpleStorageKey): DataContainer?

    /**
     * Deletes the data attached to the simple storage key.
     */
    fun delete(key: SimpleStorageKey): Boolean
}
