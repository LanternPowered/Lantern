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
package org.lanternpowered.server.world.chunk;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.world.chunk.ChunkTicketManager.PlayerLoadingTicket;

import java.util.UUID;

class LanternPlayerLoadingTicket extends LanternLoadingTicket implements PlayerLoadingTicket {

    private final UUID uniqueId;

    LanternPlayerLoadingTicket(String plugin, LanternChunkManager chunkManager,
            UUID uniqueId, int maxChunks) {
        super(plugin, chunkManager, maxChunks);
        this.uniqueId = uniqueId;
    }

    LanternPlayerLoadingTicket(String plugin, LanternChunkManager chunkManager,
            UUID uniqueId, int maxChunks, int numChunks) {
        super(plugin, chunkManager, maxChunks, numChunks);
        this.uniqueId = uniqueId;
    }

    @Override
    public UUID getPlayerUniqueId() {
        return this.uniqueId;
    }

    @Override
    MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().omitNullValues().add("player", this.uniqueId);
    }
}
