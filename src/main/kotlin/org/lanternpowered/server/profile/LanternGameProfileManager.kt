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
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.util.executor.LanternExecutorService
import org.spongepowered.api.profile.GameProfileCache
import org.spongepowered.api.profile.GameProfileManager
import org.spongepowered.api.profile.ProfileNotFoundException
import org.spongepowered.api.profile.property.ProfileProperty
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture

class LanternGameProfileManager(
        private val asyncExecutor: LanternExecutorService,
        private val service: GameProfileService
) : GameProfileManager {

    private val defaultCache = LanternGameProfileCache(this.service)
    private var cache: GameProfileCache = this.defaultCache

    override fun getDefaultCache(): GameProfileCache = this.defaultCache
    override fun getCache(): GameProfileCache = this.cache

    override fun setCache(cache: GameProfileCache) {
        this.cache = cache
    }

    override fun getAllById(uniqueIds: Iterable<UUID>, useCache: Boolean): CompletableFuture<Map<UUID, Optional<GameProfile>>> {
        TODO("Not yet implemented")
    }

    override fun getAllByName(names: Iterable<String>, useCache: Boolean): CompletableFuture<Map<String, Optional<GameProfile>>> {
        TODO("Not yet implemented")
    }

    override fun fill(profile: GameProfile, signed: Boolean, useCache: Boolean): CompletableFuture<GameProfile> {
        TODO("Not yet implemented")
    }

    override fun get(uniqueId: UUID, useCache: Boolean): CompletableFuture<GameProfile> {
        if (useCache) {
            val cache = this.cache
            return if (cache is LanternGameProfileCache) {
                cache.getOrLookupByIdAsync(uniqueId).thenApply { profile ->
                    if (profile == null)
                        throw ProfileNotFoundException(uniqueId.toString())
                    profile
                }
            } else {
                this.asyncExecutor.submit {
                    cache.getOrLookupById(uniqueId).orNull() ?: throw ProfileNotFoundException(uniqueId.toString())
                }
            }
        }
        return this.service.getBasicProfile(uniqueId).thenApply { profile ->
            if (profile == null)
                throw ProfileNotFoundException(uniqueId.toString())
            profile
        }
    }

    override fun get(name: String, useCache: Boolean): CompletableFuture<GameProfile> {
        if (useCache) {
            val cache = this.cache
            return if (cache is LanternGameProfileCache) {
                cache.getOrLookupByNameAsync(name).thenApply { profile ->
                    if (profile == null)
                        throw ProfileNotFoundException(name)
                    profile
                }
            } else {
                this.asyncExecutor.submit {
                    cache.getOrLookupByName(name).orNull() ?: throw ProfileNotFoundException(name)
                }
            }
        }
        return this.service.getBasicProfile(name).thenApply { profile ->
            if (profile == null)
                throw ProfileNotFoundException(name)
            profile
        }
    }

    override fun createProfileProperty(name: String, value: String, signature: String?): ProfileProperty =
            LanternProfileProperty(name, value, signature)

    override fun createProfile(uniqueId: UUID, name: String?): GameProfile =
            LanternGameProfile(uniqueId, name)
}
