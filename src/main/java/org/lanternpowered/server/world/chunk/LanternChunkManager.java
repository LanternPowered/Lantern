/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.world.chunk;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import org.lanternpowered.server.data.io.ChunkIOService;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.util.gen.ShortArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.ShortArrayMutableBlockBuffer;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.chunk.LanternChunk.ChunkSection;
import org.lanternpowered.server.world.chunk.LanternEntityLoadingTicket.EntityReference;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.world.chunk.ForcedChunkEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.world.ChunkLoadService.Callback;
import org.spongepowered.api.service.world.ChunkLoadService.EntityLoadingTicket;
import org.spongepowered.api.service.world.ChunkLoadService.LoadingTicket;
import org.spongepowered.api.service.world.ChunkLoadService.OrderedCallback;
import org.spongepowered.api.service.world.ChunkLoadService.PlayerEntityLoadingTicket;
import org.spongepowered.api.service.world.ChunkLoadService.PlayerLoadingTicket;
import org.spongepowered.api.service.world.ChunkLoadService.PlayerOrderedCallback;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.gen.BiomeGenerator;
import org.spongepowered.api.world.gen.GeneratorPopulator;
import org.spongepowered.api.world.gen.WorldGenerator;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkPlugin;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_AREA_SIZE;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_SIZE;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_SECTIONS;

public class LanternChunkManager {

    private final ConcurrentMap<Vector2i, Set<LanternLoadingTicket>> ticketsByPos = Maps.newConcurrentMap();
    private final Set<LanternLoadingTicket> tickets = Collections.newSetFromMap(new MapMaker()
            .initialCapacity(1000).weakKeys().<LanternLoadingTicket, Boolean>makeMap());

    private final Set<LanternChunk> forcedChunks = Sets.newConcurrentHashSet();
    private final LoadingCache<Vector2i, LanternChunk> chunks = CacheBuilder.newBuilder()
            .weakValues()
            .removalListener(new RemovalListener<Vector2i, LanternChunk>() {
                @Override
                public void onRemoval(RemovalNotification<Vector2i, LanternChunk> notification) {
                    save(notification.getValue());
                }
            })
            .build(new CacheLoader<Vector2i, LanternChunk>() {
                @Override
                public LanternChunk load(Vector2i key) throws Exception {
                    return new LanternChunk(world, key.getX(), key.getY());
                }
            });
    private final ConcurrentLinkedQueue<Vector2i> chunkLoadingQueue = new ConcurrentLinkedQueue<Vector2i>();

    private final LanternChunkLoadService chunkLoadService;
    private final ChunkIOService chunkIOService;
    private final LanternWorld world;
    private final File worldFolder;

    private WorldGenerator worldGenerator;

    public LanternChunkManager(LanternWorld world, LanternChunkLoadService chunkLoadService,
            ChunkIOService chunkIOService, WorldGenerator worldGenerator, File worldFolder) {
        this.chunkLoadService = checkNotNull(chunkLoadService, "chunkLoadService");
        this.chunkIOService = checkNotNull(chunkIOService, "chunkIOService");
        this.worldGenerator = checkNotNull(worldGenerator, "worldGenerator");
        this.worldFolder = checkNotNull(worldFolder, "worldFolder");
        this.world = checkNotNull(world, "world");
    }

    public void setWorldGenerator(WorldGenerator worldGenerator) {
        this.worldGenerator = checkNotNull(worldGenerator, "worldGenerator");
    }

    public WorldGenerator getWorldGenerator() {
        return this.worldGenerator;
    }

    void loadTickets() throws IOException {
        Multimap<String, LanternLoadingTicket> tickets = LanternLoadingTicketIO.load(this.worldFolder, this, this.chunkLoadService);
        for (Entry<String, Collection<LanternLoadingTicket>> ticket : tickets.asMap().entrySet()) {
            if (ticket instanceof LanternEntityLoadingTicket) {
                LanternEntityLoadingTicket ticket0 = (LanternEntityLoadingTicket) ticket;
                if (ticket0.getBoundEntity() != null) {
                    EntityReference ref = ticket0.entityRef;
                    if (ref != null) {
                        LanternChunk chunk = this.getOrLoadChunk(ref.chunkCoords);
                        Entity entity = chunk.getEntity(ref.uniqueId);
                        if (entity != null) {
                            ticket0.bindToEntity(entity);
                        }
                    }
                }
            }
        }
        for (Entry<String, Collection<LanternLoadingTicket>> ticket : tickets.asMap().entrySet()) {
            Collection<Callback> callbacks = this.chunkLoadService.getCallbacks().get(ticket.getKey());
            ImmutableList<LoadingTicket> tickets0 = ImmutableList.copyOf(ticket.getValue());
            ImmutableListMultimap<UUID, LoadingTicket> map = null;
            for (Callback callback : callbacks) {
                if (callback instanceof OrderedCallback) {
                    tickets0 = ImmutableList.copyOf(((OrderedCallback) callback).onLoaded(
                            tickets0, this.world, this.chunkLoadService.getMaxTicketsForPlugin(ticket.getKey())));
                }
                if (callback instanceof PlayerOrderedCallback) {
                    if (map == null) {
                        ImmutableListMultimap.Builder<UUID, LoadingTicket> mapBuilder = ImmutableListMultimap.builder();
                        for (LoadingTicket ticket0 : tickets0) {
                            if (ticket0 instanceof PlayerLoadingTicket) {
                                mapBuilder.put(((PlayerLoadingTicket) ticket0).getPlayerUniqueId(), ticket0);
                            }
                        }
                        map = mapBuilder.build();
                    }
                    map = ImmutableListMultimap.copyOf(((PlayerOrderedCallback) callback).onPlayerLoaded(map, this.world));
                }
                callback.onLoaded(tickets0, this.world);
            }
        }
    }

    void force(LanternLoadingTicket ticket, Vector2i coords) {
        this.force(ticket, coords, true);
    }

    void force(LanternLoadingTicket ticket, Vector2i coords, boolean callEvents) {
        Set<LanternLoadingTicket> set = this.ticketsByPos.get(ticket);
        if (set == null) {
            set = Sets.newConcurrentHashSet();
            Set<LanternLoadingTicket> set0 = this.ticketsByPos.putIfAbsent(coords, set);
            if (set0 != null) {
                set = set0;
            } else if (!this.chunkLoadingQueue.contains(coords)) {
                // Queue the chunk for loading
                this.chunkLoadingQueue.add(coords);
            }
            if (callEvents) {
                LanternChunk chunk = this.getChunk(coords);
                if (chunk != null) {
                    Vector3i coords0 = LanternChunk.fromVector2(coords);
                    ForcedChunkEvent event = SpongeEventFactory.createForcedChunkEvent(
                            LanternGame.get(), coords0, chunk, ticket);
                    LanternGame.get().getEventManager().post(event);
                }
            }
        }
        set.add(ticket);
    }

    void release(LanternLoadingTicket ticket, Vector2i coords) {
        Set<LanternLoadingTicket> set = this.ticketsByPos.get(ticket);
        if (set != null) {
            set.remove(ticket);
            if (set.isEmpty()) {
                this.ticketsByPos.remove(coords);
                this.chunkLoadingQueue.remove(coords);
                // Remove the chunk from loading if it's not done yet
                LanternChunk chunk = this.getChunk(coords);
                if (chunk != null) {
                    this.forcedChunks.remove(chunk);
                }
            }
        }
    }

    public void pulse() {
        Vector2i coords;
        while ((coords = this.chunkLoadingQueue.poll()) != null) {
            LanternChunk chunk = this.getOrLoadChunk(coords, true);
            this.forcedChunks.add(chunk);
            Set<LanternLoadingTicket> set = this.ticketsByPos.get(coords);
            if (set != null && !set.isEmpty()) {
                Vector3i coords0 = LanternChunk.fromVector2(coords);
                for (LanternLoadingTicket ticket : set) {
                    ForcedChunkEvent event = SpongeEventFactory.createForcedChunkEvent(
                            LanternGame.get(), coords0, chunk, ticket);
                    LanternGame.get().getEventManager().post(event);
                }
            }
        }
    }

    /**
     * Creates a new loading ticket.
     * 
     * @param plugin the plugin
     * @return the loading ticket if available
     */
    public Optional<LoadingTicket> createTicket(Object plugin) {
        PluginContainer container = checkPlugin(plugin, "plugin");
        if (this.getTicketsForPlugin(container) >= this.chunkLoadService.getMaxTicketsForPlugin(container)) {
            return Optional.empty();
        }
        int chunks = this.chunkLoadService.getMaxChunksForPluginTicket(container);
        LanternLoadingTicket ticket = new LanternLoadingTicket(container.getId(), this, chunks);
        this.tickets.add(ticket);
        return Optional.of(ticket);
    }

    /**
     * Creates a new entity loading ticket.
     * 
     * @param plugin the plugin
     * @return the loading ticket if available
     */
    public Optional<EntityLoadingTicket> createEntityTicket(Object plugin) {
        PluginContainer container = checkPlugin(plugin, "plugin");
        if (this.getTicketsForPlugin(container) >= this.chunkLoadService.getMaxTicketsForPlugin(container)) {
            return Optional.empty();
        }
        int chunks = this.chunkLoadService.getMaxChunksForPluginTicket(container);
        LanternEntityLoadingTicket ticket = new LanternEntityLoadingTicket(container.getId(), this, chunks);
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
    public Optional<PlayerLoadingTicket> createPlayerTicket(Object plugin, UUID player) {
        checkNotNull(player, "player");
        PluginContainer container = checkPlugin(plugin, "plugin");
        if (this.getTicketsForPlugin(container) >= this.chunkLoadService.getMaxTicketsForPlugin(container) ||
                this.chunkLoadService.getAvailableTickets(player) == 0) {
            return Optional.empty();
        }
        int chunks = this.chunkLoadService.getMaxChunksForPluginTicket(container);
        LanternPlayerLoadingTicket ticket = new LanternPlayerLoadingTicket(container.getId(), this, player, chunks);
        this.tickets.add(ticket);
        return Optional.<PlayerLoadingTicket>of(ticket);
    }

    /**
     * Creates a new player entity loading ticket.
     * 
     * @param plugin the plugin
     * @return the loading ticket if available
     */
    public Optional<PlayerEntityLoadingTicket> createPlayerEntityTicket(Object plugin, UUID player) {
        checkNotNull(player, "player");
        PluginContainer container = checkPlugin(plugin, "plugin");
        if (this.getTicketsForPlugin(container) >= this.chunkLoadService.getMaxTicketsForPlugin(container) ||
                this.chunkLoadService.getAvailableTickets(player) == 0) {
            return Optional.empty();
        }
        int chunks = this.chunkLoadService.getMaxChunksForPluginTicket(container);
        LanternPlayerEntityLoadingTicket ticket = new LanternPlayerEntityLoadingTicket(container.getId(),
                this, player, chunks);
        this.tickets.add(ticket);
        return Optional.<PlayerEntityLoadingTicket>of(ticket);
    }

    /**
     * Gets the amount of tickets that are attached to the plugin.
     * 
     * @param plugin the plugin
     * @return the tickets
     */
    public int getTicketsForPlugin(PluginContainer plugin) {
        checkNotNull(plugin, "plugin");
        int count = 0;
        String id = plugin.getId();
        for (LanternLoadingTicket ticket : this.tickets) {
            if (ticket.getPlugin().equals(id)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the amount of tickets that are attached to the player.
     * 
     * @param player the player uuid
     * @return the tickets
     */
    public int getTicketsForPlayer(UUID player) {
        checkNotNull(player, "player");
        int count = 0;
        for (LanternLoadingTicket ticket : this.tickets) {
            if (ticket instanceof PlayerLoadingTicket && player.equals(((PlayerLoadingTicket) ticket).getPlayerUniqueId())) {
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
     * Gets a immutable set with all the loaded chunks.
     * 
     * @return the loaded chunks
     */
    public ImmutableSet<Chunk> getLoadedChunks() {
        ImmutableSet.Builder<Chunk> builder = ImmutableSet.builder();
        for (LanternChunk chunk : this.chunks.asMap().values()) {
            if (chunk.isLoaded()) {
                builder.add(chunk);
            }
        }
        return builder.build();
    }

    /**
     * Gets a chunk for the coordinates, may not be loaded yet.
     * 
     * @param coords the coordinates
     * @return the chunk
     */
    @Nullable
    public LanternChunk getChunk(Vector3i coords) {
        return this.getChunk(coords.toVector2(true));
    }

    /**
     * Gets a chunk for the coordinates, may not be loaded yet.
     * 
     * @param coords the coordinates
     * @return the chunk
     */
    @Nullable
    public LanternChunk getChunk(Vector2i coords) {
        return this.chunks.getIfPresent(coords);
    }

    /**
     * Gets a chunk for the coordinates, may not be loaded yet.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the chunk
     */
    @Nullable
    public LanternChunk getChunk(int x, int z) {
        return this.getChunk(new Vector2i(x, z));
    }

    /**
     * Gets a chunk safely (new one will be created) for the coordinates, may
     * not be loaded yet.
     * 
     * @param coords the coordinates
     * @return the chunk
     */
    public LanternChunk getOrCreateChunk(Vector2i coords) {
        try {
            return this.chunks.get(coords);
        } catch (ExecutionException e) {
            throw new RuntimeException(e); // This shouldn't happen
        }
    }

    /**
     * Gets a chunk safely (new one will be created) for the coordinates, may
     * not be loaded yet.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the chunk
     */
    public LanternChunk getOrCreateChunk(int x, int z) {
        return this.getOrCreateChunk(new Vector2i(x, z));
    }

    /**
     * Gets a chunk for the coordinates.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the chunk
     */
    @Nullable
    public LanternChunk getOrLoadChunkIfPresent(int x, int z) {
        return this.getOrLoadChunkIfPresent(new Vector2i(x, z));
    }

    /**
     * Gets a chunk for the coordinates.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the chunk
     */
    @Nullable
    public LanternChunk getOrLoadChunkIfPresent(Vector2i coords) {
        LanternChunk chunk = this.getChunk(coords);
        if (chunk == null) {
            return null;
        }
        this.load(chunk, false);
        return chunk;
    }

    /**
     * Gets a chunk safely (new one will be created) for the coordinates.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the chunk
     */
    public LanternChunk getOrLoadChunk(int x, int z) {
        return this.getOrLoadChunk(new Vector2i(x, z));
    }

    /**
     * Gets a chunk safely (new one will be created) for the coordinates.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the chunk
     */
    public LanternChunk getOrLoadChunk(int x, int z, boolean generate) {
        return this.getOrLoadChunk(new Vector2i(x, z), generate);
    }

    /**
     * Gets a chunk safely (new one will be created) for the coordinates.
     * 
     * @param coords the coordinates
     * @return the chunk
     */
    public LanternChunk getOrLoadChunk(Vector2i coords) {
        return this.getOrLoadChunk(coords, false);
    }

    /**
     * Gets a chunk safely (new one will be created) for the coordinates.
     * 
     * @param coords the coordinates
     * @param generate whether the chunk should be generated if not done
     * @return the chunk
     */
    public LanternChunk getOrLoadChunk(Vector2i coords, boolean generate) {
        LanternChunk chunk = this.getOrCreateChunk(coords);
        chunk.loadChunk(generate);
        return chunk;
    }

    public boolean load(int x, int z, boolean generate) {
        return this.load(this.getOrCreateChunk(x, z), generate);
    }

    public boolean unload(int x, int z) {
        LanternChunk chunk = this.getChunk(x, z);
        if (chunk != null) {
            return this.unload(chunk);
        }
        return false;
    }

    boolean unload(LanternChunk chunk) {
        return false;
    }

    boolean save(LanternChunk chunk) {
        if (chunk.isLoaded()) {
            try {
                this.chunkIOService.write(chunk);
                return true;
            } catch (IOException e) {
                LanternGame.log().error("Error while saving " + chunk, e);
            }
        }
        return false;
    }

    boolean load(LanternChunk chunk, boolean generate) {
        // Try to load chunk
        try {
            if (this.chunkIOService.read(chunk)) {
                // TODO: Add cause
                LanternGame.get().getEventManager().post(SpongeEventFactory.createLoadChunkEvent(
                        LanternGame.get(), null, chunk));
                return true;
            }
        } catch (Exception e) {
            LanternGame.log().error("Error while loading chunk (" + chunk.getX() + "," + chunk.getZ() + ")", e);
            // an error in chunk reading may have left the chunk in an invalid state
            // (i.e. double initialization errors), so it's forcibly unloaded here
            chunk.unloadChunk();
        }
        // Stop here if we can't generate
        if (!generate) {
            chunk.initializeEmpty();
            return false;
        }
        try {
            this.generate(chunk);
        } catch (Throwable e) {
            LanternGame.log().error("Error while generating chunk (" + chunk.getX() + "," + chunk.getZ() + ")", e);
            return false;
        }
        // TODO: Add cause
        LanternGame.get().getEventManager().post(SpongeEventFactory.createLoadChunkEvent(
                LanternGame.get(), Cause.empty(), chunk));
        return true;
    }

    void generate(LanternChunk chunk) {
        ShortArrayMutableBiomeBuffer biomes = new ShortArrayMutableBiomeBuffer(Vector2i.ZERO, CHUNK_AREA_SIZE);

        BiomeGenerator biomeGenerator = this.worldGenerator.getBiomeGenerator();
        biomeGenerator.generateBiomes(biomes);

        chunk.initializeBiomes(biomes.detach());

        ShortArrayMutableBlockBuffer blocks = new ShortArrayMutableBlockBuffer(Vector3i.ZERO, CHUNK_SIZE);
        ImmutableBiomeArea biomeBuffer = biomes.getImmutableBiomeCopy();

        GeneratorPopulator generator = this.worldGenerator.getBaseGeneratorPopulator();
        generator.populate(this.world, blocks, biomeBuffer);

        // Apply the generator populators to complete the blockBuffer
        for (GeneratorPopulator populator : this.worldGenerator.getGeneratorPopulators()) {
            populator.populate(this.world, blocks, biomeBuffer);
        }

        // Get unique biomes to determine what generator populators to run
        List<BiomeType> uniqueBiomes = Lists.newArrayList();
        BiomeType biome;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                biome = biomes.getBiome(x, z);
                if (!uniqueBiomes.contains(biome)) {
                    uniqueBiomes.add(biome);
                }
            }
        }
        
        // Run our generator populators, checking for overrides from the generator
        for (BiomeType type: uniqueBiomes) {
            for (GeneratorPopulator populator : type.getGeneratorPopulators()) {
                populator.populate(this.world, blocks, biomeBuffer);
            }
        }

        short[] blocksArray = blocks.getArray();

        ChunkSection[] sections = new ChunkSection[CHUNK_SECTIONS];
        for (int sy = 0; sy < sections.length; ++sy) {
            int y = sy << 4;
            int start = blocks.getIndex(0, y, 0);
            int end = blocks.getIndex(15, y | 15, 15);

            short[] sectionBlocks = Arrays.copyOfRange(blocksArray, start, end + 1);
            sections[sy] = new ChunkSection(sectionBlocks);
        }

        chunk.initializeSections(sections);
        chunk.automaticHeightMap();
    }
}
