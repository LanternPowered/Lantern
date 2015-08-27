package org.lanternpowered.server.world.chunk.tickets;

import java.util.Collections;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.lanternpowered.server.world.chunk.LanternChunk;
import org.spongepowered.api.service.world.ChunkLoadService.EntityLoadingTicket;
import org.spongepowered.api.service.world.ChunkLoadService.LoadingTicket;
import org.spongepowered.api.service.world.ChunkLoadService.PlayerEntityLoadingTicket;
import org.spongepowered.api.service.world.ChunkLoadService.PlayerLoadingTicket;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import static com.google.common.base.Preconditions.checkNotNull;

public final class LanternLoadingTickets {

    private final ConcurrentMap<Vector2i, Set<LanternLoadingTicket>> ticketsByPos = Maps.newConcurrentMap();
    private final Set<LanternLoadingTicket> tickets = Collections.newSetFromMap(new MapMaker()
            .initialCapacity(1000).weakKeys().<LanternLoadingTicket, Boolean>makeMap());

    private final TicketsProvider provider;

    public LanternLoadingTickets(TicketsProvider provider) {
        this.provider = checkNotNull(provider, "provider");
    }

    /**
     * Creates a new loading ticket.
     * 
     * @param plugin the plugin
     * @return the loading ticket if available
     */
    public Optional<LoadingTicket> createTicket(String plugin) {
        checkNotNull(plugin, "plugin");
        if (this.getTicketsFor(plugin) >= this.provider.getMaxTicketsFor(plugin)) {
            return Optional.absent();
        }
        int chunks = this.provider.getMaxChunksForTicket(plugin);
        LanternLoadingTicket ticket = new LanternLoadingTicket(plugin, this, chunks);
        this.tickets.add(ticket);
        return Optional.<LoadingTicket>of(ticket);
    }

    /**
     * Creates a new entity loading ticket.
     * 
     * @param plugin the plugin
     * @return the loading ticket if available
     */
    public Optional<EntityLoadingTicket> createEntityTicket(String plugin) {
        checkNotNull(plugin, "plugin");
        if (this.getTicketsFor(plugin) >= this.provider.getMaxTicketsFor(plugin)) {
            return Optional.absent();
        }
        int chunks = this.provider.getMaxChunksForTicket(plugin);
        LanternEntityLoadingTicket ticket = new LanternEntityLoadingTicket(plugin, this, chunks);
        this.tickets.add(ticket);
        return Optional.<EntityLoadingTicket>of(ticket);
    }

    /**
     * Creates a new player loading ticket.
     * 
     * @param plugin the plugin
     * @param player the player uuid
     * @return the loading ticket if available
     */
    public Optional<PlayerLoadingTicket> createPlayerTicket(String plugin, UUID player) {
        checkNotNull(plugin, "plugin");
        checkNotNull(player, "player");
        if (this.getTicketsFor(plugin) >= this.provider.getMaxTicketsFor(plugin)) {
            return Optional.absent();
        }
        int chunks = this.provider.getMaxChunksForTicket(plugin);
        LanternPlayerLoadingTicket ticket = new LanternPlayerLoadingTicket(plugin, this, player, chunks);
        this.tickets.add(ticket);
        return Optional.<PlayerLoadingTicket>of(ticket);
    }

    /**
     * Creates a new player entity loading ticket.
     * 
     * @param plugin the plugin
     * @return the loading ticket if available
     */
    public Optional<PlayerEntityLoadingTicket> createPlayerEntityTicket(String plugin, UUID player) {
        checkNotNull(plugin, "plugin");
        checkNotNull(player, "player");
        if (this.getTicketsFor(plugin) >= this.provider.getMaxTicketsFor(plugin)) {
            return Optional.absent();
        }
        int chunks = this.provider.getMaxChunksForTicket(plugin);
        LanternPlayerEntityLoadingTicket ticket = new LanternPlayerEntityLoadingTicket(plugin, this, player, chunks);
        this.tickets.add(ticket);
        return Optional.<PlayerEntityLoadingTicket>of(ticket);
    }

    /**
     * Gets the amount of tickets that are attached to the plugin.
     * 
     * @param plugin the plugin
     * @return the tickets
     */
    public int getTicketsFor(String plugin) {
        checkNotNull(plugin, "plugin");
        int count = 0;
        for (LanternLoadingTicket ticket : this.tickets) {
            if (ticket.getPlugin().equals(plugin)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets a map with all the forced chunk and the assigned tickets.
     * 
     * @return the tickets
     */
    public ImmutableSetMultimap<Vector3i, LoadingTicket> getForced() {
        ImmutableSetMultimap.Builder<Vector3i, LoadingTicket> builder = ImmutableSetMultimap.builder();

        for (Entry<Vector2i, Set<LanternLoadingTicket>> en : this.ticketsByPos.entrySet()) {
            Vector3i pos = LanternChunk.fromVector2(en.getKey());
            for (LanternLoadingTicket ticket : en.getValue()) {
                builder.put(pos, ticket);
            }
        }

        return builder.build();
    }

    /**
     * Gets whether the chunk a ticket for the chunks has.
     * 
     * @param chunk the chunk
     * @return has ticket
     */
    public boolean hasTicket(Vector2i chunk) {
        return this.ticketsByPos.containsKey(checkNotNull(chunk, "chunk"));
    }

    /**
     * Gets whether the chunk a ticket for the chunks has.
     * 
     * @param chunk the chunk
     * @return has ticket
     */
    public boolean hasTicket(int x, int z) {
        return this.ticketsByPos.containsKey(new Vector2i(x, z));
    }

    /**
     * Forces the chunk for the ticket.
     * 
     * @param ticket the ticket
     * @param chunk the chunk
     */
    protected void force(LanternLoadingTicket ticket, Vector2i chunk) {
        Set<LanternLoadingTicket> set = this.ticketsByPos.get(ticket);
        if (set == null) {
            set = Sets.newConcurrentHashSet();
            Set<LanternLoadingTicket> set0 = this.ticketsByPos.putIfAbsent(chunk, set);
            if (set0 != null) {
                set = set0;
            }
        }
        set.add(ticket);
    }

    /**
     * Releases the chunk for the ticket.
     * 
     * @param ticket the ticket
     * @param chunk the chunk
     */
    protected void release(LanternLoadingTicket ticket, Vector2i chunk) {
        Set<LanternLoadingTicket> set = this.ticketsByPos.get(ticket);
        if (set != null) {
            set.remove(ticket);
            if (set.isEmpty()) {
                this.ticketsByPos.remove(chunk);
            }
        }
    }

}
