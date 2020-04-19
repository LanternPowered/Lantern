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
public class WorldChunkLoadingTickets implements ChunkLoadingTickets {

    @Setting(value = ChunkLoading.MAXIMUM_CHUNKS_PER_TICKET, comment =
            "The default maximum number of chunks a plugin can force, per ticket, for a plugin\n " +
            "without an override. This is the maximum number of chunks a single ticket can force.")
    private int maximumChunksPerTicket = 25;

    @Setting(value = ChunkLoading.MAXIMUM_TICKET_COUNT, comment =
            "The default maximum ticket count for a plugin which does not have an override\n " +
            "in this file. This is the number of chunk loading requests a plugin is allowed to make.")
    private int maximumTicketCount = 200;

    @Override
    public int getMaximumChunksPerTicket() {
        return this.maximumChunksPerTicket;
    }

    @Override
    public int getMaximumTicketCount() {
        return this.maximumTicketCount;
    }

}
