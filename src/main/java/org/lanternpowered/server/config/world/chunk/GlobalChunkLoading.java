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

import static org.lanternpowered.server.config.ConfigConstants.DEFAULTS;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class GlobalChunkLoading extends ChunkLoading {

    @Setting(value = DEFAULTS, comment = "Default configuration for chunk loading control.")
    private GlobalChunkLoadingTickets defaults = new GlobalChunkLoadingTickets();

    @Override
    protected ChunkLoadingTickets getDefaults() {
        return this.defaults;
    }

    /**
     * Gets the maximum amount of tickets that can be requested
     * per player.
     * 
     * TODO: Make this configurable per world?
     * 
     * @return the player ticket count
     */
    public int getPlayerTicketCount() {
        return this.defaults.getPlayerTicketCount();
    }

}
