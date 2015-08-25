package org.lanternpowered.server.world.chunk.tickets;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.service.world.ChunkLoadService.EntityLoadingTicket;

class LanternEntityLoadingTicket extends LanternLoadingTicket implements EntityLoadingTicket {

    protected volatile Entity entity;

    public LanternEntityLoadingTicket(String plugin, LanternLoadingTickets tickets, int maxChunks) {
        super(plugin, tickets, maxChunks);
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
