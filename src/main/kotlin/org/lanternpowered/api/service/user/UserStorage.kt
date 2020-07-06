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
package org.lanternpowered.api.service.user

import org.lanternpowered.api.data.persistence.DataContainer
import org.lanternpowered.api.data.persistence.DataView
import org.lanternpowered.api.util.Identifiable

/**
 * Represents a storage of data for a specific user.
 *
 * The statistic data is present in the path
 * `DataQuery.of("Statistics")`. The data is represented
 * as a map where the key is the id of the statistic and
 * the value is the value of the statistic.
 */
interface UserStorage : Identifiable {

    /**
     * Whether data related to the user exists.
     */
    val exists: Boolean

    /**
     * Loads a [DataContainer] with all user data.
     */
    fun load(): DataContainer

    /**
     * Saves a [DataView] with all user data.
     */
    fun save(data: DataView)

    /**
     * Deletes all data related to the user.
     */
    fun delete(): Boolean
}
