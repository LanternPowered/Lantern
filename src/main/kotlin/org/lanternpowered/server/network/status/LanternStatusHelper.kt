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
package org.lanternpowered.server.network.status

import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.util.collections.asNonNullList
import org.lanternpowered.server.LanternServer
import org.lanternpowered.server.profile.LanternGameProfile
import org.spongepowered.api.event.server.ClientPingServerEvent
import org.spongepowered.api.profile.GameProfile

object LanternStatusHelper {

    /**
     * The maximum amount of players that are by default displayed in the status ping.
     */
    private const val DEFAULT_MAX_PLAYERS_DISPLAYED = 12

    @JvmStatic
    fun createPlayers(server: LanternServer): ClientPingServerEvent.Response.Players {
        // Get the online players
        val players = server.rawOnlinePlayers
        val online = players.size
        val max = server.maxPlayers

        // Create a list with the players
        var playersList = players.toMutableList()

        // Randomize the players list
        playersList.shuffle()

        // Limit the maximum amount of displayed players
        if (playersList.size > DEFAULT_MAX_PLAYERS_DISPLAYED) {
            playersList = playersList.subList(0, DEFAULT_MAX_PLAYERS_DISPLAYED)
        }

        // Get all the game profiles and create a copy
        val gameProfiles: List<GameProfile> = playersList.asSequence()
                .map { player -> (player.profile as LanternGameProfile).copyWithoutProperties() }
                .toMutableList()
                .asNonNullList()
        return LanternEventFactory.createClientPingServerEventResponsePlayers(gameProfiles, max, online)
    }
}