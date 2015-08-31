package org.lanternpowered.server.world.chunk;

import java.util.UUID;

import org.spongepowered.api.service.world.ChunkLoadService.PlayerLoadingTicket;

class LanternPlayerLoadingTicket extends LanternLoadingTicket implements PlayerLoadingTicket {

    private final UUID uuid;

    public LanternPlayerLoadingTicket(String plugin, LanternChunkManager chunkManager, UUID uuid, int maxChunks) {
        super(plugin, chunkManager, maxChunks);
        this.uuid = uuid;
    }

    @Override
    public UUID getPlayerUniqueId() {
        return this.uuid;
    }
}
