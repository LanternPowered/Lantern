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
package org.lanternpowered.server.network.status;

import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.util.collect.Lists2;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.profile.GameProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class LanternStatusHelper {

    /**
     * The maximum amount of players that are by default displayed in the status ping.
     */
    private static final int DEFAULT_MAX_PLAYERS_DISPLAYED = 12;

    public static ClientPingServerEvent.Response.Players createPlayers(LanternServer server) {
        // Get the online players
        final Collection<LanternPlayer> players = server.getRawOnlinePlayers();

        final int online = players.size();
        final int max = server.getMaxPlayers();

        // Create a list with the players
        List<LanternPlayer> playersList = new ArrayList<>(players);

        // Randomize the players list
        Collections.shuffle(playersList);

        // Limit the maximum amount of displayed players
        if (playersList.size() > DEFAULT_MAX_PLAYERS_DISPLAYED) {
            playersList = playersList.subList(0, DEFAULT_MAX_PLAYERS_DISPLAYED);
        }

        // Get all the game profiles and create a copy
        final List<GameProfile> gameProfiles = Lists2.nonNullOf(playersList.stream()
                .map(player -> ((LanternGameProfile) player.getProfile()).copyWithoutProperties())
                .collect(Collectors.toList()));

        return SpongeEventFactory.createClientPingServerEventResponsePlayers(gameProfiles, max, online);
    }

    private LanternStatusHelper() {
    }
}
