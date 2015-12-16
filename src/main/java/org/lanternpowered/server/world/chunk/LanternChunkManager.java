/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
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
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Nullable;

import org.lanternpowered.server.config.world.WorldConfig;
import org.lanternpowered.server.data.io.ChunkIOService;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.util.gen.biome.ShortArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.block.AbstractMutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.AtomicShortArrayMutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.ShortArrayImmutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.ShortArrayMutableBlockBuffer;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.chunk.LanternChunk.ChunkSection;
import org.lanternpowered.server.world.extent.ExtentBufferHelper;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.world.chunk.ForcedChunkEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.ChunkTicketManager.EntityLoadingTicket;
import org.spongepowered.api.world.ChunkTicketManager.LoadingTicket;
import org.spongepowered.api.world.ChunkTicketManager.PlayerEntityLoadingTicket;
import org.spongepowered.api.world.ChunkTicketManager.PlayerLoadingTicket;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.biome.BiomeGenerationSettings;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.gen.BiomeGenerator;
import org.spongepowered.api.world.gen.GenerationPopulator;
import org.spongepowered.api.world.gen.WorldGenerator;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkPlugin;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_AREA_SIZE;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTION_SIZE;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTION_VOLUME;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTIONS;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_HEIGHT;

public final class LanternChunkManager {

    // The maximum amount of chunks that may be loaded during
    // a game tick
    private static final int CHUNK_LOADING_LIMIT = 100;

    // All the attached tickets mapped by the forced chunk coordinates
    private final Map<Vector2i, Set<LanternLoadingTicket>> ticketsByPos =
            Maps.newConcurrentMap();
    // All the loading tickets that are still usable
    private final Set<LanternLoadingTicket> tickets =
            Sets.newConcurrentHashSet();

    // All the chunks that are loaded into the server
    private final Map<Vector2i, LanternChunk> loadedChunks =
            Maps.newConcurrentMap();

    // A cache that can be used to get chunks that weren't unloaded
    // so much after all, because of active references to the chunk
    private final Map<Vector2i, LanternChunk> reusableChunks =
            new MapMaker().weakValues().makeMap();

    // A queue of all the chunks that should be loaded
    private final ConcurrentLinkedQueue<Vector2i> forcedChunkLoadingQueue =
            new ConcurrentLinkedQueue<>();

    // The game instance
    private final LanternGame game;

    // The world
    private final LanternWorld world;

    // The world configuration
    private final WorldConfig worldConfig;

    // The chunk I/O service
    private final ChunkIOService chunkIOService;

    // The chunk load (ticket) service
    private final LanternChunkTicketManager chunkLoadService;

    // The world folder
    private final Path worldFolder;

    // The biome buffers that will be reused
    private final ThreadLocal<ChunkBiomeBuffer> biomeBuffer = ThreadLocal.withInitial(() -> {
                final ChunkBiomeBuffer buffer = new ChunkBiomeBuffer(
                        Vector2i.ZERO, CHUNK_AREA_SIZE);
                buffer.detach();
                return buffer;
            });

    // The block buffers that will be reused
    private final ThreadLocal<ChunkBlockBuffer> blockBuffer = ThreadLocal.withInitial(ChunkBlockBuffer::new);

    // The world generator
    private volatile WorldGenerator worldGenerator;

    /**
     * Creates a new chunk manager.
     * 
     * @param game the game instance
     * @param world the world this chunk manage is attached to
     * @param worldConfig the configuration file of the world
     * @param chunkLoadService the chunk load (ticket) service
     * @param chunkIOService the chunk i/o service
     * @param worldGenerator the world generator
     * @param worldFolder2 the world data folder
     */
    public LanternChunkManager(LanternGame game, LanternWorld world, WorldConfig worldConfig,
            LanternChunkTicketManager chunkLoadService, ChunkIOService chunkIOService,
            WorldGenerator worldGenerator, Path worldFolder) {
        this.chunkLoadService = chunkLoadService;
        this.chunkIOService = chunkIOService;
        this.worldGenerator = worldGenerator;
        this.worldFolder = worldFolder;
        this.worldConfig = worldConfig;
        this.world = world;
        this.game = game;
    }

    /**
     * Sets the generator of the world (chunk manager).
     * 
     * @param worldGenerator the world generator
     */
    public void setWorldGenerator(WorldGenerator worldGenerator) {
        this.worldGenerator = checkNotNull(worldGenerator, "worldGenerator");
    }

    /**
     * Gets the generator of the world (chunk manager).
     * 
     * @return the world generator
     */
    public WorldGenerator getWorldGenerator() {
        return this.worldGenerator;
    }

    /**
     * Gets whether a loading ticket exists for the chunk
     * at the specified coordinates.
     * 
     * @param coords the coordinates
     * @return has ticket
     */
    public boolean hasTicket(Vector2i coords) {
        return this.ticketsByPos.containsKey(checkNotNull(coords, "coords"));
    }

    /**
     * Gets whether a loading ticket exists for the chunk
     * at the specified coordinates.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return has ticket
     */
    public boolean hasTicket(int x, int z) {
        return this.ticketsByPos.containsKey(new Vector2i(x, z));
    }

    /**
     * Gets a map with all the forced chunk and the assigned tickets.
     * 
     * @return the tickets
     */
    public ImmutableSetMultimap<Vector3i, LoadingTicket> getForced() {
        final ImmutableSetMultimap.Builder<Vector3i, LoadingTicket> builder =
                ImmutableSetMultimap.builder();
        for (Entry<Vector2i, Set<LanternLoadingTicket>> en : this.ticketsByPos.entrySet()) {
            final Vector2i pos0 = en.getKey();
            final Vector3i pos = new Vector3i(pos0.getX(), 0, pos0.getY());
            for (LanternLoadingTicket ticket : en.getValue()) {
                builder.put(pos, ticket);
            }
        }
        return builder.build();
    }

    /**
     * Gets the amount of tickets that are attached to the player.
     * 
     * @param player the player uuid
     * @return the tickets
     */
    public int getTicketsForPlayer(UUID player) {
        checkNotNull(player, "player");
        return (int) this.tickets.stream().filter(ticket -> ticket instanceof PlayerLoadingTicket &&
                player.equals(((PlayerLoadingTicket) ticket).getPlayerUniqueId())).count();
    }

    /**
     * Gets the amount of tickets that are attached to the plugin.
     * 
     * @param plugin the plugin
     * @return the tickets
     */
    public int getTicketsForPlugin(Object plugin) {
        return this.getTicketsForPlugin(checkPlugin(plugin, "plugin").getId());
    }

    /**
     * Gets the amount of tickets that are attached to the plugin.
     * 
     * @param pluginId the plugin id
     * @return the tickets
     */
    public int getTicketsForPlugin(String pluginId) {
        checkNotNull(pluginId, "pluginId");
        return (int) this.tickets.stream().filter(ticket -> ticket.getPlugin().equals(pluginId)).count();
    }

    /**
     * Gets the maximum amount of tickets for the plugin per world.
     * 
     * @param plugin the plugin
     * @return the maximum amount of tickets
     */
    public int getMaxTicketsForPlugin(Object plugin) {
        return this.getMaxTicketsForPlugin(checkPlugin(plugin, "plugin").getId());
    }

    /**
     * Gets the maximum amount of tickets for the plugin per world.
     * 
     * @param plugin the plugin
     * @return the maximum amount of tickets
     */
    public int getMaxTicketsForPlugin(String plugin) {
        return this.worldConfig.getChunkLoadingTickets(plugin).getMaximumTicketCount();
    }

    /**
     * Gets the maximum amount of forced chunks each ticket of the plugin can contain.
     * 
     * @param plugin the plugin
     * @return the maximum amount of forced chunks
     */
    public int getMaxChunksForPluginTicket(Object plugin) {
        return this.getMaxChunksForPluginTicket(checkPlugin(plugin, "plugin").getId());
    }

    /**
     * Gets the maximum amount of forced chunks each ticket of the plugin can contain.
     * 
     * @param plugin the plugin
     * @return the maximum amount of forced chunks
     */
    public int getMaxChunksForPluginTicket(String plugin) {
        return this.worldConfig.getChunkLoadingTickets(plugin).getMaximumChunksPerTicket();
    }

    /**
     * Attempts to create a new loading ticket for the specified plugin.
     * 
     * @param plugin the plugin
     * @return the loading ticket if available
     */
    public Optional<LoadingTicket> createTicket(Object plugin) {
        final String pluginId = checkPlugin(plugin, "plugin").getId();
        if (this.getTicketsForPlugin(pluginId) >= this.getMaxTicketsForPlugin(pluginId)) {
            return Optional.empty();
        }
        final int maxChunks = this.getMaxChunksForPluginTicket(pluginId);
        final LanternLoadingTicket ticket = new LanternLoadingTicket(pluginId, this, maxChunks);
        this.tickets.add(ticket);
        return Optional.of(ticket);
    }

    /**
     * Attempts to create a new entity loading ticket for the specified plugin.
     * 
     * @param plugin the plugin
     * @return the loading ticket if available
     */
    public Optional<EntityLoadingTicket> createEntityTicket(Object plugin) {
        final String pluginId = checkPlugin(plugin, "plugin").getId();
        if (this.getTicketsForPlugin(pluginId) >= this.getMaxTicketsForPlugin(pluginId)) {
            return Optional.empty();
        }
        final int maxChunks = this.getMaxChunksForPluginTicket(pluginId);
        final LanternEntityLoadingTicket ticket = new LanternEntityLoadingTicket(
                pluginId, this, maxChunks);
        this.tickets.add(ticket);
        return Optional.of(ticket);
    }

    /**
     * Attempts to create a new player loading ticket for the specified plugin.
     * 
     * @param plugin the plugin
     * @param player the unique id of the player
     * @return the loading ticket if available
     */
    public Optional<PlayerLoadingTicket> createPlayerTicket(Object plugin, UUID player) {
        checkNotNull(player, "player");
        final String pluginId = checkPlugin(plugin, "plugin").getId();
        if (this.getTicketsForPlugin(pluginId) >= this.getMaxTicketsForPlugin(pluginId) ||
                this.chunkLoadService.getAvailableTickets(player) <= 0) {
            return Optional.empty();
        }
        final int maxChunks = this.getMaxChunksForPluginTicket(pluginId);
        final LanternPlayerLoadingTicket ticket = new LanternPlayerLoadingTicket(
                pluginId, this, player, maxChunks);
        this.tickets.add(ticket);
        return Optional.of(ticket);
    }

    /**
     * Attempts to create a new player loading ticket for the specified plugin.
     * 
     * @param plugin the plugin
     * @param player the unique id of the player
     * @return the loading ticket if available
     */
    public Optional<PlayerEntityLoadingTicket> createPlayerEntityTicket(Object plugin, UUID player) {
        checkNotNull(player, "player");
        final String pluginId = checkPlugin(plugin, "plugin").getId();
        if (this.getTicketsForPlugin(pluginId) >= this.getMaxTicketsForPlugin(pluginId) ||
                this.chunkLoadService.getAvailableTickets(player) <= 0) {
            return Optional.empty();
        }
        final int maxChunks = this.getMaxChunksForPluginTicket(pluginId);
        final LanternPlayerEntityLoadingTicket ticket = new LanternPlayerEntityLoadingTicket(
                pluginId, this, player, maxChunks);
        this.tickets.add(ticket);
        return Optional.of(ticket);
    }

    /**
     * Gets a immutable set with all the loaded chunks.
     * 
     * @return the loaded chunks
     */
    public ImmutableSet<Chunk> getLoadedChunks() {
        return ImmutableSet.copyOf(this.loadedChunks.values());
    }

    /**
     * Gets a chunk for the coordinates,
     * may not be loaded yet.
     * 
     * @param coords the coordinates
     * @return the chunk if loaded, otherwise null
     */
    @Nullable
    public LanternChunk getChunk(Vector2i coords) {
        return this.loadedChunks.get(checkNotNull(coords, "coords"));
    }

    /**
     * Gets a chunk for the coordinates, may not be loaded yet.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the chunk if loaded, otherwise null
     */
    @Nullable
    public LanternChunk getChunk(int x, int z) {
        return this.getChunk(new Vector2i(x, z));
    }

    /**
     * Gets a chunk safely (new one will be created) for the coordinates, may
     * not be loaded yet.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the chunk
     */
    public LanternChunk getOrLoadChunk(int x, int z) {
        return this.getOrCreateChunk(x, z, false);
    }

    /**
     * Gets a chunk safely (new one will be created) for the coordinates, may
     * not be loaded yet.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @param generate whether the new chunk should be generated
     *                  if not done before
     * @return the chunk
     */
    public LanternChunk getOrCreateChunk(int x, int z, boolean generate) {
        return this.getOrCreateChunk(x, z, Cause.of(), generate);
    }

    /**
     * Gets a chunk safely (new one will be created) for the coordinates, may
     * not be loaded yet.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the chunk
     */
    public LanternChunk getOrCreateChunk(int x, int z, Cause cause, boolean generate) {
        return this.getOrCreateChunk(new Vector2i(x, z), cause, generate);
    }

    /**
     * Gets a chunk safely (new one will be created) for the coordinates, may
     * not be loaded yet.
     * 
     * @param coords the coordinates
     * @param cause the cause
     * @param generate whether the chunk should be generated if missing
     * @return the chunk
     */
    public LanternChunk getOrCreateChunk(Vector2i coords, Cause cause, boolean generate) {
        checkNotNull(cause, "cause");
        LanternChunk chunk = this.loadedChunks.get(checkNotNull(coords, "coords"));
        // Chunk is already loaded
        if (chunk != null) {
            return chunk;
        }
        // Lets try to visit the graveyard
        chunk = this.reusableChunks.get(coords);
        if (chunk != null) {
            this.loadedChunks.put(coords, chunk);
            return chunk;
        }
        // Finally, create a new chunk if needed
        chunk = new LanternChunk(this.world, coords.getX(), coords.getY());
        final LanternChunk chunk0 = this.loadedChunks.putIfAbsent(coords, chunk);
        if (chunk0 != null) {
            return chunk0;
        }
        this.load(chunk, cause, generate);
        return chunk;
    }

    /**
     * Loads the specified chunk and attempts to generate it if not done before
     * if {@code generate} is set to {@code true}.
     * 
     * @param chunk the chunk to load
     * @param cause the cause
     * @param generate whether the chunk should be generated if missing
     * @return true if it was successful
     */
    public boolean load(LanternChunk chunk, Cause cause, boolean generate) {
        checkNotNull(chunk, "chunk");
        checkNotNull(cause, "cause");
        try {
            // Try to load the chunk
            if (this.chunkIOService.read(chunk)) {
                this.game.getEventManager().post(SpongeEventFactory.createLoadChunkEvent(
                        this.game, cause, chunk));
                return true;
            }
        } catch (Exception e) {
            LanternGame.log().error("Error while loading chunk ({};{})",
                    chunk.getX(), chunk.getZ(), e);
            // An error in chunk reading may have left the chunk in an invalid state
            // (i.e. double initialization errors), so it's forcibly unloaded here
            chunk.unloadChunk();
        }
        // Stop here if we can't generate
        if (!generate) {
            chunk.initializeEmpty();
            return false;
        }
        // Generate chunk
        try {
            this.generate(chunk);
        } catch (Throwable e) {
            LanternGame.log().error("Error while generating chunk ({};{})",
                    chunk.getX(), chunk.getZ(), e);
            return false;
        }
        this.game.getEventManager().post(SpongeEventFactory.createLoadChunkEvent(
                this.game, cause, chunk));
        return true;
    }

    /**
     * Attempts to generate the chunk.
     * 
     * @param chunk the chunk
     */
    void generate(LanternChunk chunk) {
        final ChunkBiomeBuffer biomeBuffer = this.biomeBuffer.get();
        biomeBuffer.reuse(new Vector2i(chunk.getX() << 4, chunk.getZ() << 4));

        // Generate the biomes
        final BiomeGenerator biomeGenerator = this.worldGenerator.getBiomeGenerator();
        biomeGenerator.generateBiomes(biomeBuffer);

        // Initialize the biomes into the chunk
        final ImmutableBiomeArea immutableBiomeArea = biomeBuffer.getImmutableBiomeCopy();
        chunk.initializeBiomes(biomeBuffer.detach());

        final ChunkBlockBuffer blockBuffer = this.blockBuffer.get();
        blockBuffer.reuse(new Vector3i(chunk.getX() << 4, 0, chunk.getZ() << 4));

        // Apply the main world generator
        final GenerationPopulator baseGenerator = this.worldGenerator.getBaseGenerationPopulator();
        baseGenerator.populate(this.world, blockBuffer, immutableBiomeArea);

        // Get all the used biome types
        final Set<BiomeType> biomeTypes = ImmutableSet.copyOf(biomeBuffer.biomeTypes);
        for (BiomeType biomeType : biomeTypes) {
            final BiomeGenerationSettings settings = this.worldGenerator.getBiomeSettings(biomeType);
            for (GenerationPopulator generator : settings.getGenerationPopulators()) {
                generator.populate(this.world, blockBuffer, immutableBiomeArea);
            }
        }

        // Apply the generator populators to complete the block buffer
        for (GenerationPopulator generator : this.worldGenerator.getGenerationPopulators()) {
            generator.populate(this.world, blockBuffer, immutableBiomeArea);
        }

        // Create the chunk sections
        final ChunkSection[] sections = new ChunkSection[CHUNK_SECTIONS];
        for (int sy = 0; sy < CHUNK_SECTIONS; sy++) {
            if (blockBuffer.nonAirCount[sy] > 0) {
                sections[sy] = new ChunkSection(blockBuffer.types[sy], blockBuffer.nonAirCount[sy]);
            }
        }

        // Initialize the chunk
        chunk.initializeSections(sections);
        chunk.automaticHeightMap();
    }

    private static final Vector3i CHUNK_SIZE = new Vector3i(
            CHUNK_SECTION_SIZE, CHUNK_HEIGHT, CHUNK_SECTION_SIZE);

    /**
     * A biome buffer that also holds a backing array with all the biome
     * type objects to allow faster access to all the used biome types.
     */
    private final class ChunkBiomeBuffer extends ShortArrayMutableBiomeBuffer {

        private final BiomeType[] biomeTypes;

        public ChunkBiomeBuffer(Vector2i start, Vector2i size) {
            super(start, size);
            this.biomeTypes = new BiomeType[size.getX() * size.getY()];
            Arrays.fill(this.biomeTypes, BiomeTypes.OCEAN);
        }

        @Override
        public void setBiome(int x, int z, BiomeType biome) {
            super.setBiome(x, z, biome);
            this.biomeTypes[this.index(x, z)] = biome;
        }

        @Override
        public void reuse(Vector2i start) {
            super.reuse(start);
            Arrays.fill(this.biomeTypes, BiomeTypes.OCEAN);
        }
    }

    /**
     * A custom block buffer that will be used to generate the chunks
     * in the world generation to increase the performance.
     */
    private final class ChunkBlockBuffer extends AbstractMutableBlockBuffer {

        private final short[][] types = new short[CHUNK_SECTIONS][CHUNK_SECTION_VOLUME];
        private final int[] nonAirCount = new int[CHUNK_SECTIONS];

        protected ChunkBlockBuffer() {
            super(new Vector3i(0 , 0, 0), CHUNK_SIZE);
        }

        public void reuse(Vector3i start) {
            this.start = checkNotNull(start, "start");
            this.end = this.start.add(this.size).sub(Vector3i.ONE);
            for (int i = 0; i < CHUNK_SECTIONS; i++) {
                Arrays.fill(this.types[i], (short) 0);
            }
            Arrays.fill(this.nonAirCount, 0);
        }

        @Override
        public void setBlock(int x, int y, int z, BlockState block) {
            checkNotNull(block, "blockState");
            this.checkRange(x, y, z);
            final int sy = y >> 4;
            final int index = ((y & 0xf) << 8) | ((z & 0xf) << 4) | x & 0xf;
            final short[] types = this.types[sy];
            final short type = game.getRegistry().getBlockRegistry()
                    .getInternalStateId(block);
            if (type == 0 && types[index] != 0) {
                this.nonAirCount[sy]--;
            } else if (type != 0 && types[index] == 0) {
                this.nonAirCount[sy]++;
            }
            types[index] = type;
        }

        @Override
        public BlockState getBlock(int x, int y, int z) {
            this.checkRange(x, y, z);
            return game.getRegistry().getBlockRegistry().getStateByInternalId(
                    this.types[y >> 4][((y & 0xf) << 8) | ((z & 0xf) << 4) | x & 0xf]);
        }

        @Override
        public MutableBlockVolume getBlockCopy(StorageType type) {
            checkNotNull(type, "storageType");
            switch (type) {
                case STANDARD:
                    return new ShortArrayMutableBlockBuffer(ExtentBufferHelper.copyToArray(
                            this, this.start, this.end, this.size), this.start, this.size);
                case THREAD_SAFE:
                    return new AtomicShortArrayMutableBlockBuffer(ExtentBufferHelper.copyToArray(
                            this, this.start, this.end, this.size), this.start, this.size);
                default:
                    throw new UnsupportedOperationException(type.name());
            }
        }

        @Override
        public ImmutableBlockVolume getImmutableBlockCopy() {
            return ShortArrayImmutableBlockBuffer.newWithoutArrayClone(ExtentBufferHelper.copyToArray(
                    this, this.start, this.end, this.size), this.start, this.size);
        }
    }

    /**
     * Attempts to save the specified chunk.
     * 
     * @param chunk the chunk
     * @return true if it was successful
     */
    public boolean save(LanternChunk chunk) {
        checkNotNull(chunk, "chunk");
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

    /**
     * Attempts to unload the chunk at the specified coordinates.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @param cause the cause
     * @return true if it was successful
     */
    public boolean unload(int x, int z, Cause cause) {
        return this.unload(new Vector2i(x, z), cause);
    }

    /**
     * Attempts to unload the chunk at the specified coordinates.
     * 
     * @param coords the coordinates
     * @param cause the cause
     * @return true if it was successful
     */
    public boolean unload(Vector2i coords, Cause cause) {
        checkNotNull(cause, "cause");
        final LanternChunk chunk = this.getChunk(checkNotNull(coords, "coords"));
        if (chunk != null) {
            return this.unload(chunk, cause);
        }
        return true;
    }

    /**
     * Attempts to unload the specified chunk.
     * 
     * @param chunk the chunk to unload
     * @param cause the cause
     * @return true if it was successful
     */
    public boolean unload(LanternChunk chunk, Cause cause) {
        checkNotNull(chunk, "chunk");
        checkNotNull(cause, "cause");
        final Vector2i coords = new Vector2i(chunk.getX(), chunk.getZ());
        // Forced chunks cannot be unloaded
        if (this.ticketsByPos.containsKey(coords)) {
            return false;
        }
        // Post the chunk unload event
        this.game.getEventManager().post(SpongeEventFactory.createUnloadChunkEvent(
                this.game, cause, chunk));
        // Remove from the loaded chunks
        this.loadedChunks.remove(coords);
        // Move the chunk to the graveyard
        this.reusableChunks.put(coords, chunk);
        this.save(chunk);
        return true;
    }

    /**
     * Forces the specified chunk coordinates for a specific ticket.
     * 
     * @param ticket the ticket
     * @param coords the coordinates
     */
    void force(LanternLoadingTicket ticket, Vector2i coords) {
        this.force(ticket, coords, true);
    }

    /**
     * Forces the specified chunk coordinates for a specific ticket.
     * 
     * @param ticket the ticket
     * @param coords the coordinates
     * @param callEvents whether the force chunk events should be called
     */
    void force(LanternLoadingTicket ticket, Vector2i coords, boolean callEvents) {
        final Set<LanternLoadingTicket> set = this.ticketsByPos.computeIfAbsent(
                coords, coords0 -> Sets.newConcurrentHashSet());
        final LanternChunk chunk = this.getChunk(coords);
        if (set.isEmpty() && chunk == null && !this.forcedChunkLoadingQueue.contains(coords)) {
            // Queue the chunk for loading
            this.forcedChunkLoadingQueue.add(coords);
        }
        set.add(ticket);
        if (chunk != null && callEvents) {
            final Vector3i coords0 = new Vector3i(coords.getX(), 0, coords.getY());
            final ForcedChunkEvent event = SpongeEventFactory.createForcedChunkEvent(
                    this.game, coords0, chunk, ticket);
            this.game.getEventManager().post(event);
        }
    }

    /**
     * Unforces the specified chunk coordinates for a specific ticket.
     * 
     * @param ticket the ticket
     * @param coords the coordinates
     */
    void unforce(LanternLoadingTicket ticket, Vector2i coords) {
        final Set<LanternLoadingTicket> set = this.ticketsByPos.get(ticket);
        if (set != null) {
            set.remove(ticket);
            // Remove the chunk from the cache (can unload) if it's doesn't
            // have any attached tickets
            if (set.isEmpty()) {
                this.ticketsByPos.remove(coords);
                this.forcedChunkLoadingQueue.remove(coords);
            }
        }
    }

    /**
     * Releases the ticket.
     * 
     * @param ticket the ticket
     */
    void release(LanternLoadingTicket ticket) {
        this.tickets.remove(ticket);
    }

    /**
     * Shuts the chunk manager down, all the chunks will
     * be saved in the process.
     */
    public void shutdown() {
        for (Entry<Vector2i, LanternChunk> entry : this.loadedChunks.entrySet()) {
            final LanternChunk chunk = entry.getValue();
            // Post the chunk unload event
            this.game.getEventManager().post(SpongeEventFactory.createUnloadChunkEvent(
                    this.game, Cause.of(), chunk));
            // Save the chunk
            this.save(chunk);
        }
        // Cleanup
        this.loadedChunks.clear();
        this.reusableChunks.clear();
    }

    /**
     * Pulses the chunk manager.
     */
    public void pulse() {
        final PluginContainer minecraft = this.game.getPlugin();
        final Cause unloadCause = Cause.of(minecraft);
        for (Entry<Vector2i, LanternChunk> en : this.loadedChunks.entrySet()) {
            // The chunk is no longer locked, try to unload
            if (!this.ticketsByPos.containsKey(en.getKey())) {
                this.unload(en.getValue(), unloadCause);
            }
        }
        // TODO: Async chunk loading/unloading?
        int counter = 0;
        Vector2i coords;
        while (++counter < CHUNK_LOADING_LIMIT && (coords = this.forcedChunkLoadingQueue.poll()) != null) {
            final Set<LanternLoadingTicket> set = this.ticketsByPos.get(coords);
            Cause cause = Cause.of(minecraft);
            if (set != null) {
                for (LanternLoadingTicket ticket : set) {
                    cause = cause.with(ticket);
                }
            }
            final LanternChunk chunk = this.getOrCreateChunk(coords, cause, true);
            if (set != null && !set.isEmpty()) {
                final Vector3i coords0 = new Vector3i(coords.getX(), 0, coords.getY());
                for (LanternLoadingTicket ticket : set) {
                    final ForcedChunkEvent event = SpongeEventFactory.createForcedChunkEvent(
                            this.game, coords0, chunk, ticket);
                    this.game.getEventManager().post(event);
                }
            }
        }
    }

    /*
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
    }*/
}
