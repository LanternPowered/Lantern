package org.lanternpowered.server.world.chunk;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.service.world.ChunkLoadService.EntityLoadingTicket;

class LanternEntityLoadingTicket extends LanternLoadingTicket implements EntityLoadingTicket {

    protected volatile Entity entity;

    public LanternEntityLoadingTicket(String plugin, LanternChunkManager chunkManager, int maxChunks) {
        super(plugin, chunkManager, maxChunks);
    }

    @Override
    public void bindToEntity(Entity arg0) {
        this.entity = arg0;
    }

    @Override
    public Entity getBoundEntity() {
        return this.entity;
    }
}
