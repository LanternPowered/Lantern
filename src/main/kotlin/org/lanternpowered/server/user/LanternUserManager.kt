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

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import org.lanternpowered.api.service.user.UserStorageService
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.entity.player.LanternPlayer
import org.lanternpowered.server.entity.player.OfflinePlayer
import org.spongepowered.api.Server
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.profile.GameProfileManager
import org.spongepowered.api.user.UserManager
import java.util.Optional
import java.util.UUID
import java.util.stream.Stream
import kotlin.streams.asSequence
import kotlin.streams.asStream
import kotlin.streams.toList

class LanternUserManager(
        private val server: Server,
        private val storageService: UserStorageService,
        private val gameProfileManager: GameProfileManager
) : UserManager {

    private val users = Caffeine.newBuilder()
            .removalListener { _: UUID?, value: LanternUser?, cause: RemovalCause ->
                if (value == null || cause == RemovalCause.EXPLICIT)
                    return@removalListener
                value.saveIfNeeded()
            }
            .build<UUID, LanternUser> { uniqueId ->
                val profile = this.getGameProfile(uniqueId)
                val storage = this.storageService.get(uniqueId)
                LanternUser(profile, storage)
            }

    private fun LanternUser.saveIfNeeded() {
        val player = this.internalPlayer
        // Only save data for offline players, also ignore
        // unloaded players (if it's null)
        if (player !is OfflinePlayer)
            return
        this.save()
    }

    fun shutdown() {
        val pairs = this.users.asMap().values.toList()
        this.users.invalidateAll()
        for (pair in pairs)
            pair.save()
    }

    private fun exists(uniqueId: UUID): Boolean =
            this.server.getPlayer(uniqueId).isPresent || this.storageService.exists(uniqueId)

    private fun getGameProfile(uniqueId: UUID): GameProfile {
        val onlineProfile = this.server.getPlayer(uniqueId).orNull()?.profile
        if (onlineProfile != null)
            return onlineProfile
        return this.gameProfileManager.cache.getById(uniqueId)
                .orElseGet { GameProfile.of(uniqueId, "Unknown($uniqueId)") }
    }

    override fun streamOfMatches(lastKnownName: String): Stream<GameProfile> {
        val onlineProfiles = this.server.onlinePlayers
                .asSequence()
                .map { it.profile }
                .filter { profile -> profile.name.orNull() == lastKnownName }
        val offlineProfiles = this.gameProfileManager.cache.streamOfMatches(lastKnownName)
                .filter { profile -> this.exists(profile.uniqueId) }
                .asSequence()
        return (onlineProfiles + offlineProfiles).distinctBy { it.uniqueId }.asStream()
    }

    override fun getAll(): Collection<GameProfile> = this.streamAll().toList()

    override fun get(uniqueId: UUID): Optional<User> =
            if (this.exists(uniqueId)) this.users.get(uniqueId)!!.asOptional() else emptyOptional()

    override fun get(lastKnownName: String): Optional<User> {
        var profile = this.server.getPlayer(lastKnownName).orNull()?.profile
        if (profile == null)
            profile = this.gameProfileManager.cache.getByName(lastKnownName).orNull()
        return if (profile == null) emptyOptional() else this[profile]
    }

    override fun get(profile: GameProfile): Optional<User> = this[profile.uniqueId]

    override fun getOrCreate(profile: GameProfile): User {
        this.gameProfileManager.cache.add(profile)
        return this.users.get(profile.uniqueId)!!
    }

    override fun streamAll(): Stream<GameProfile> {
        val onlineProfiles = this.server.onlinePlayers
                .asSequence()
                .map { it.profile }
        val offlineProfiles = this.storageService.sequence()
                .map { storage -> this.getGameProfile(storage.uniqueId) }
        return (onlineProfiles + offlineProfiles)
                .distinctBy { it.uniqueId }
                .asStream()
    }

    override fun delete(profile: GameProfile): Boolean {
        // You cannot delete an online player
        if (this.server.getPlayer(profile.uniqueId).isPresent)
            return false
        // Invalidate the user as well, explicitly remove it so it doesn't get saved
        this.users.refresh(profile.uniqueId)
        return this.storageService.getIfPresent(profile.uniqueId)?.delete() ?: false
    }

    override fun delete(user: User): Boolean {
        user as LanternUser
        // You cannot delete an online player
        if (user.internalPlayer is LanternPlayer)
            return false
        val cachedUser = this.users.getIfPresent(user.uniqueId) ?: return false
        // Cached user isn't the same as the one requested to be deleted,
        // this means that the requested one was already deleted before
        if (cachedUser !== user)
            return false
        // Invalidate the user as well, explicitly remove it so it doesn't get saved
        this.users.refresh(user.uniqueId)
        return cachedUser.storage.delete()
    }
}
