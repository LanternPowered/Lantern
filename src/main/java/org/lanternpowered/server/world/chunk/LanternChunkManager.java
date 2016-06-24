/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkPlugin;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_AREA;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_HEIGHT;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTIONS;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTION_SIZE;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTION_VOLUME;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_AREA_SIZE;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.lanternpowered.server.config.world.WorldConfig;
import org.lanternpowered.server.data.io.ChunkIOService;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.util.FastSoftThreadLocal;
import org.lanternpowered.server.util.SoftThreadLocal;
import org.lanternpowered.server.util.ThreadHelper;
import org.lanternpowered.server.util.gen.biome.ShortArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.block.AbstractMutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.AtomicShortArrayMutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.ShortArrayImmutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.ShortArrayMutableBlockBuffer;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.chunk.LanternChunk.ChunkSection;
import org.lanternpowered.server.world.extent.ExtentBufferHelper;
import org.lanternpowered.server.world.extent.SoftBufferExtentViewDownsize;
import org.lanternpowered.server.world.extent.worker.LanternMutableBiomeAreaWorker;
import org.lanternpowered.server.world.extent.worker.LanternMutableBlockVolumeWorker;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.ChunkTicketManager;
import org.spongepowered.api.world.ChunkTicketManager.EntityLoadingTicket;
import org.spongepowered.api.world.ChunkTicketManager.LoadingTicket;
import org.spongepowered.api.world.ChunkTicketManager.PlayerEntityLoadingTicket;
import org.spongepowered.api.world.ChunkTicketManager.PlayerLoadingTicket;
import org.spongepowered.api.world.biome.BiomeGenerationSettings;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.worker.MutableBiomeAreaWorker;
import org.spongepowered.api.world.extent.worker.MutableBlockVolumeWorker;
import org.spongepowered.api.world.gen.BiomeGenerator;
import org.spongepowered.api.world.gen.GenerationPopulator;
import org.spongepowered.api.world.gen.Populator;
import org.spongepowered.api.world.gen.WorldGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public final class LanternChunkManager {

    // The maximum amount of threads that can load chunks asynchronously
    private static final int CHUNK_LOADING_MAX_POOL_SIZE = 10;

    // The core amount of threads that can load chunks asynchronously
    private static final int CHUNK_LOADING_CORE_POOL_SIZE = 4;

    // The delay to unload chunks that are not forced,
    // loaded through loadChunk methods
    private static final long UNLOAD_DELAY = TimeUnit.SECONDS.toMillis(1);

    // All the attached tickets mapped by the forced chunk coordinates
    private final Map<Vector2i, Set<ChunkLoadingTicket>> ticketsByPos = Maps.newConcurrentMap();

    // All the loading tickets that are still usable
    private final Set<LanternLoadingTicket> tickets = Sets.newConcurrentHashSet();

    // All the chunks that are loaded into the server
    private final Map<Vector2i, LanternChunk> loadedChunks = Maps.newConcurrentMap();

    // A cache that can be used to get chunks that weren't unloaded
    // so much after all, because of active references to the chunk
    private final Map<Vector2i, LanternChunk> reusableChunks = new MapMaker().weakValues().makeMap();

    // A set which contains chunks that are pending for removal,
    // chunks loaded by loadChunk may not have been locked in the process,
    // and using a queue for removal should prevent the chunks from unloading too early
    private final Queue<UnloadingChunkEntry> pendingForUnload = new ConcurrentLinkedQueue<>();

    private class UnloadingChunkEntry {

        final Vector2i coords;
        final long time;

        private UnloadingChunkEntry(Vector2i coords) {
            this.time = System.currentTimeMillis();
            this.coords = coords;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof UnloadingChunkEntry && ((UnloadingChunkEntry) obj).coords.equals(this.coords);
        }

        @Override
        public int hashCode() {
            return this.coords.hashCode();
        }
    }

    // All the futures that will cause chunk loading/unloading, they are stored
    // here to allow them to be cancelled
    private final Map<Vector2i, LanternChunkQueueTask> chunkQueueTasks = Maps.newConcurrentMap();

    // The chunk load executor
    private final ThreadPoolExecutor chunkTaskExecutor = new ThreadPoolExecutor(
            CHUNK_LOADING_CORE_POOL_SIZE, CHUNK_LOADING_MAX_POOL_SIZE, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
            ThreadHelper.newFastThreadLocalThreadFactory());

    // Some objects that can be used in {@link Chunk} population.
    private class PopulationData {

        private final Random random = new Random();
        private final ChunkLoadingTicket lockTicket = new InternalLoadingTicket();
    }

    private LanternChunkQueueTask queueTask(Vector2i coords, Runnable runnable) {
        final LanternChunkQueueTask task = new LanternChunkQueueTask(coords, runnable);
        task.setFuture(this.chunkTaskExecutor.submit(task));
        return task;
    }

    private class LanternChunkQueueTask implements Callable<Void> {

        private final Vector2i coords;
        // The runnable that should be executed
        private final Runnable runnable;
        // The future attached to this callable
        @Nullable private Future<Void> future;

        public LanternChunkQueueTask(Vector2i coords, Runnable runnable) {
            this.runnable = runnable;
            this.coords = coords;
        }

        public void setFuture(Future<Void> future) {
            this.future = future;
            synchronized (this) {
                this.notifyAll();
            }
        }

        @Override
        public Void call() throws Exception {
            // Wait for the future to be set, in case it's getting directly executed
            synchronized (this) {
                while (this.future == null) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            this.runnable.run();
            return null;
        }

        public boolean cancel() {
            // We have to wait for the future to be set before we
            // can cancel it, shouldn't be long
            synchronized (this) {
                while (this.future == null) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            return this.future.cancel(false);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof LanternChunkQueueTask && ((LanternChunkQueueTask) other).coords.equals(this.coords);
        }

        @Override
        public int hashCode() {
            return this.coords.hashCode();
        }
    }

    private class LanternChunkUnloadTask implements Runnable {

        // The callable that is bound to the unloading task
        private final LanternChunkQueueTask callable;

        private LanternChunkUnloadTask(LanternChunkQueueTask callable) {
            this.callable = callable;
        }

        @Override
        public void run() {
            unload0(this.callable.coords, () -> Cause.source(world).build(), false);
        }
    }

    private class LanternChunkLoadTask implements Runnable {

        // The coordinates of the chunk
        private final Vector2i coords;

        private LanternChunkLoadTask(Vector2i coords) {
            this.coords = coords;
        }

        @Override
        public void run() {
            doChunkLoad(this.coords);
        }
    }

    private void doChunkLoad(Vector2i coords) {
        final Set<ChunkLoadingTicket> tickets = ticketsByPos.get(coords);
        if (tickets == null) {
            return;
        }
        // Chunk may be null if's already being loaded by a different thread.
        final LanternChunk chunk = getOrCreateChunk(coords, () -> {
            // Build the cause only if the chunk isn't already loaded
            return Cause.source(world).named("tickets", tickets.toArray(new Object[tickets.size()])).build();
        }, true, false);
    }

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

    private class GenerationBuffers {

        final ChunkBiomeBuffer chunkBiomeBuffer = new ChunkBiomeBuffer();
        final ChunkBlockBuffer chunkBlockBuffer = new ChunkBlockBuffer();
    }

    // The world generation buffers that will be reused
    private final FastSoftThreadLocal<GenerationBuffers> genBuffers = FastSoftThreadLocal.withInitial(GenerationBuffers::new);

    // The randoms that will be shared for population
    private final FastSoftThreadLocal<PopulationData> populationData = FastSoftThreadLocal.withInitial(PopulationData::new);

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
     * @param worldFolder the world data folder
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

    public ChunkIOService getChunkIOService() {
        return this.chunkIOService;
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
        for (Entry<Vector2i, Set<ChunkLoadingTicket>> en : this.ticketsByPos.entrySet()) {
            final Vector2i pos0 = en.getKey();
            final Vector3i pos = new Vector3i(pos0.getX(), 0, pos0.getY());
            for (ChunkLoadingTicket ticket : en.getValue()) {
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
        return this.getChunk(coords, true);
    }

    @Nullable
    public LanternChunk getChunkIfLoaded(Vector2i coords) {
        final LanternChunk chunk = this.loadedChunks.get(checkNotNull(coords, "coords"));
        if (chunk != null && !chunk.loaded) {
            return null;
        }
        return chunk;
    }

    @Nullable
    private LanternChunk getChunk(Vector2i coords, boolean wait) {
        final LanternChunk chunk = this.loadedChunks.get(checkNotNull(coords, "coords"));
        if (wait && chunk != null && !chunk.loaded &&
                chunk.lockState == LanternChunk.LockState.LOADING) {
            // Wait for the chunk to finish loading
            chunk.lockCondition.awaitUninterruptibly();
        }
        return chunk;
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
        return this.getOrCreateChunk(x, z, () -> Cause.source(this.world).build(), generate);
    }

    /**
     * Gets a chunk safely (new one will be created) for the coordinates, may
     * not be loaded yet.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the chunk
     */
    public LanternChunk getOrCreateChunk(int x, int z, Supplier<Cause> cause, boolean generate) {
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
    public LanternChunk getOrCreateChunk(Vector2i coords, Supplier<Cause> cause, boolean generate) {
        return this.getOrCreateChunk(coords, cause, generate, true);
    }

    /**
     *
     * @param coords the coordinates of the chunk to load
     * @param cause a supplier of the cause that triggered the chunk load
     * @param generate whether the chunk should be generated if not found
     * @param wait whether the current thread should wait for the loading to finish, this should only
     *             be internally used inside the chunk manager
     * @return the chunk
     */
    private LanternChunk getOrCreateChunk(Vector2i coords, Supplier<Cause> cause, boolean generate, boolean wait) {
        checkNotNull(cause, "cause");
        LanternChunk chunk = this.loadedChunks.get(checkNotNull(coords, "coords"));
        // Chunk is already loaded
        if (chunk != null) {
            if (!this.ticketsByPos.containsKey(coords)) {
                this.pendingForUnload.add(new UnloadingChunkEntry(coords));
            }
            return chunk;
        }
        // Lets try to visit the graveyard, try to retrieve chunks that where
        // not gc yet, allowing us to reuse them to avoid loading a new chunk
        chunk = this.reusableChunks.get(coords);
        if (chunk != null) {
            this.loadedChunks.put(coords, chunk);
            this.reusableChunks.remove(coords);
            if (!this.ticketsByPos.containsKey(coords)) {
                this.pendingForUnload.add(new UnloadingChunkEntry(coords));
            }
            this.game.getEventManager().post(SpongeEventFactory.createLoadChunkEvent(cause.get(), chunk));
            return chunk;
        }
        boolean[] newChunk = new boolean[1];
        // Finally, create a new chunk if needed
        chunk = this.loadedChunks.computeIfAbsent(coords, coords0 -> {
            newChunk[0] = true;
            return new LanternChunk(this.world, coords.getX(), coords.getY());
        });
        // This method call was too late
        if (!newChunk[0]) {
            // If the chunk is already loaded, just return it
            if (chunk.loaded) {
                return chunk;
            }
            if (chunk.lockState == LanternChunk.LockState.LOADING) {
                // Don't bother waiting for the chunk to finish
                if (!wait) {
                    return chunk;
                }
                // Wait for the chunk to finish loading
                chunk.lockCondition.awaitUninterruptibly();
            }
            // Loading is not triggered?
            return chunk;
        }
        // Try to load the chunk
        this.load(chunk, cause, generate);
        if (!this.ticketsByPos.containsKey(coords)) {
            this.pendingForUnload.add(new UnloadingChunkEntry(coords));
        }
        return chunk;
    }

    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int RIGHT = 2;
    private static final int RIGHT_UP = 3;
    private static final int RIGHT_DOWN = 4;
    private static final int LEFT = 5;
    private static final int LEFT_UP = 6;
    private static final int LEFT_DOWN = 7;

    private static Vector2i[] getSides(Vector2i center) {
        final Vector2i[] sides = new Vector2i[8];
        sides[UP] = center.add(0, 1);
        sides[DOWN] = center.add(0, -1);
        sides[RIGHT] = center.add(1, 0);
        sides[RIGHT_UP] = center.add(1, 1);
        sides[RIGHT_DOWN] = center.add(1, -1);
        sides[LEFT] = center.add(-1, 0);
        sides[LEFT_UP] = center.add(-1, 1);
        sides[LEFT_DOWN] = center.add(-1, -1);
        return sides;
    }

    /**
     * This is taken from the {@link Populator} class to give a bit more info
     * about what we are trying here to achieve.
     *
     * +----------+----------+ . . The chunk provided as a parameter
     * |          |          | . . to this method.
     * |          |          |
     * |     #####|#####     | ### The area you (the populator) should populate.
     * |     #####|#####     | ###
     * +----------+----------+
     * | . . #####|#####     |
     * | . . #####|#####     |
     * | . . . . .|          |
     * | . . . . .|          |
     * +----------+----------+
     *
     * @param chunk the chunk
     */
    private void tryPopulateSurroundingChunks(LanternChunk chunk, Cause cause) {
        final Vector2i pos = chunk.chunkPos;
        final Vector2i[] sides = getSides(pos);
        final PopulationData populationData = this.populationData.get();
        final Random random = populationData.random;
        // TODO: Populating must be done in the sync thread???
        for (Vector2i side : sides) {
            this.lockInternally(side, populationData.lockTicket);
        }
        LanternChunk up = this.isChunkLoaded(sides[UP]);
        LanternChunk right = this.isChunkLoaded(sides[RIGHT]);
        LanternChunk rightUp = this.isChunkLoaded(sides[RIGHT_UP]);
        if (up != null && right != null && rightUp != null) {
            if (!chunk.populating && !chunk.populated) {
                this.populateChunk(chunk, cause, random);
            }
        }
        LanternChunk left = this.isChunkLoaded(sides[LEFT]);
        LanternChunk leftDown = this.isChunkLoaded(sides[LEFT_DOWN]);
        LanternChunk down = this.isChunkLoaded(sides[DOWN]);
        if (leftDown != null && left != null && down != null) {
            if (!leftDown.populating && !leftDown.populated) {
                this.populateChunk(leftDown, cause, random);
            }
        }
        if (up == null) { // Maybe it is loaded by now?
            up = this.isChunkLoaded(sides[UP]);
        }
        if (left == null) { // Maybe it is loaded by now?
            left = this.isChunkLoaded(sides[LEFT]);
        }
        LanternChunk leftUp = this.isChunkLoaded(sides[LEFT_UP]);
        if (left != null && leftUp != null && up != null) {
            if (!left.populating && !left.populated) {
                this.populateChunk(left, cause, random);
            }
        }
        if (right == null) { // Maybe it is loaded by now?
            right = this.isChunkLoaded(sides[RIGHT]);
        }
        if (down == null) { // Maybe it is loaded by now?
            down = this.isChunkLoaded(sides[DOWN]);
        }
        LanternChunk rightDown = this.isChunkLoaded(sides[RIGHT_DOWN]);
        if (down != null && rightDown != null && right != null) {
            if (!down.populating && !down.populated) {
                this.populateChunk(down, cause, random);
            }
        }
        for (Vector2i side : sides) {
            this.unlockInternally(side, populationData.lockTicket);
        }
    }

    private void populateChunk(LanternChunk chunk, Cause cause, Random random) {
        chunk.populating = true;

        // Populate
        int chunkX = chunk.getX() * 16;
        int chunkZ = chunk.getZ() * 16;
        long worldSeed = this.world.getProperties().getSeed();
        random.setSeed(worldSeed);
        long xSeed = random.nextLong() / 2 * 2 + 1;
        long zSeed = random.nextLong() / 2 * 2 + 1;
        long chunkSeed = xSeed * chunkX + zSeed * chunkZ ^ worldSeed;
        random.setSeed(chunkSeed);

        // Using the biome at an arbitrary point within the chunk
        // ({16, 0, 16} in the vanilla game)
        final BiomeType biomeType = chunk.getWorld().getBiome(chunkX + 16, chunkZ + 16);

        // Get the generation settings
        final BiomeGenerationSettings biomeGenSettings = this.worldGenerator.getBiomeSettings(biomeType);

        final List<Populator> populators = new LinkedList<>(biomeGenSettings.getPopulators());
        populators.addAll(this.worldGenerator.getPopulators());

        final EventManager eventManager = Sponge.getEventManager();

        Vector3i min = new Vector3i(chunkX + 8, 0, chunkZ + 8);
        Extent volume = new SoftBufferExtentViewDownsize(chunk.getWorld(), min, min.add(15, 0, 15), min.sub(8, 0, 8), min.add(23, 0, 23));

        // Call the pre populate event, this allows
        // modifications to the populators list
        // Called before a chunk begins populating. (javadoc)
        eventManager.post(SpongeEventFactory.createPopulateChunkEventPre(cause, populators, chunk));

        // First populate the chunk with the biome populators
        for (Populator populator : populators) {
            // Called when a populator is about to run against a chunk. (javadoc)
            eventManager.post(SpongeEventFactory.createPopulateChunkEventPopulate(cause, populator, chunk));
            populator.populate(this.world, volume, random);
        }

        // Called when a chunk finishes populating. (javadoc)
        eventManager.post(SpongeEventFactory.createPopulateChunkEventPost(cause, ImmutableList.copyOf(populators), chunk));

        // We are done
        chunk.populated = true;
        chunk.populating = false;
    }

    @Nullable
    private LanternChunk isChunkLoaded(Vector2i pos) {
        final LanternChunk chunk = this.getChunk(pos, false);
        return chunk != null && chunk.loaded ? chunk : null;
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
    public boolean load(LanternChunk chunk, Supplier<Cause> cause, boolean generate) {
        return this.load0(chunk, cause, generate, true);
    }

    private boolean load0(LanternChunk chunk, Supplier<Cause> cause, boolean generate, boolean wait) {
        checkNotNull(chunk, "chunk");
        checkNotNull(cause, "cause");
        if (chunk.loaded) {
            return chunk.loadingSuccess;
        }
        // The chunk is already getting lock by a different thread,
        // wait for the task to finish
        if (!chunk.lock.tryLock()) {
            if (chunk.lockState == LanternChunk.LockState.LOADING) {
                if (wait) {
                    // Wait for the chunk to finish loading
                    chunk.lockCondition.awaitUninterruptibly();
                    // Consider it was a success?
                    return chunk.loadingSuccess;
                } else {
                    return false;
                }
            } else if (chunk.lockState == LanternChunk.LockState.UNLOADING) {
                // Acquire the lock and wait for the chunk to get unloaded
                chunk.lock.lock();
            // The chunk can only be saved if it was once loaded, so return true
            } else if (chunk.lockState == LanternChunk.LockState.SAVING) {
                return true;
            }
        }
        boolean success = true;
        try {
            chunk.lockState = LanternChunk.LockState.LOADING;
            final LanternChunkQueueTask task = this.chunkQueueTasks.remove(chunk.getCoords());
            // Try to cancel the task, the task will probably be ignored
            // because we are already locked
            if (task != null) {
                task.cancel();
            }
            try {
                // Try to load the chunk
                if (this.chunkIOService.read(chunk)) {
                    this.game.getEventManager().post(SpongeEventFactory.createLoadChunkEvent(cause.get(), chunk));
                    return true;
                }
            } catch (Exception e) {
                this.game.getLogger().error("Error while loading chunk ({};{})",
                        chunk.getX(), chunk.getZ(), e);
                // An error in chunk reading may have left the chunk in an invalid state
                // (i.e. double initialization errors), so it's forcibly unloaded here
                // chunk.unloadChunk(); TODO
            }
            // Stop here if we can't generate
            if (!generate) {
                chunk.initializeEmpty();
                return success = false;
            }
            Cause cause0 = cause.get();
            // Generate chunk
            try {
                this.generate(chunk, cause0);
            } catch (Throwable e) {
                this.game.getLogger().error("Error while generating chunk ({};{})", chunk.getX(), chunk.getZ(), e);
                return success = false;
            }
            // Try to populate the chunk
            this.tryPopulateSurroundingChunks(chunk, cause0);
            this.game.getEventManager().post(SpongeEventFactory.createLoadChunkEvent(cause0, chunk));
            return true;
        } finally {
            chunk.lockState = LanternChunk.LockState.NONE;
            chunk.loaded = true;
            chunk.loadingSuccess = success;
            chunk.lockCondition.signalAll();
            chunk.lock.unlock();
        }
    }

    /**
     * Attempts to generate the chunk.
     * 
     * @param chunk the chunk
     */
    void generate(LanternChunk chunk, Cause cause) {
        final EventManager eventManager = Sponge.getEventManager();
        eventManager.post(SpongeEventFactory.createGenerateChunkEventPre(cause, chunk));

        final GenerationBuffers buffers = this.genBuffers.get();
        final ChunkBiomeBuffer biomeBuffer = buffers.chunkBiomeBuffer;
        biomeBuffer.reuse(new Vector2i(chunk.getX() << 4, chunk.getZ() << 4));

        // Generate the biomes
        final BiomeGenerator biomeGenerator = this.worldGenerator.getBiomeGenerator();
        biomeGenerator.generateBiomes(biomeBuffer);

        // Initialize the biomes into the chunk
        final ImmutableBiomeArea immutableBiomeArea = biomeBuffer.getImmutableBiomeCopy();
        chunk.initializeBiomes(biomeBuffer.detach());

        final ChunkBlockBuffer blockBuffer = buffers.chunkBlockBuffer;
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
                sections[sy] = new ChunkSection(blockBuffer.types[sy]);
            }
        }

        // Initialize the chunk
        chunk.initializeSections(sections);
        chunk.initializeHeightMap(null);
        chunk.initializeLight();

        eventManager.post(SpongeEventFactory.createGenerateChunkEventPost(cause, chunk));
    }

    private static final Vector3i CHUNK_SIZE = new Vector3i(
            CHUNK_SECTION_SIZE, CHUNK_HEIGHT, CHUNK_SECTION_SIZE);

    /**
     * A biome buffer that also holds a backing array with all the biome
     * type objects to allow faster access to all the used biome types.
     */
    private final class ChunkBiomeBuffer extends ShortArrayMutableBiomeBuffer {

        private final BiomeType[] biomeTypes;

        public ChunkBiomeBuffer() {
            super(Vector2i.ZERO, CHUNK_AREA_SIZE);
            this.biomeTypes = new BiomeType[CHUNK_AREA];
            Arrays.fill(this.biomeTypes, BiomeTypes.OCEAN);
            this.detach();
        }

        @Override
        public void setBiome(int x, int z, BiomeType biome) {
            super.setBiome(x, z, biome);
            this.biomeTypes[this.index(x, z)] = biome;
        }

        @Override
        public MutableBiomeAreaWorker<? extends MutableBiomeArea> getBiomeWorker() {
            return new LanternMutableBiomeAreaWorker<>(this);
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
            super(Vector3i.ZERO, CHUNK_SIZE);
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
            final short type = BlockRegistryModule.get().getStateInternalIdAndData(block);
            if (type == 0 && types[index] != 0) {
                this.nonAirCount[sy]--;
            } else if (type != 0 && types[index] == 0) {
                this.nonAirCount[sy]++;
            }
            types[index] = type;
        }

        @Override
        public MutableBlockVolumeWorker<? extends MutableBlockVolume> getBlockWorker() {
            return new LanternMutableBlockVolumeWorker<>(this);
        }

        @Override
        public BlockState getBlock(int x, int y, int z) {
            this.checkRange(x, y, z);
            return BlockRegistryModule.get().getStateByInternalIdAndData(this.types[y >> 4][((y & 0xf) << 8) | ((z & 0xf) << 4) | x & 0xf])
                    .orElse(BlockTypes.AIR.getDefaultState());
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
        chunk.lock.lock();
        try {
            chunk.lockState = LanternChunk.LockState.SAVING;
            return this.save0(chunk);
        } finally {
            chunk.lockState = LanternChunk.LockState.NONE;
            chunk.lockCondition.signalAll();
            chunk.lock.unlock();
        }
    }

    private boolean save0(LanternChunk chunk) {
        try {
            this.chunkIOService.write(chunk);
            return true;
        } catch (IOException e) {
            this.game.getLogger().error("Error while saving " + chunk, e);
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
    public boolean unload(int x, int z, Supplier<Cause> cause) {
        return this.unload(new Vector2i(x, z), cause);
    }

    /**
     * Attempts to unload the chunk at the specified coordinates.
     * 
     * @param coords the coordinates
     * @param cause the cause
     * @return true if it was successful
     */
    public boolean unload(Vector2i coords, Supplier<Cause> cause) {
        return this.unload0(coords, cause, true);
    }

    private boolean unload0(Vector2i coords, Supplier<Cause> cause, boolean wait) {
        checkNotNull(cause, "cause");
        final LanternChunk chunk = this.getChunk(checkNotNull(coords, "coords"));
        if (chunk != null) {
            return this.unload0(chunk, cause, wait);
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
    public boolean unload(LanternChunk chunk, Supplier<Cause> cause) {
        checkNotNull(chunk, "chunk");
        checkNotNull(cause, "cause");
        return this.unload0(chunk, cause, true);
    }

    private boolean unload0(LanternChunk chunk, Supplier<Cause> cause, boolean wait) {
        final Vector2i coords = chunk.getCoords();
        // Forced chunks cannot be unloaded
        if (this.ticketsByPos.containsKey(coords)) {
            chunk.unloadingSuccess = false;
            return false;
        }
        if (!chunk.lock.tryLock()) {
            // The chunk is already unloading, so wait for it to complete
            if (chunk.lockState == LanternChunk.LockState.UNLOADING) {
                if (wait) {
                    chunk.lockCondition.awaitUninterruptibly();
                    return chunk.unloadingSuccess;
                } else {
                    return false;
                }
            // The chunk is currently loading or saving, wait for it to complete
            // and then unload it again
            } else {
                chunk.lock.lock();
            }
        }
        try {
            chunk.lockState = LanternChunk.LockState.UNLOADING;
            // The chunk isn't loaded yet, fast fail
            if (!chunk.loaded) {
                return true;
            }
            final LanternChunkQueueTask task = this.chunkQueueTasks.remove(coords);
            // Try to cancel all the current tasks
            if (task != null) {
                task.cancel();
            }
            // Post the chunk unload event
            this.game.getEventManager().post(SpongeEventFactory.createUnloadChunkEvent(cause.get(), chunk));
            // Remove from the loaded chunks
            this.loadedChunks.remove(coords);
            // Move the chunk to the graveyard
            this.reusableChunks.put(coords, chunk);
            this.save0(chunk);
            return true;
        } finally {
            chunk.lockState = LanternChunk.LockState.NONE;
            chunk.unloadingSuccess = true;
            chunk.lockCondition.signalAll();
            chunk.lock.unlock();
        }
    }

    /**
     * Locks the chunk of the coordinates with a internal loading ticket. This
     * method does not trigger the loading of a chunk but locks the chunk from
     * unloading.
     *
     * @param coords the coordinates
     * @return whether it was previously empty
     */
    private boolean lockInternally(Vector2i coords, ChunkLoadingTicket ticket) {
        final boolean[] empty = new boolean[1];
        this.ticketsByPos.computeIfAbsent(coords, coords0 -> {
            empty[0] = true;
            return Sets.newConcurrentHashSet();
        }).add(ticket);
        return empty[0];
    }

    private boolean unlockInternally(Vector2i coords, ChunkLoadingTicket ticket) {
        final Set<ChunkLoadingTicket> set = this.ticketsByPos.get(coords);
        if (set != null && set.remove(ticket)) {
            if (set.isEmpty()) {
                this.ticketsByPos.remove(coords);
            }
            return true;
        }
        return false;
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
        final LanternChunk chunk = this.getChunk(coords, false);
        // The chunk at this coords is already loaded,
        // wa can call the event directly
        this.lockInternally(coords, ticket);
        // Remove from unload through loadChunk
        this.pendingForUnload.removeIf(e -> e.coords.equals(coords));
        // Whether the chunk should be queued for loading
        boolean queueLoad = false;
        if (chunk != null) {
            if (chunk.lock.isLocked() && chunk.lockState == LanternChunk.LockState.UNLOADING) {
                queueLoad = true;
            }
        // Queue the chunk to load
        } else {
            queueLoad = true;
        }
        if (queueLoad) {
            LanternChunkQueueTask task = this.chunkQueueTasks.get(coords);
            if (task == null || !(task.runnable instanceof LanternChunkLoadTask)) {
                this.chunkQueueTasks.computeIfAbsent(coords, coords1 ->
                        this.queueTask(coords, new LanternChunkLoadTask(coords)));
            }
        }
        if  (callEvents) {
            final Vector3i coords0 = new Vector3i(coords.getX(), 0, coords.getY());
            this.game.getEventManager().post(SpongeEventFactory.createForcedChunkEvent(
                    Cause.source(ticket).owner(this.world).build(), coords0, ticket));
        }
    }

    /**
     * Unforces the specified chunk coordinates for a specific ticket.
     * 
     * @param ticket the ticket
     * @param coords the coordinates
     */
    void unforce(LanternLoadingTicket ticket, Vector2i coords, boolean callEvents) {
        if (this.unlockInternally(coords, ticket)) {
            final LanternChunk chunk = this.getChunk(coords, false);
            // Try to cancel any queued chunk loadings
            if (chunk != null && chunk.lock.isLocked() && chunk.lockState == LanternChunk.LockState.LOADING) {
                final LanternChunkQueueTask task = this.chunkQueueTasks.get(coords);
                if (task != null && task.runnable instanceof LanternChunkLoadTask) {
                    task.cancel();
                }
            // Queue the chunk for unload, will be some ticks later
            } else {
                final UnloadingChunkEntry entry = new UnloadingChunkEntry(coords);
                if (!this.pendingForUnload.contains(entry)) {
                    this.pendingForUnload.offer(entry);
                }
            }
        }
        if (callEvents) {
            final Vector3i coords0 = new Vector3i(coords.getX(), 0, coords.getY());
            this.game.getEventManager().post(SpongeEventFactory.createUnforcedChunkEvent(
                    Cause.source(ticket).owner(this.world).build(), coords0, ticket));
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

    public void save() {
        try {
            LanternLoadingTicketIO.save(this.worldFolder, this.tickets);
        } catch (IOException e) {
            this.game.getLogger().warn("An error occurred while saving the chunk loading tickets", e);
        }
        for (Entry<Vector2i, LanternChunk> entry : this.loadedChunks.entrySet()) {
            // Save the chunk
            this.save(entry.getValue());
        }
    }

    /**
     * Shuts the chunk manager down, all the chunks will
     * be saved in the process.
     */
    public void shutdown() {
        try {
            LanternLoadingTicketIO.save(this.worldFolder, this.tickets);
        } catch (IOException e) {
            this.game.getLogger().warn("An error occurred while saving the chunk loading tickets", e);
        }
        for (Entry<Vector2i, LanternChunk> entry : this.loadedChunks.entrySet()) {
            final LanternChunk chunk = entry.getValue();
            // Post the chunk unload event
            this.game.getEventManager().post(SpongeEventFactory.createUnloadChunkEvent(
                    Cause.source(this.game.getMinecraftPlugin()).owner(this.world).build(), chunk));
            // Save the chunk
            this.save(chunk);
        }
        // Cleanup
        this.loadedChunks.clear();
        this.reusableChunks.clear();
        this.chunkTaskExecutor.shutdown();
    }

    /**
     * Pulses the chunk manager.
     */
    public void pulse() {
        UnloadingChunkEntry entry;
        while ((entry = this.pendingForUnload.peek()) != null &&
                (System.currentTimeMillis() - entry.time) > UNLOAD_DELAY) {
            this.pendingForUnload.poll();
            if (!this.ticketsByPos.containsKey(entry.coords)) {
                // TODO: Create unload tasks
                this.unload(entry.coords, () -> Cause.source(this.world).build());
            }
        }
    }

    public void loadTickets() throws IOException {
        Multimap<String, LanternLoadingTicket> tickets = LanternLoadingTicketIO.load(this.worldFolder, this, this.chunkLoadService);
        Iterator<Entry<String, LanternLoadingTicket>> it = tickets.entries().iterator();
        while (it.hasNext()) {
            LanternLoadingTicket ticket = (LanternLoadingTicket) it.next();
            if (ticket instanceof LanternEntityLoadingTicket) {
                LanternEntityLoadingTicket ticket0 = (LanternEntityLoadingTicket) ticket;
                EntityReference ref = ticket0.getEntityReference().orElse(null);
                if (ref != null) {
                    LanternChunk chunk = this.getOrCreateChunk(ref.getChunkCoords(),
                            () -> Cause.source(ticket0).owner(this.world).build(), true, true);
                    Entity entity = chunk.getEntity(ref.getUniqueId()).orElse(null);
                    if (entity != null) {
                        ticket0.bindToEntity(entity);
                    } else {
                        // The entity is gone?
                        it.remove();
                    }
                } else {
                    // The entity is gone?
                    it.remove();
                }
            }
        }
        for (Entry<String, Collection<LanternLoadingTicket>> ticket : tickets.asMap().entrySet()) {
            Collection<ChunkTicketManager.Callback> callbacks = this.chunkLoadService.getCallbacks().get(ticket.getKey());
            ImmutableList<LoadingTicket> tickets0 = ImmutableList.copyOf(ticket.getValue());
            ImmutableListMultimap<UUID, LoadingTicket> map = null;
            for (ChunkTicketManager.Callback callback : callbacks) {
                if (callback instanceof ChunkTicketManager.OrderedCallback) {
                    List<LoadingTicket> result = ((ChunkTicketManager.OrderedCallback) callback).onLoaded(
                            tickets0, this.world, this.chunkLoadService.getMaxTicketsById(ticket.getKey()));
                    if (result == null) {
                        throw new IllegalStateException("The OrderedCallback#onLoaded method may not return null, error caused by (plugin="
                                + ticket.getKey() + ", clazz=" + callback.getClass().getName() + ")");
                    }
                    tickets0 = ImmutableList.copyOf(result);
                }
                if (callback instanceof ChunkTicketManager.PlayerOrderedCallback) {
                    if (map == null) {
                        ImmutableListMultimap.Builder<UUID, LoadingTicket> mapBuilder = ImmutableListMultimap.builder();
                        tickets0.stream().filter(ticket0 -> ticket0 instanceof PlayerLoadingTicket).forEach(ticket0 ->
                                mapBuilder.put(((PlayerLoadingTicket) ticket0).getPlayerUniqueId(), ticket0));
                        map = mapBuilder.build();
                    }
                    ListMultimap<UUID, LoadingTicket> result = ((ChunkTicketManager.PlayerOrderedCallback) callback)
                            .onPlayerLoaded(map, this.world);
                    if (result == null) {
                        throw new IllegalStateException("The PlayerOrderedCallback#onPlayerLoaded method may not return null, "
                                + "error caused by (plugin=" + ticket.getKey() + ", clazz=" + callback.getClass().getName() + ")");
                    }
                    map = ImmutableListMultimap.copyOf(result);
                }
                callback.onLoaded(tickets0, this.world);
            }
        }
    }
}
