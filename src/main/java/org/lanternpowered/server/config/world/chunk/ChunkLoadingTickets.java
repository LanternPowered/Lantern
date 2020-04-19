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

public interface ChunkLoadingTickets {

    /**
     * Gets the maximum amount of chunks that can
     * be forced to load per ticket.
     * 
     * @return the maximum amount of chunks per ticket
     */
    int getMaximumChunksPerTicket();

    /**
     * Gets the maximum amount of tickets that a
     * plugin can request.
     * 
     * @return the maximum count of tickets
     */
    int getMaximumTicketCount();

}
