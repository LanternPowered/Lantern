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
package org.lanternpowered.api.service.profile

import org.lanternpowered.api.profile.GameProfile
import org.lanternpowered.api.profile.ProfileProperty
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * A service which looks up [GameProfile] data for users.
 */
interface GameProfileService {

    /**
     * Attempts to lookup the basic [GameProfile] of the account with the given
     * unique id, at the given time if applied.
     *
     * A basic [GameProfile] doesn't contain any [ProfileProperty]s.
     *
     * @param uniqueId The unique id for which the unique id should be lookup up
     * @return The basic game profile, if the unique id is in use
     */
    fun getBasicProfile(uniqueId: UUID): CompletableFuture<GameProfile?>

    /**
     * Attempts to lookup the basic [GameProfile] of the account with the given
     * name, at the given time if applied.
     *
     * A basic [GameProfile] doesn't contain any [ProfileProperty]s.
     *
     * The requested name is matched case insensitive, the corrected name will be
     * present in the returned [GameProfile].
     *
     * @param name The name for which the unique id should be lookup up
     * @param time The time at which the name was assigned to a specific unique id
     * @return The basic profile, if the name is in use
     */
    fun getBasicProfile(name: String, time: Instant? = null): CompletableFuture<GameProfile?>

    /**
     * Attempts to lookup for the basic [GameProfile]s of the accounts with the given
     * names, at the given time if applied.
     *
     * A basic [GameProfile] doesn't contain any [ProfileProperty]s.
     *
     * The requested names are matched case insensitive, the corrected name will be
     * present in the returned [GameProfile].
     *
     * @param names The names for which the unique ids should be lookup up
     * @param time The time at which the name was assigned to a specific unique id
     * @return The basic profiles mapped by their request name, profiles are only present if they exist
     */
    fun getBasicProfiles(names: Iterable<String>, time: Instant? = null): CompletableFuture<Map<String, GameProfile>>

    /**
     * Gets the [NameHistory] for the account with the given unique id. If
     * the account exists.
     *
     * @param uniqueId The unique id of the account to lookup
     * @return The name history, or `null` if the account doesn't exist
     */
    fun getNameHistory(uniqueId: UUID): CompletableFuture<NameHistory?>

    /**
     * Gets the [GameProfile] for the account with the given unique id. If
     * the account exists.
     *
     * @param uniqueId The unique id of the account to lookup
     * @param signed Whether the properties of the profile should include their signatures
     * @return The profile, or `null` if the account doesn't exist
     */
    fun getProfile(uniqueId: UUID, signed: Boolean = true): CompletableFuture<GameProfile?>

    /**
     * Gets the [GameProfile] for the account with the given name. If
     * the account exists.
     *
     * @param name The name of the account to lookup
     * @param signed Whether the properties of the profile should include their signatures
     * @return The profile, or `null` if the account doesn't exist
     */
    fun getProfile(name: String, signed: Boolean = true): CompletableFuture<GameProfile?>
}
