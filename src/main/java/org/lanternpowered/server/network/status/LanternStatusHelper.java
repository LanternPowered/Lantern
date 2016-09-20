/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
