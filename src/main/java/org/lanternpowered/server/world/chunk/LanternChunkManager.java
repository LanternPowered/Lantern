package org.lanternpowered.server.world.chunk;

import org.lanternpowered.server.world.chunk.tickets.LanternLoadingTickets;
import org.lanternpowered.server.world.chunk.tickets.TicketsProvider;

public class LanternChunkManager {

    private final LanternLoadingTickets tickets;

    public LanternChunkManager(TicketsProvider provider) {
        this.tickets = new LanternLoadingTickets(provider);
    }

    /**
     * Gets the loading tickets of the chunk manager.
     * 
     * @return the loading tickets
     */
    public LanternLoadingTickets getLoadingTickets() {
        return this.tickets;
    }

}
