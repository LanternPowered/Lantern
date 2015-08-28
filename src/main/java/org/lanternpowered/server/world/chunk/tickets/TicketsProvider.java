package org.lanternpowered.server.world.chunk.tickets;

import java.util.UUID;

public interface TicketsProvider {

    int getMaxTicketsFor(String plugin);

    int getMaxChunksForTicket(String plugin);

    int getMaxTicketsFor(UUID playerUUID);

    int getAvailableTicketsFor(UUID playerUUID);
}
