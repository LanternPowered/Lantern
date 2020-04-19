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

/**
 * The internal Minecraft plugin has no chunk loading limits.
 */
public final class MinecraftChunkLoadingTickets implements ChunkLoadingTickets {

    @Override
    public int getMaximumChunksPerTicket() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaximumTicketCount() {
        return Integer.MAX_VALUE;
    }

}
