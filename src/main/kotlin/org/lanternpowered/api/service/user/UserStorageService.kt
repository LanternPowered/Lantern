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

import java.util.UUID

interface UserStorageService {

    /**
     * Gets the user storage for the user with the given [UUID].
     */
    fun get(uniqueId: UUID): UserStorage

    /**
     * Gets a sequence of [UserStorage]s of users that "exist".
     */
    fun sequence(): Sequence<UserStorage>

    /**
     * Deletes all the user data.
     */
    fun deleteAll() {
        for (storage in this.sequence())
            storage.delete()
    }
}
