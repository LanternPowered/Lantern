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
package org.lanternpowered.server.profile

import org.lanternpowered.api.profile.GameProfile
import org.lanternpowered.api.service.profile.GameProfileService
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.orNull
import org.spongepowered.api.profile.GameProfileCache
import java.time.Instant
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Stream

class LanternGameProfileCache(
        private val service: GameProfileService
) : GameProfileCache {

    private val byUniqueId = ConcurrentHashMap<UUID, GameProfile>()

    override fun getProfiles(): Collection<GameProfile> {
        TODO("Not yet implemented")
    }

    override fun getOrLookupByName(name: String): Optional<GameProfile> {
        TODO("Not yet implemented")
    }

    fun getOrLookupByNameAsync(name: String): CompletableFuture<GameProfile?> {
        val cachedProfile = this.getByName(name).orNull()
        if (cachedProfile != null)
            return CompletableFuture.completedFuture(cachedProfile)
        return this.service.getBasicProfile(name).thenApply { profile ->
            if (profile != null)
                this.add(profile)
            profile
        }
    }

    fun lookupByNameAsync(name: String): CompletableFuture<GameProfile?> =
            this.service.getBasicProfile(name)

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun getById(uniqueId: UUID): Optional<GameProfile> {
        TODO("Not yet implemented")
    }

    override fun lookupByIds(uniqueIds: Iterable<UUID>): Map<UUID, Optional<GameProfile>> {
        TODO("Not yet implemented")
    }

    override fun getByNames(names: Iterable<String>): Map<String, Optional<GameProfile>> {
        TODO("Not yet implemented")
    }

    override fun lookupByName(name: String): Optional<GameProfile> =
            this.lookupByNameAsync(name).get().asOptional()

    override fun streamProfiles(): Stream<GameProfile> {
        TODO("Not yet implemented")
    }

    override fun remove(profile: GameProfile): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(profiles: Iterable<GameProfile>): Collection<GameProfile> {
        TODO("Not yet implemented")
    }

    override fun getByIds(uniqueIds: Iterable<UUID>): Map<UUID, Optional<GameProfile>> {
        TODO("Not yet implemented")
    }

    override fun lookupByNames(names: Iterable<String>): Map<String, Optional<GameProfile>> {
        TODO("Not yet implemented")
    }

    override fun getByName(name: String?): Optional<GameProfile> {
        TODO("Not yet implemented")
    }

    override fun getOrLookupByIds(uniqueIds: Iterable<UUID>): Map<UUID, Optional<GameProfile>> {
        TODO("Not yet implemented")
    }

    override fun add(profile: GameProfile, overwrite: Boolean, expiry: Instant?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getOrLookupByNames(names: Iterable<String>): Map<String, Optional<GameProfile>> {
        TODO("Not yet implemented")
    }

    override fun fillProfile(profile: GameProfile, signed: Boolean): Optional<GameProfile> {
        TODO("Not yet implemented")
    }

    override fun match(name: String): Collection<GameProfile> {
        TODO("Not yet implemented")
    }

    override fun streamOfMatches(name: String): Stream<GameProfile> {
        TODO("Not yet implemented")
    }

    override fun lookupById(uniqueId: UUID): Optional<GameProfile> {
        TODO("Not yet implemented")
    }

    override fun getOrLookupById(uniqueId: UUID): Optional<GameProfile> {
        TODO("Not yet implemented")
    }

    fun getOrLookupByIdAsync(uniqueId: UUID): CompletableFuture<GameProfile?> {
        TODO("Not yet implemented")
    }
}
