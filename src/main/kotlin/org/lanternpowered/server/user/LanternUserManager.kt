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
package org.lanternpowered.server.user

import org.lanternpowered.api.service.user.UserStorageService
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.user.UserManager
import java.util.Optional
import java.util.UUID
import java.util.stream.Stream

class LanternUserManager(
        private val storageService: UserStorageService
) : UserManager {

    override fun streamOfMatches(lastKnownName: String): Stream<GameProfile> {
        TODO("Not yet implemented")
    }

    override fun getAll(): MutableCollection<GameProfile> {
        TODO("Not yet implemented")
    }

    override fun get(uniqueId: UUID): Optional<User> {
        TODO("Not yet implemented")
    }

    override fun get(lastKnownName: String): Optional<User> {
        TODO("Not yet implemented")
    }

    override fun get(profile: GameProfile): Optional<User> {
        TODO("Not yet implemented")
    }

    override fun getOrCreate(profile: GameProfile): User {
        TODO("Not yet implemented")
    }

    override fun streamAll(): Stream<GameProfile> {
        TODO("Not yet implemented")
    }

    override fun delete(profile: GameProfile): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(user: User): Boolean {
        TODO("Not yet implemented")
    }
}
