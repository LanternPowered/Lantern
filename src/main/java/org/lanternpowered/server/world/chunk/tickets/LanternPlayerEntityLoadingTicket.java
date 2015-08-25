package org.lanternpowered.server.world.chunk.tickets;

import java.util.UUID;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.service.world.ChunkLoadService.PlayerEntityLoadingTicket;

class LanternPlayerEntityLoadingTicket extends LanternEntityLoadingTicket implements PlayerEntityLoadingTicket {

    private final UUID uniqueId;

    public LanternPlayerEntityLoadingTicket(String plugin, LanternLoadingTickets tickets, UUID uniqueId, int maxChunks) {
        super(plugin, tickets, maxChunks);
        this.uniqueId = uniqueId;
    }

    @Override
    public void bindToEntity(Entity entity) {
        if (entity == null || !entity.getUniqueId().equals(this.uniqueId)) {
            throw new IllegalArgumentException("Only a player with the uuid (" + this.uniqueId + ") can be applied to this ticket!");
        }

        super.bindToEntity(entity);
    }

    @Override
    public UUID getPlayerUniqueId() {
        return this.uniqueId;
    }

}
