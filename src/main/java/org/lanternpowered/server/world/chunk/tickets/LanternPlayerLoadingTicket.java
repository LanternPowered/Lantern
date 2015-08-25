package org.lanternpowered.server.world.chunk.tickets;

import java.util.UUID;

import org.spongepowered.api.service.world.ChunkLoadService.PlayerLoadingTicket;

class LanternPlayerLoadingTicket extends LanternLoadingTicket implements PlayerLoadingTicket {

    private final UUID uuid;

    public LanternPlayerLoadingTicket(String plugin, LanternLoadingTickets tickets, UUID uuid, int maxChunks) {
        super(plugin, tickets, maxChunks);
        this.uuid = uuid;
    }

    @Override
    public UUID getPlayerUniqueId() {
        return this.uuid;
    }

}
