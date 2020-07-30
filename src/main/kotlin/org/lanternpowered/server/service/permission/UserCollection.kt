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
package org.lanternpowered.server.service.permission

import org.lanternpowered.api.Server
import org.lanternpowered.server.game.Lantern
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.permission.Subject
import java.util.UUID

/**
 * User collection keeping track of opped users.
 */
class UserCollection internal constructor(
        val service: LanternPermissionService
) : LanternSubjectCollection(PermissionService.SUBJECTS_USER, service) {

    private fun parseUniqueId(identifier: String): UUID? {
        return try {
            UUID.fromString(identifier)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    override fun get(identifier: String): LanternSubject {
        val uuid = parseUniqueId(identifier) ?: error("Provided identifier must be a uuid, was $identifier")
        val profile = try {
            Server.gameProfileManager[uuid, true].get()
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to lookup game profile for $uuid", e)
        }
        return UserSubject(profile, this)
    }

    override fun isRegistered(identifier: String): Boolean {
        val uuid = parseUniqueId(identifier)
        return uuid != null && Lantern.getGame().opsConfig.getEntryByUUID(uuid).isPresent
    }

    override fun getLoadedSubjects(): Collection<Subject> = Server.onlinePlayers
}
