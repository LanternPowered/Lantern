package org.lanternpowered.server.world.chunk;

import java.util.UUID;

import org.spongepowered.api.service.world.ChunkLoadService.PlayerLoadingTicket;

class LanternPlayerLoadingTicket extends LanternLoadingTicket implements PlayerLoadingTicket {

    private final UUID uniqueId;

    LanternPlayerLoadingTicket(String plugin, LanternChunkManager chunkManager, UUID uniqueId, int maxChunks) {
        super(plugin, chunkManager, maxChunks);
        this.uniqueId = uniqueId;
    }

    LanternPlayerLoadingTicket(String plugin, LanternChunkManager chunkManager, UUID uniqueId, int maxChunks, int numChunks) {
        super(plugin, chunkManager, maxChunks, numChunks);
        this.uniqueId = uniqueId;
    }

    @Override
    public UUID getPlayerUniqueId() {
        return this.uniqueId;
    }
}
