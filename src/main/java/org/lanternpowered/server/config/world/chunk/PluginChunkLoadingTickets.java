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

@ConfigSerializable
public class PluginChunkLoadingTickets implements ChunkLoadingTickets {

    @Setting(value = ChunkLoading.MAXIMUM_CHUNKS_PER_TICKET, comment =
            "Maximum chunks per ticket for the plugin.")
    private int maximumChunksPerTicket = 25;

    @Setting(value = ChunkLoading.MAXIMUM_TICKET_COUNT, comment =
            "Maximum ticket count for the mod. Zero disables chunkloading capabilities.")
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
