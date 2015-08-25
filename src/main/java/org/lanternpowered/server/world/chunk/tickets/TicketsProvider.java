package org.lanternpowered.server.world.chunk.tickets;

public interface TicketsProvider {

    int getMaxTicketsFor(String plugin);

    int getMaxChunksForTicket(String plugin);

}
