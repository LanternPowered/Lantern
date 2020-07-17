/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.world.chunk;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkPlugin;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_AREA;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_HEIGHT;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTIONS;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTION_SIZE;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTION_VOLUME;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_BIOME_VOLUME;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.api.util.concurrent.SoftThreadLocal;
import org.lanternpowered.server.config.world.WorldConfig;
import org.lanternpowered.server.data.io.ChunkIOService;
import org.lanternpowered.server.game.DirectoryKeys;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.util.ThreadHelper;
import org.lanternpowered.server.util.gen.biome.IntArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.biome.ObjectArrayImmutableBiomeBuffer;
import org.lanternpowered.server.util.gen.block.AbstractMutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.AtomicIntArrayMutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.IntArrayImmutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.IntArrayMutableBlockBuffer;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.chunk.LanternChunk.ChunkSection;
import org.lanternpowered.server.world.extent.ExtentBufferHelper;
import org.lanternpowered.server.world.extent.SoftBufferExtentViewDownsize;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.world.biome.BiomeGenerationSettings;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.biome.VirtualBiomeType;
import org.spongepowered.api.world.chunk.Chunk;
import org.spongepowered.api.world.chunk.ChunkTicketManager;
import org.spongepowered.api.world.chunk.ChunkTicketManager.EntityLoadingTicket;
import org.spongepowered.api.world.chunk.ChunkTicketManager.LoadingTicket;
import org.spongepowered.api.world.chunk.ChunkTicketManager.PlayerEntityLoadingTicket;
import org.spongepowered.api.world.chunk.ChunkTicketManager.PlayerLoadingTicket;
import org.spongepowered.api.world.gen.GenerationPopulator;
import org.spongepowered.api.world.gen.Populator;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.math.vector.Vector2i;
import org.spongepowered.math.vector.Vector3i;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class LanternChunkManager {

    // The maximum amount of threads that can load chunks asynchronously
    private static final int CHUNK_LOADING_MAX_POOL_SIZE = 10;

    // The core amount of threads that can load chunks asynchronously
    private static final int CHUNK_LOADING_CORE_POOL_SIZE = 4;

    // The delay to unload chunks that are not forced,
    // loaded through loadChunk methods
    private static final long UNLOAD_DELAY = TimeUnit.SECONDS.toMillis(1);

    // All the attached tickets mapped by the forced chunk coordinates
    private final Map<Vector2i, Set<LanternChunkLoadingTicket>> ticketsByPos = new ConcurrentHashMap<>();

    // All the loading tickets that are still usable
    private final Set<LanternLoadingTicket> tickets = Sets.newConcurrentHashSet();

    // All the chunks that are loaded into the server
    private final Map<Vector2i, LanternChunk> loadedChunks = new ConcurrentHashMap<>();

    // A cache that can be used to get chunks that weren't unloaded
    // so much after all, because of active references to the chunk
    private final Map<Vector2i, LanternChunk> reusableChunks = new MapMaker().weakValues().makeMap();

    // A set which contains chunks that are pending for removal,
    // chunks loaded by loadChunk may not have been locked in the process,
    // and using a queue for removal should prevent the chunks from unloading too early
    private final Queue<UnloadingChunkEntry> pendingForUnload = new ConcurrentLinkedQueue<>();

    private final PluginContainer minecraftPluginContainer;

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
    private final Map<Vector2i, LanternChunkQueueTask> chunkQueueTasks = new ConcurrentHashMap<>();

    // The chunk load executor
    private final ThreadPoolExecutor chunkTaskExecutor = new ThreadPoolExecutor(
            CHUNK_LOADING_CORE_POOL_SIZE, CHUNK_LOADING_MAX_POOL_SIZE, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
            ThreadHelper.newThreadFactory());

    // Some objects that can be used in {@link Chunk} population.
    private class PopulationData {

        private final Random random = new Random();
        private final LanternChunkLoadingTicket lockTicket = new InternalLoadingTicket();
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

        LanternChunkQueueTask(Vector2i coords, Runnable runnable) {
            this.runnable = runnable;
            this.coords = coords;
        }

        public void setFuture(Future<Void> future) {
            this.future = future;
            synchronized (this) {
                notifyAll();
            }
        }

        @Override
        public Void call() throws Exception {
            // Wait for the future to be set, in case it's getting directly executed
            synchronized (this) {
                while (this.future == null) {
                    try {
                        wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            this.runnable.run();
            return null;
        }

        boolean cancel() {
            // We have to wait for the future to be set before we
            // can cancel it, shouldn't be long
            synchronized (this) {
                while (this.future == null) {
                    try {
                        wait();
                    } catch (InterruptedException ignored) {
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
            unload0(this.callable.coords, CauseStack.currentOrEmpty(), false);
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
        Set<LanternChunkLoadingTicket> tickets = this.ticketsByPos.get(coords);
        if (tickets == null) {
            return;
        }
        tickets = new HashSet<>(tickets);
        final CauseStack causeStack = CauseStack.current();
        tickets.forEach(causeStack::pushCause);
        // Chunk may be null if's already being loaded by a different thread.
        getOrCreateChunk(coords, causeStack, true, false);
        causeStack.popCauses(tickets.size());
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
    private final SoftThreadLocal<GenerationBuffers> genBuffers = new SoftThreadLocal<>(GenerationBuffers::new);

    // The randoms that will be shared for population
    private final SoftThreadLocal<PopulationData> populationData = new SoftThreadLocal<>(PopulationData::new);

    // The world generator
    private volatile WorldGenerator worldGenerator;

    @Inject
    public LanternChunkManager(
            @Named(InternalPluginsInfo.Minecraft.IDENTIFIER) PluginContainer minecraftPluginContainer,
            LanternGame game, LanternWorld world, WorldConfig worldConfig,
            LanternChunkTicketManager chunkLoadService, ChunkIOService chunkIOService,
            WorldGenerator worldGenerator,
            @Named(DirectoryKeys.WORLD) Path worldFolder) {
        this.minecraftPluginContainer = minecraftPluginContainer;
        this.chunkLoadService = chunkLoadService;
        this.chunkIOService = chunkIOService;
        this.worldGenerator = worldGenerator;
        this.worldFolder = worldFolder;
        this.worldConfig = worldConfig;
        this.world = world;
        this.game = game;
    }

    public LanternWorld getWorld() {
        return this.world;
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
        for (Entry<Vector2i, Set<LanternChunkLoadingTicket>> en : this.ticketsByPos.entrySet()) {
            final Vector2i pos0 = en.getKey();
            final Vector3i pos = new Vector3i(pos0.getX(), 0, pos0.getY());
            for (LanternChunkLoadingTicket ticket : en.getValue()) {
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
        return getTicketsForPlugin(checkPlugin(plugin, "plugin").getId());
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
        return getMaxTicketsForPlugin(checkPlugin(plugin, "plugin").getId());
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
        return getMaxChunksForPluginTicket(checkPlugin(plugin, "plugin").getId());
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
        if (getTicketsForPlugin(pluginId) >= getMaxTicketsForPlugin(pluginId)) {
            return Optional.empty();
        }
        final int maxChunks = getMaxChunksForPluginTicket(pluginId);
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
        if (getTicketsForPlugin(pluginId) >= getMaxTicketsForPlugin(pluginId)) {
            return Optional.empty();
        }
        final int maxChunks = getMaxChunksForPluginTicket(pluginId);
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
        if (getTicketsForPlugin(pluginId) >= getMaxTicketsForPlugin(pluginId) ||
                this.chunkLoadService.getAvailableTickets(player) <= 0) {
            return Optional.empty();
        }
        final int maxChunks = getMaxChunksForPluginTicket(pluginId);
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
        if (getTicketsForPlugin(pluginId) >= getMaxTicketsForPlugin(pluginId) ||
                this.chunkLoadService.getAvailableTickets(player) <= 0) {
            return Optional.empty();
        }
        final int maxChunks = getMaxChunksForPluginTicket(pluginId);
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
        return this.loadedChunks.values().stream().filter(Chunk::isLoaded).collect(ImmutableSet.toImmutableSet());
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
        return getChunk(coords, true);
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
    public LanternChunk getChunkIfLoaded(int x, int z) {
        return getChunkIfLoaded(new Vector2i(x, z));
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
        return getChunk(new Vector2i(x, z));
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
        return getOrCreateChunk(x, z, false);
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
        return getOrCreateChunk(x, z, CauseStack.currentOrEmpty(), generate);
    }

    /**
     * Gets a chunk safely (new one will be created) for the coordinates, may
     * not be loaded yet.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the chunk
     */
    public LanternChunk getOrCreateChunk(int x, int z, CauseStack causeStack, boolean generate) {
        return getOrCreateChunk(new Vector2i(x, z), causeStack, generate);
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
    public LanternChunk getOrCreateChunk(Vector2i coords, CauseStack cause, boolean generate) {
        return getOrCreateChunk(coords, cause, generate, true);
    }

    /**
     *
     * @param coords the coordinates of the chunk to load
     * @param causeStack a supplier of the cause that triggered the chunk load
     * @param generate whether the chunk should be generated if not found
     * @param wait whether the current thread should wait for the loading to finish, this should only
     *             be internally used inside the chunk manager
     * @return the chunk
     */
    private LanternChunk getOrCreateChunk(Vector2i coords, CauseStack causeStack, boolean generate, boolean wait) {
        checkNotNull(causeStack, "causeStack");
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
            this.game.getEventManager().post(SpongeEventFactory.createLoadChunkEvent(causeStack.getCurrentCause(), chunk));
            this.world.getEventListener().onLoadChunk(chunk);
            // Resurrect all the entities in the chunk
            chunk.resurrectEntities();
            this.world.addEntities(chunk.getEntities());
            return chunk;
        }
        boolean[] newChunk = new boolean[1];
        // Finally, create a new chunk if needed
        chunk = this.loadedChunks.computeIfAbsent(coords, coords0 -> {
            newChunk[0] = true;
            return new LanternChunk(this.world, coords0.getX(), coords0.getY());
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
        load(chunk, causeStack, generate);
        this.world.addEntities(chunk.getEntities());
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
     * |     #####|#####     | ### The volume you (the populator) should populate.
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
            lockInternally(side, populationData.lockTicket);
        }
        LanternChunk up = isChunkLoaded(sides[UP]);
        LanternChunk right = isChunkLoaded(sides[RIGHT]);
        LanternChunk rightUp = isChunkLoaded(sides[RIGHT_UP]);
        if (up != null && right != null && rightUp != null) {
            if (!chunk.populating && !chunk.populated) {
                populateChunk(chunk, cause, random);
            }
        }
        LanternChunk left = isChunkLoaded(sides[LEFT]);
        LanternChunk leftDown = isChunkLoaded(sides[LEFT_DOWN]);
        LanternChunk down = isChunkLoaded(sides[DOWN]);
        if (leftDown != null && left != null && down != null) {
            if (!leftDown.populating && !leftDown.populated) {
                populateChunk(leftDown, cause, random);
            }
        }
        if (up == null) { // Maybe it is loaded by now?
            up = isChunkLoaded(sides[UP]);
        }
        if (left == null) { // Maybe it is loaded by now?
            left = isChunkLoaded(sides[LEFT]);
        }
        LanternChunk leftUp = isChunkLoaded(sides[LEFT_UP]);
        if (left != null && leftUp != null && up != null) {
            if (!left.populating && !left.populated) {
                populateChunk(left, cause, random);
            }
        }
        if (right == null) { // Maybe it is loaded by now?
            right = isChunkLoaded(sides[RIGHT]);
        }
        if (down == null) { // Maybe it is loaded by now?
            down = isChunkLoaded(sides[DOWN]);
        }
        LanternChunk rightDown = isChunkLoaded(sides[RIGHT_DOWN]);
        if (down != null && rightDown != null && right != null) {
            if (!down.populating && !down.populated) {
                populateChunk(down, cause, random);
            }
        }
        for (Vector2i side : sides) {
            unlockInternally(side, populationData.lockTicket);
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

        //noinspection ConstantConditions
        final ChunkBiomeBuffer biomeBuffer = this.genBuffers.get().chunkBiomeBuffer;
        biomeBuffer.reuse(new Vector3i(chunkX + 8, 0, chunkZ + 8));

        // We ave to regenerate the biomes so that any
        // virtual biomes can be passed to the populator.
        final BiomeGenerator biomeGenerator = this.worldGenerator.getBiomeGenerator();
        biomeGenerator.generateBiomes(biomeBuffer);

        // Initialize the biomes into the chunk
        final ImmutableBiomeVolume immutableBiomeVolume = biomeBuffer.getImmutableBiomeCopy();
        chunk.initializeBiomes(biomeBuffer.detach().clone());

        // Using the biome at an arbitrary point within the chunk
        // ({16, 0, 16} in the vanilla game)
        final BiomeType biomeType = immutableBiomeVolume.getBiome(chunkX + 16, 0, chunkZ + 16);

        // Get the generation settings
        final BiomeGenerationSettings biomeGenSettings = this.worldGenerator.getBiomeSettings(biomeType);

        final List<Populator> populators = new LinkedList<>(biomeGenSettings.getPopulators());
        populators.addAll(this.worldGenerator.getPopulators());

        final EventManager eventManager = Sponge.getEventManager();

        final Vector3i min = new Vector3i(chunkX + 8, 0, chunkZ + 8);
        final Extent volume = new SoftBufferExtentViewDownsize(chunk.getWorld(), min,
                min.add(15, 0, 15), min.sub(8, 0, 8), min.add(23, 0, 23));

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
        this.world.getEventListener().onPopulateChunk(chunk);

        // We are done
        chunk.populated = true;
        chunk.populating = false;
    }

    @Nullable
    private LanternChunk isChunkLoaded(Vector2i pos) {
        final LanternChunk chunk = getChunk(pos, false);
        return chunk != null && chunk.loaded ? chunk : null;
    }

    /**
     * Loads the specified chunk and attempts to generate it if not done before
     * if {@code generate} is set to {@code true}.
     *
     * @param chunk the chunk to load
     * @param causeStack the cause stack
     * @param generate whether the chunk should be generated if missing
     * @return true if it was successful
     */
    public boolean load(LanternChunk chunk, CauseStack causeStack, boolean generate) {
        return load0(chunk, causeStack, generate, true);
    }

    private boolean load0(LanternChunk chunk, CauseStack causeStack, boolean generate, boolean wait) {
        checkNotNull(chunk, "chunk");
        checkNotNull(causeStack, "causeStack");
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
                    this.game.getEventManager().post(SpongeEventFactory.createLoadChunkEvent(causeStack.getCurrentCause(), chunk));
                    this.world.getEventListener().onLoadChunk(chunk);
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
            Cause cause0 = causeStack.getCurrentCause();
            // Generate chunk
            try {
                generate(chunk, cause0);
            } catch (Throwable e) {
                this.game.getLogger().error("Error while generating chunk ({};{})", chunk.getX(), chunk.getZ(), e);
                return success = false;
            }
            // Try to populate the chunk
            tryPopulateSurroundingChunks(chunk, cause0);
            this.game.getEventManager().post(SpongeEventFactory.createLoadChunkEvent(cause0, chunk));
            this.world.getEventListener().onLoadChunk(chunk);
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
     * @param chunk The chunk
     * @param cause The cause
     */
    private void generate(LanternChunk chunk, Cause cause) {
        final EventManager eventManager = Sponge.getEventManager();
        eventManager.post(SpongeEventFactory.createGenerateChunkEventPre(cause, chunk));

        final GenerationBuffers buffers = this.genBuffers.get();
        //noinspection ConstantConditions
        final ChunkBiomeBuffer biomeBuffer = buffers.chunkBiomeBuffer;
        biomeBuffer.reuse(new Vector3i(chunk.getX() << 4, 0, chunk.getZ() << 4));

        // Generate the biomes
        final BiomeGenerator biomeGenerator = this.worldGenerator.getBiomeGenerator();
        biomeGenerator.generateBiomes(biomeBuffer);

        // Initialize the biomes into the chunk
        final ImmutableBiomeVolume immutableBiomeVolume = biomeBuffer.getImmutableBiomeCopy();
        chunk.initializeBiomes(biomeBuffer.detach().clone());

        final ChunkBlockBuffer blockBuffer = buffers.chunkBlockBuffer;
        blockBuffer.reuse(new Vector3i(chunk.getX() << 4, 0, chunk.getZ() << 4));

        // Apply the main world generator
        final GenerationPopulator baseGenerator = this.worldGenerator.getBaseGenerationPopulator();
        baseGenerator.populate(this.world, blockBuffer, immutableBiomeVolume);

        // Get all the used biome types
        final Set<BiomeType> biomeTypes = ImmutableSet.copyOf(biomeBuffer.biomeTypes);
        for (BiomeType biomeType : biomeTypes) {
            final BiomeGenerationSettings settings = this.worldGenerator.getBiomeSettings(biomeType);
            for (GenerationPopulator generator : settings.getGenerationPopulators()) {
                generator.populate(this.world, blockBuffer, immutableBiomeVolume);
            }
        }

        // Apply the generator populators to complete the block buffer
        for (GenerationPopulator generator : this.worldGenerator.getGenerationPopulators()) {
            generator.populate(this.world, blockBuffer, immutableBiomeVolume);
        }

        // Create the chunk sections
        final ChunkSection[] sections = new ChunkSection[CHUNK_SECTIONS];
        for (int sy = 0; sy < CHUNK_SECTIONS; sy++) {
            final int nonAirCount = blockBuffer.nonAirCount[sy];
            if (nonAirCount > 0) {
                final ChunkBlockStateArray blockStateArray =
                        new ChunkBlockStateArray(blockBuffer.types[sy]);
                sections[sy] = new ChunkSection(blockStateArray);
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
    private final class ChunkBiomeBuffer extends IntArrayMutableBiomeBuffer {

        private final BiomeType[] biomeTypes;

        ChunkBiomeBuffer() {
            super(Vector3i.ZERO, CHUNK_BIOME_VOLUME);
            this.biomeTypes = new BiomeType[CHUNK_AREA];
            Arrays.fill(this.biomeTypes, BiomeTypes.OCEAN);
            detach();
        }

        @Override
        public void setBiome(int x, int y, int z, BiomeType biome) {
            super.setBiome(x, y, z, biome instanceof VirtualBiomeType ? ((VirtualBiomeType) biome).getPersistedType() : biome);
            this.biomeTypes[index(x, y, z)] = biome;
        }

        @Override
        public BiomeType getBiome(int x, int y, int z) {
            checkOpen();
            checkRange(x, y, z);
            return this.biomeTypes[index(x, y, z)];
        }

        @Override
        protected int index(int x, int y, int z) {
            return (z & 0xf) << 4 | x & 0xf;
        }

        @Override
        public void reuse(Vector3i start) {
            super.reuse(start);
            Arrays.fill(this.biomeTypes, BiomeTypes.OCEAN);
        }

        @Override
        public ImmutableBiomeVolume getImmutableBiomeCopy() {
            checkOpen();
            return new ObjectArrayImmutableBiomeBuffer(this.biomeTypes, this.start, this.size);
        }
    }

    /**
     * A custom block buffer that will be used to generate the chunks
     * in the world generation to increase the performance.
     */
    private final class ChunkBlockBuffer extends AbstractMutableBlockBuffer {

        private final int[][] types = new int[CHUNK_SECTIONS][CHUNK_SECTION_VOLUME];
        private final int[] nonAirCount = new int[CHUNK_SECTIONS];

        ChunkBlockBuffer() {
            super(Vector3i.ZERO, CHUNK_SIZE);
        }

        void reuse(Vector3i start) {
            this.start = checkNotNull(start, "start");
            this.end = this.start.add(this.size).sub(Vector3i.ONE);
            for (int i = 0; i < CHUNK_SECTIONS; i++) {
                Arrays.fill(this.types[i], (short) 0);
            }
            Arrays.fill(this.nonAirCount, 0);
        }

        @Override
        public boolean setBlock(int x, int y, int z, BlockState block) {
            checkNotNull(block, "blockState");
            checkRange(x, y, z);
            final int sy = y >> 4;
            final int index = ((y & 0xf) << 8) | ((z & 0xf) << 4) | x & 0xf;
            final int[] types = this.types[sy];
            final int type = BlockRegistryModule.get().getStateInternalId(block);
            if (type == 0 && types[index] != 0) {
                this.nonAirCount[sy]--;
            } else if (type != 0 && types[index] == 0) {
                this.nonAirCount[sy]++;
            }
            types[index] = type;
            return true;
        }

        @Override
        public BlockState getBlock(int x, int y, int z) {
            checkRange(x, y, z);
            return BlockRegistryModule.get().getStateByInternalId(this.types[y >> 4][((y & 0xf) << 8) | ((z & 0xf) << 4) | x & 0xf])
                    .orElse(BlockTypes.AIR.getDefaultState());
        }

        @Override
        public MutableBlockVolume getBlockCopy(StorageType type) {
            checkNotNull(type, "storageType");
            switch (type) {
                case STANDARD:
                    return new IntArrayMutableBlockBuffer(ExtentBufferHelper.copyToBlockArray(
                            this, this.start, this.end, this.size), this.start, this.size);
                case THREAD_SAFE:
                    return new AtomicIntArrayMutableBlockBuffer(ExtentBufferHelper.copyToBlockArray(
                            this, this.start, this.end, this.size), this.start, this.size);
                default:
                    throw new UnsupportedOperationException(type.name());
            }
        }

        @Override
        public ImmutableBlockVolume getImmutableBlockCopy() {
            return IntArrayImmutableBlockBuffer.newWithoutArrayClone(ExtentBufferHelper.copyToBlockArray(
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
            return save0(chunk);
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
     * @param causeStack the cause stack
     * @return true if it was successful
     */
    public boolean unload(int x, int z, CauseStack causeStack) {
        return unload(new Vector2i(x, z), causeStack);
    }

    /**
     * Attempts to unload the chunk at the specified coordinates.
     * 
     * @param coords the coordinates
     * @param causeStack the cause
     * @return true if it was successful
     */
    public boolean unload(Vector2i coords, CauseStack causeStack) {
        return unload0(coords, causeStack, true);
    }

    private boolean unload0(Vector2i coords, CauseStack causeStack, boolean wait) {
        checkNotNull(causeStack, "causeStack");
        final LanternChunk chunk = getChunk(checkNotNull(coords, "coords"));
        if (chunk != null) {
            return unload0(chunk, causeStack, wait);
        }
        return true;
    }

    /**
     * Attempts to unload the specified chunk.
     * 
     * @param chunk The chunk to unload
     * @param causeStack The cause stack
     * @return true if it was successful
     */
    public boolean unload(LanternChunk chunk, CauseStack causeStack) {
        checkNotNull(chunk, "chunk");
        checkNotNull(causeStack, "causeStack");
        return unload0(chunk, causeStack, true);
    }

    private boolean unload0(LanternChunk chunk, CauseStack causeStack, boolean wait) {
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
            this.game.getEventManager().post(SpongeEventFactory.createUnloadChunkEvent(causeStack.getCurrentCause(), chunk));
            this.world.getEventListener().onUnloadChunk(chunk);
            // Remove from the loaded chunks
            this.loadedChunks.remove(coords);
            // Move the chunk to the graveyard
            this.reusableChunks.put(coords, chunk);
            // Bury the entities
            chunk.buryEntities();
            save0(chunk);
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
    private boolean lockInternally(Vector2i coords, LanternChunkLoadingTicket ticket) {
        final boolean[] empty = new boolean[1];
        this.ticketsByPos.computeIfAbsent(coords, coords0 -> {
            empty[0] = true;
            return Sets.newConcurrentHashSet();
        }).add(ticket);
        return empty[0];
    }

    private boolean unlockInternally(Vector2i coords, LanternChunkLoadingTicket ticket) {
        final Set<LanternChunkLoadingTicket> set = this.ticketsByPos.get(coords);
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
        force(ticket, coords, true);
    }

    /**
     * Forces the specified chunk coordinates for a specific ticket.
     * 
     * @param ticket the ticket
     * @param coords the coordinates
     * @param callEvents whether the force chunk events should be called
     */
    void force(LanternLoadingTicket ticket, Vector2i coords, boolean callEvents) {
        final LanternChunk chunk = getChunk(coords, false);
        // The chunk at this coords is already loaded,
        // wa can call the event directly
        lockInternally(coords, ticket);
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
                        queueTask(coords1, new LanternChunkLoadTask(coords1)));
            }
        }
        if  (callEvents) {
            final Vector3i coords0 = new Vector3i(coords.getX(), 0, coords.getY());
            final CauseStack causeStack = CauseStack.currentOrNull();
            if (causeStack != null) {
                postForcedChunkEvent(causeStack, ticket, coords0);
            } else {
                Lantern.getSyncScheduler().submit(() -> postForcedChunkEvent(CauseStack.current(), ticket, coords0));
            }
        }
    }

    private void postForcedChunkEvent(CauseStack causeStack, LanternLoadingTicket ticket, Vector3i coords) {
        Sponge.getEventManager().post(SpongeEventFactory.createForcedChunkEvent(causeStack.getCurrentCause(), coords, ticket));
    }

    /**
     * Unforces the specified chunk coordinates for a specific ticket.
     * 
     * @param ticket the ticket
     * @param coords the coordinates
     */
    void unforce(LanternLoadingTicket ticket, Vector2i coords, @Nullable CauseStack causeStack) {
        if (unlockInternally(coords, ticket)) {
            final LanternChunk chunk = getChunk(coords, false);
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
        if (causeStack != null) {
            final Vector3i coords0 = new Vector3i(coords.getX(), 0, coords.getY());
            Sponge.getEventManager().post(SpongeEventFactory.createUnforcedChunkEvent(causeStack.getCurrentCause(), coords0, ticket));
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

    void attach(LanternLoadingTicket ticket) {
        this.tickets.add(ticket);
    }

    public void save() {
        try {
            LanternLoadingTicketIO.save(this.worldFolder, this.tickets);
        } catch (IOException e) {
            this.game.getLogger().warn("An error occurred while saving the chunk loading tickets", e);
        }
        for (Entry<Vector2i, LanternChunk> entry : this.loadedChunks.entrySet()) {
            // Save the chunk
            save(entry.getValue());
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
        final CauseStack causeStack = CauseStack.current();
        final Cause cause = causeStack.getCurrentCause();
        for (Entry<Vector2i, LanternChunk> entry : this.loadedChunks.entrySet()) {
            final LanternChunk chunk = entry.getValue();
            // Post the chunk unload event
            this.game.getEventManager().post(SpongeEventFactory.createUnloadChunkEvent(cause, chunk));
            // Save the chunk
            save(chunk);
        }
        // Cleanup
        this.loadedChunks.clear();
        this.reusableChunks.clear();
        this.chunkTaskExecutor.shutdown();
        try {
            this.chunkIOService.unload();
        } catch (IOException e) {
            this.game.getLogger().warn("An error occurred while unloading the chunk io service", e);
        }
    }

    /**
     * Pulses the chunk manager.
     */
    public void pulse(CauseStack causeStack) {
        UnloadingChunkEntry entry;
        while ((entry = this.pendingForUnload.peek()) != null &&
                (System.currentTimeMillis() - entry.time) > UNLOAD_DELAY) {
            this.pendingForUnload.poll();
            if (!this.ticketsByPos.containsKey(entry.coords)) {
                // TODO: Create unload tasks
                unload(entry.coords, causeStack);
            }
        }
    }

    public void loadTickets() throws IOException {
        final Multimap<String, LanternLoadingTicket> tickets = LanternLoadingTicketIO.load(this.worldFolder, this, this.chunkLoadService);
        final Iterator<Entry<String, LanternLoadingTicket>> it = tickets.entries().iterator();
        final CauseStack causeStack = CauseStack.current();
        while (it.hasNext()) {
            final LanternLoadingTicket ticket = it.next().getValue();
            if (ticket instanceof LanternEntityLoadingTicket) {
                final LanternEntityLoadingTicket ticket0 = (LanternEntityLoadingTicket) ticket;
                final EntityReference ref = ticket0.getEntityReference().orElse(null);
                if (ref != null) {
                    causeStack.pushCause(ticket0);
                    final LanternChunk chunk = getOrCreateChunk(ref.getChunkCoords(), causeStack, true, true);
                    causeStack.popCause();
                    final Entity entity = chunk.getEntity(ref.getUniqueId()).orElse(null);
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
        for (Entry<String, Collection<LanternLoadingTicket>> entry : tickets.asMap().entrySet()) {
            final Collection<ChunkTicketManager.Callback> callbacks = this.chunkLoadService.getCallbacks().get(entry.getKey());

            // These maps will be loaded lazily
            ImmutableListMultimap<UUID, LoadingTicket> playerLoadedTickets = null;
            ImmutableList<LoadingTicket> nonPlayerLoadedTickets = null;

            final Set<LoadingTicket> resultPlayerLoadedTickets = entry.getValue().stream()
                    .filter(ticket -> ticket instanceof PlayerLoadingTicket)
                    .collect(Collectors.toSet());
            final Set<LoadingTicket> resultNonPlayerLoadedTickets = entry.getValue().stream()
                    .filter(ticket -> !(ticket instanceof PlayerLoadingTicket))
                    .collect(Collectors.toSet());

            final int maxTickets = this.chunkLoadService.getMaxTicketsById(entry.getKey());

            for (ChunkTicketManager.Callback callback : callbacks) {
                if (callback instanceof ChunkTicketManager.OrderedCallback) {
                    if (nonPlayerLoadedTickets == null) {
                        nonPlayerLoadedTickets = ImmutableList.copyOf(resultNonPlayerLoadedTickets);
                        resultNonPlayerLoadedTickets.clear();
                    }
                    final List<LoadingTicket> result = ((ChunkTicketManager.OrderedCallback) callback).onLoaded(
                            nonPlayerLoadedTickets, this.world, maxTickets);
                    checkNotNull(result, "The OrderedCallback#onLoaded method may not return null, "
                            + "error caused by (plugin=%s, clazz=%s)", entry.getKey(), callback.getClass().getName());
                    resultNonPlayerLoadedTickets.addAll(result);
                }
                if (callback instanceof ChunkTicketManager.PlayerOrderedCallback) {
                    if (playerLoadedTickets == null) {
                        final ImmutableListMultimap.Builder<UUID, LoadingTicket> mapBuilder = ImmutableListMultimap.builder();
                        resultPlayerLoadedTickets.forEach(ticket -> mapBuilder.put(((PlayerLoadingTicket) ticket).getPlayerUniqueId(), ticket));
                        resultPlayerLoadedTickets.clear();
                        playerLoadedTickets = mapBuilder.build();
                    }
                    final ListMultimap<UUID, LoadingTicket> result = ((ChunkTicketManager.PlayerOrderedCallback) callback)
                            .onPlayerLoaded(playerLoadedTickets, this.world);
                    checkNotNull(result, "The PlayerOrderedCallback#onPlayerLoaded method may not return null, "
                            + "error caused by (plugin=%s, clazz=%s)", entry.getKey(), callback.getClass().getName());
                    resultPlayerLoadedTickets.addAll(result.values());
                }
            }

            final List<LoadingTicket> resultLoadedTickets = new ArrayList<>();
            resultLoadedTickets.addAll(resultPlayerLoadedTickets);
            resultLoadedTickets.addAll(resultNonPlayerLoadedTickets);

            // Lets see how many plugins attempted to add loading tickets
            final int sizeA = resultLoadedTickets.size();
            resultLoadedTickets.retainAll(entry.getValue());
            final int sizeB = resultLoadedTickets.size();

            if (sizeA != sizeB) {
                Lantern.getLogger().warn("The plugin {} attempted to add LoadingTicket's that were previously not present.", entry.getKey());
            }

            // Remove all the tickets that are already released
            resultLoadedTickets.removeIf(ticket -> ((LanternChunkLoadingTicket) ticket).isReleased());

            if (resultLoadedTickets.size() > maxTickets) {
                Lantern.getLogger().warn("The plugin {} has too many open chunk loading tickets {}. "
                        + "Excess will be dropped", entry.getKey(), resultLoadedTickets.size());
                resultLoadedTickets.subList(maxTickets, resultLoadedTickets.size()).clear();
            }

            // Release all the tickets that were no longer usable
            final List<LoadingTicket> removedTickets = new ArrayList<>(entry.getValue());
            removedTickets.removeAll(resultLoadedTickets);
            removedTickets.forEach(LoadingTicket::release);

            final ImmutableList<LoadingTicket> loadedTickets = ImmutableList.copyOf(resultLoadedTickets);
            for (ChunkTicketManager.Callback callback : callbacks) {
                callback.onLoaded(loadedTickets, this.world);
            }
        }
    }
}
