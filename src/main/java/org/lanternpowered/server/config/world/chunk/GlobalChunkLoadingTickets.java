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
package org.lanternpowered.server.config.world.chunk;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Global/default settings of the chunk loading, all these settings are available in
 * the global config and the world specific configs.
 */
@ConfigSerializable
public class GlobalChunkLoadingTickets extends WorldChunkLoadingTickets {

    @Setting(value = ChunkLoading.PLAYER_TICKET_COUNT, comment =
            "The number of tickets a player can be assigned instead of a plugin. This is shared\n" +
            "across all plugins.")
    private int playerTicketCount = 500;

    /**
     * Gets the maximum amount of tickets that can be requested
     * per player.
     * 
     * TODO: Make this configurable per world? This is currently not possible using the current api.
     * 
     * @return the player ticket count
     */
    public int getPlayerTicketCount() {
        return this.playerTicketCount;
    }

}
