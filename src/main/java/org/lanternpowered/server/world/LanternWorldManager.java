/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.server.world;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.lanternpowered.server.config.GlobalConfig;
import org.lanternpowered.server.config.world.WorldConfig;
import org.lanternpowered.server.data.io.ScoreboardIO;
import org.lanternpowered.server.event.CauseStack;
import org.lanternpowered.server.event.LanternCauseStack;
import org.lanternpowered.server.game.DirectoryKeys;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.util.ThreadHelper;
import org.lanternpowered.server.world.LanternWorldPropertiesIO.LevelData;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.util.Functional;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

@Singleton
public final class LanternWorldManager {

    // The counter for the executor threads
    private final AtomicInteger counter = new AtomicInteger();
    // The executor for async world manager operations
    private final ExecutorService executor = Executors.newCachedThreadPool(
            runnable -> new Thread(runnable, "worlds-" + this.counter.getAndIncrement()));

    // The name of the world configs
    private static final String WORLD_CONFIG = "world.conf";

    // The size of the dimension map
    static final int DIMENSION_MAP_SIZE = Long.SIZE << 4;

    // A lookup entry used to store world references
    private static class WorldLookupEntry {

        // The world properties
        public final LanternWorldProperties properties;

        // The folder where all the world files are stored
        public final Path folder;

        // The dimension id of the world
        public final int dimensionId;

        // The reference to the world instance
        @Nullable public volatile LanternWorld world;

        WorldLookupEntry(LanternWorldProperties properties, Path folder, int dimensionId) {
            this.dimensionId = dimensionId;
            this.properties = properties;
            this.folder = folder;
        }
    }

    // A map with all the world threads
    private final Map<LanternWorld, Thread> worldThreads = new ConcurrentHashMap<>();

    // The world entries indexed by the name
    private final Map<LanternWorldProperties, WorldLookupEntry> worldByProperties = new ConcurrentHashMap<>();

    // The world entries indexed by the name
    private final Map<String, WorldLookupEntry> worldByName = new ConcurrentHashMap<>();

    // The world entries indexed by the unique ids
    private final Map<UUID, WorldLookupEntry> worldByUUID = new ConcurrentHashMap<>();

    // The world entries indexed by the dimension ids
    private final Map<Integer, WorldLookupEntry> worldByDimensionId = new ConcurrentHashMap<>();

    // The map of all the dimension ids
    private BitSet dimensionMap;

    @Inject private Logger logger;

    // The global configuration file
    @Inject private GlobalConfig globalConfig;

    // The game instance
    @Inject private LanternGame game;

    // The directory of the root world
    @Inject @Named(DirectoryKeys.ROOT_WORLD) private Provider<Path> rootWorldDirectory;

    // The phasers to synchronize the world threads
    private final Phaser tickBegin = new Phaser(1);
    private final Phaser tickEnd = new Phaser(1);

    @Inject
    private LanternWorldManager() {
    }

    /**
     * Gets a loaded {@link World} by name, if it exists.
     *
     * @param worldName name to lookup
     * @return the world, if found
     */
    public Optional<World> getWorld(String worldName) {
        checkNotNull(worldName, "worldName");
        return this.worldByName.containsKey(worldName) ? Optional.ofNullable(this.worldByName.get(worldName).world) : Optional.empty();
    }

    /**
     * Gets all currently loaded {@link World}s.
     *
     * @return a collection of loaded worlds
     */
    public Collection<World> getWorlds() {
        return this.worldByUUID.values().stream().filter(e -> e.world != null).map(e -> e.world).collect(ImmutableList.toImmutableList());
    }

    /**
     * Gets a loaded {@link World} by its unique id ({@link UUID}), if it
     * exists.
     *
     * @param uniqueId uuid to lookup
     * @return the world, if found
     */
    public Optional<World> getWorld(UUID uniqueId) {
        checkNotNull(uniqueId, "uniqueId");
        return this.worldByUUID.containsKey(uniqueId) ? Optional.ofNullable(this.worldByUUID.get(uniqueId).world) : Optional.empty();
    }

    /**
     * Gets the properties of all worlds, loaded or otherwise.
     *
     * @return a collection of world properties
     */
    public Collection<WorldProperties> getAllWorldProperties() {
        return this.worldByUUID.values().stream().map(e -> e.properties).collect(ImmutableList.toImmutableList());
    }

    /**
     * Gets the properties of all unloaded worlds.
     *
     * @return a collection of world properties
     */
    public Collection<WorldProperties> getUnloadedWorlds() {
        return this.worldByUUID.values().stream().filter(e -> e.world == null).map(e -> e.properties).collect(ImmutableList.toImmutableList());
    }

    /**
     * Gets the {@link WorldProperties} of a world. If a world with the given
     * name is loaded then this is equivalent to calling
     * {@link World#getProperties()}. However, if no loaded world is found then
     * an attempt will be made to match unloaded worlds.
     *
     * @param worldName the name to lookup
     * @return the world properties, if found
     */
    public Optional<WorldProperties> getWorldProperties(String worldName) {
        checkNotNull(worldName, "worldName");
        WorldLookupEntry entry = this.worldByName.get(worldName);
        return entry != null ? Optional.of(entry.properties) : Optional.empty();
    }

    /**
     * Gets the {@link WorldProperties} of a world. If a world with the given
     * UUID is loaded then this is equivalent to calling
     * {@link World#getProperties()}. However, if no loaded world is found then
     * an attempt will be made to match unloaded worlds.
     *
     * @param uniqueId the uuid to lookup
     * @return the world properties, if found
     */
    public Optional<WorldProperties> getWorldProperties(UUID uniqueId) {
        checkNotNull(uniqueId, "uniqueId");
        WorldLookupEntry entry = this.worldByUUID.get(uniqueId);
        return entry != null ? Optional.of(entry.properties) : Optional.empty();
    }

    /**
     * Gets the {@link WorldProperties} of a world. If a world with the given
     * UUID is loaded then this is equivalent to calling
     * {@link World#getProperties()}. However, if no loaded world is found then
     * an attempt will be made to match unloaded worlds.
     *
     * @param dimensionId the uuid to lookup
     * @return the world properties, if found
     */
    public Optional<WorldProperties> getWorldProperties(int dimensionId) {
        WorldLookupEntry entry = this.worldByDimensionId.get(dimensionId);
        return entry != null ? Optional.of(entry.properties) : Optional.empty();
    }

    public Optional<Integer> getWorldDimensionId(UUID uniqueId) {
        checkNotNull(uniqueId, "uniqueId");
        WorldLookupEntry entry = this.worldByUUID.get(uniqueId);
        return entry != null ? Optional.of(entry.dimensionId) : Optional.empty();
    }

    /**
     * Gets the properties of default world.
     *
     * @return the world properties
     */
    public Optional<WorldProperties> getDefaultWorld() {
        final WorldLookupEntry entry = this.worldByDimensionId.get(0);
        // Can be empty if the properties aren't loaded yet
        return entry != null ? Optional.of(entry.properties) : Optional.empty();
    }

    /**
     * Unloads a {@link World}, if there are any connected players in the given
     * world then no operation will occur.
     *
     * <p>A world which is unloaded will be removed from memory. However if it
     * is still enabled according to {@link WorldProperties#isEnabled()} then it
     * will be loaded again if the server is restarted or an attempt is made by
     * a plugin to transfer an entity to the world using
     * {@link org.spongepowered.api.entity.Entity#transferToWorld(World, Vector3d)}.</p>
     *
     * @param world the world to unload
     * @return whether the operation was successful
     */
    public boolean unloadWorld(World world) {
        checkNotNull(world, "world");
        final LanternWorld world0 = (LanternWorld) world;
        // We cannot unload the world if there are
        // still players active
        if (!world0.getPlayers().isEmpty()) {
            return false;
        }
        final CauseStack causeStack = CauseStack.currentOrEmpty();
        causeStack.pushCause(world);
        // Post the unload world event
        this.game.getEventManager().post(SpongeEventFactory.createUnloadWorldEvent(
                causeStack.getCurrentCause(), world));
        causeStack.popCause();
        // Save all the world data
        world0.shutdown();
        // Remove the tick task
        removeWorldTask(world0);
        // Get the lookup entry and properties to remove all the references
        final LanternWorldProperties properties = world0.getProperties();
        final WorldLookupEntry entry = this.worldByProperties.get(properties);
        properties.setWorld(null);
        entry.world = null;
        // Save the world properties
        this.saveWorldProperties(properties);
        try {
            ScoreboardIO.write(entry.folder, world0.getScoreboard());
        } catch (IOException e) {
            Lantern.getLogger().warn("An error occurred while saving the scoreboard data.", e);
        }
        return true;
    }

    /**
     * Creates a world copy asynchronously using the new name given and returns
     * the new world properties if the copy was possible.
     *
     * <p>If the world is already loaded then the following will occur:</p>
     *
     * <ul>
     * <li>World is saved.</li>
     * <li>World saving is disabled.</li>
     * <li>World is copied. </li>
     * <li>World saving is enabled.</li>
     * </ul>
     *
     * @param worldProperties The world properties to copy
     * @param copyName The name for copied world
     * @return An {@link Optional} containing the properties of the new world
     *         instance, if the copy was successful
     */
    public CompletableFuture<Optional<WorldProperties>> copyWorld(WorldProperties worldProperties, String copyName) {
        checkNotNull(worldProperties, "worldProperties");
        checkNotNull(copyName, "copyName");
        return Functional.asyncFailableFuture(() -> {
            // Get the new dimension id
            final int dimensionId = getNextFreeDimensionId();

            // TODO: Save the world if loaded
            // TODO: Block world saving

            // Get the lookup entry
            final WorldLookupEntry entry = this.worldByProperties.get(worldProperties);

            // The folder of the new world
            final Path targetFolder = this.getWorldFolder(copyName, dimensionId);
            // The folder of the original world
            final Path folder = entry.folder;

            if (Files.exists(targetFolder) && Files.list(targetFolder).count() > 0) {
                this.logger.error("The copy world folder already exists and it isn't empty!");
                return Optional.empty();
            }

            // Save the changes once more to make sure that they will be saved
            saveWorldProperties(worldProperties);

            // Copy the world folder
            final String folderPath = folder.toFile().getAbsolutePath();
            try {
                Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                        final Path dstPath = targetFolder.resolve(path.toFile()
                                .getAbsolutePath().substring(folderPath.length()));
                        Files.copy(path, dstPath, StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                this.logger.error("Failed to copy the world folder of {}: {} to {}",
                        copyName, folder, targetFolder, e);
                return Optional.empty();
            }

            final WorldConfigResult result = this.getOrCreateWorldConfig(copyName);
            // Copy the settings
            result.config.copyFrom(entry.properties.getConfig());
            result.config.save();

            final LevelData levelData;
            final LanternWorldProperties properties;
            try {
                levelData = LanternWorldPropertiesIO.read(folder, copyName, null);
                properties = LanternWorldPropertiesIO.convert(levelData, result.config, false);
            } catch (IOException e) {
                this.logger.error("Unable to open the copied world properties of {}", copyName, e);
                return Optional.empty();
            }

            final LevelData newData = new LevelData(levelData.worldName, levelData.uniqueId, levelData.worldData,
                    levelData.spongeWorldData, dimensionId, null);

            // Store the new world
            this.addUpdatedWorldProperties(properties, targetFolder, dimensionId);

            // Save the changes once more to make sure that they will be saved
            LanternWorldPropertiesIO.write(targetFolder, newData);

            return Optional.of(properties);
        }, this.executor);
    }

    /**
     * Renames an unloaded world.
     *
     * @param worldProperties The world properties to rename
     * @param newName The name that should be used as a replacement for the
     *        current world name
     * @return An {@link Optional} containing the new {@link WorldProperties}
     *         if the rename was successful
     */
    public Optional<WorldProperties> renameWorld(WorldProperties worldProperties, String newName) {
        checkNotNull(worldProperties, "worldProperties");
        checkNotNull(newName, "newName");
        // There already exists a world with that name
        if (this.worldByName.containsKey(newName)) {
            return Optional.empty();
        }
        final WorldLookupEntry entry = this.worldByProperties.get(worldProperties);
        // You cannot rename a loaded world
        if (entry.world != null || this.getWorld(worldProperties.getWorldName()).isPresent()) {
            return Optional.empty();
        }
        final LanternWorldProperties worldProperties0 = (LanternWorldProperties) worldProperties;
        this.worldByName.put(newName, entry);
        this.worldByName.remove(worldProperties0.getWorldName());
        worldProperties0.setName(newName);

        // Save the changes once more to make sure that they will be saved
        this.saveWorldProperties(worldProperties0);
        return Optional.of(worldProperties0);
    }

    /**
     * Deletes the provided world's files asynchronously from the disk.
     *
     * @param worldProperties the world properties to delete
     * @return true if the deletion was successful
     */
    public CompletableFuture<Boolean> deleteWorld(WorldProperties worldProperties) {
        checkNotNull(worldProperties, "worldProperties");
        return Functional.asyncFailableFuture(() -> {
            final WorldLookupEntry entry = this.worldByProperties.get(worldProperties);
            if (entry.world != null) {
                return false;
            }
            final boolean[] flag = { true };
            Files.walkFileTree(entry.folder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        logger.error("Unable to delete the file {} of world {}",
                                path.toFile().getAbsolutePath(), worldProperties.getWorldName(), e);
                        flag[0] = false;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            this.worldByName.remove(worldProperties.getWorldName());
            this.worldByDimensionId.remove(entry.dimensionId);
            this.worldByProperties.remove(worldProperties);
            this.worldByUUID.remove(worldProperties.getUniqueId());
            this.dimensionMap.clear(entry.dimensionId);
            Files.delete(entry.folder);
            return flag[0];
        }, this.executor);
    }

    /**
     * Persists the given {@link WorldProperties} to the world storage for it,
     * updating any modified values.
     *
     * @param worldProperties the world properties to save
     * @return true if the save was successful
     */
    public boolean saveWorldProperties(WorldProperties worldProperties) {
        checkNotNull(worldProperties, "worldProperties");
        final WorldLookupEntry entry = this.worldByProperties.get(worldProperties);
        checkNotNull(entry, "entry");
        final LanternWorldProperties worldProperties0 = (LanternWorldProperties) worldProperties;
        final BitSet dimensionMap = entry.dimensionId == 0 ? (BitSet) this.dimensionMap.clone() : null;
        try {
            final LevelData levelData = LanternWorldPropertiesIO.convert(worldProperties0, entry.dimensionId, dimensionMap);
            LanternWorldPropertiesIO.write(entry.folder, levelData);
            worldProperties0.getConfig().save();
        } catch (IOException e) {
            this.logger.error("Unable to save the world properties of {}: {}",
                    worldProperties.getWorldName(), e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * Creates a new world from the given {@link WorldArchetype}. For the
     * creation of the WorldCreationSettings please see
     * {@link org.spongepowered.api.world.WorldArchetype.Builder}.
     *
     * <p>If the world already exists then the existing {@link WorldProperties}
     * are returned else a new world is created and the new WorldProperties
     * returned.</p>
     *
     * <p>Although the world is created it is not loaded at this time. Please
     * see one of the following methods for loading the world.</p>
     *
     * <ul> <li>{@link #loadWorld(String)}</li> <li>{@link #loadWorld(UUID)}
     * </li> <li>{@link #loadWorld(WorldProperties)}</li> </ul>
     *
     * @param worldArchetype The world archetype for creation
     * @return The new or existing world properties, if creation was successful
     */
    public WorldProperties createWorldProperties(String folderName, WorldArchetype worldArchetype) throws IOException {
        checkNotNull(worldArchetype, "worldArchetype");
        WorldLookupEntry entry = this.worldByName.get(worldArchetype.getName());
        if (entry != null) {
            return entry.properties;
        }
        // Get the next dimension id
        final int dimensionId = this.getNextFreeDimensionId();
        // Create the world properties
        return this.createWorld(worldArchetype, folderName, dimensionId);
    }

    /**
     * Creates the world properties for the specified settings and the dimension id.
     * 
     * @param worldArchetype The world archetype
     * @param folderName The folder name
     * @param dimensionId The dimension id
     * @return The new or existing world properties, if creation was successful
     */
    private LanternWorldProperties createWorld(WorldArchetype worldArchetype, String folderName, int dimensionId) throws IOException {
        final LanternWorldArchetype settings0 = (LanternWorldArchetype) checkNotNull(worldArchetype, "worldArchetype");
        final String worldName = worldArchetype.getName();
        final WorldLookupEntry entry = this.worldByName.get(worldName);
        if (entry != null) {
            return entry.properties;
        }
        final WorldConfigResult worldConfigResult;
        // Create a config
        try {
            worldConfigResult = getOrCreateWorldConfig(worldName);
        } catch (IOException e) {
            throw new IOException("Unable to read/write the world config, please fix this issue before"
                    + " creating the world.", e);
        }
        // Create the world properties
        final LanternWorldProperties worldProperties = new LanternWorldProperties(settings0.getName(), worldConfigResult.config);
        if (worldConfigResult.newCreated) {
            worldProperties.update(settings0);
        }

        // Get the world folder
        final Path worldFolder = getWorldFolder(folderName, dimensionId);
        try {
            Files.createDirectories(worldFolder);
        } catch (IOException e) {
            throw new IOException("Unable to create the world folders for " + settings0.getName(), e);
        }
        // Store the new properties
        addWorldProperties(worldProperties, worldFolder, dimensionId);
        final CauseStack causeStack = CauseStack.currentOrEmpty();
        Sponge.getEventManager().post(SpongeEventFactory.createConstructWorldPropertiesEvent(
                causeStack.getCurrentCause(), worldArchetype, worldProperties));
        // Save the world properties to reserve the world folder
        saveWorldProperties(worldProperties);
        return worldProperties;
    }

    /**
     * Loads a {@link World} from the default storage container. If a world with
     * the given UUID is already loaded then it is returned instead.
     *
     * @param uniqueId the uuid to lookup
     * @return the world, if found
     */
    public Optional<World> loadWorld(UUID uniqueId) {
        checkNotNull(uniqueId, "uniqueId");
        return loadWorld(this.worldByUUID.get(uniqueId));
    }

    /**
     * Loads a {@link World} from the default storage container. If the world
     * associated with the given properties is already loaded then it is
     * returned instead.
     *
     * @param worldProperties the properties of the world to load
     * @return the world, if found
     */
    public Optional<World> loadWorld(WorldProperties worldProperties) {
        checkNotNull(worldProperties, "worldProperties");
        //noinspection SuspiciousMethodCalls
        return loadWorld(this.worldByProperties.get(worldProperties));
    }

    /**
     * Loads a {@link World} from the default storage container. If a world with
     * the given name is already loaded then it is returned instead.
     *
     * @param worldName the name to lookup
     * @return the world, if found
     */
    public Optional<World> loadWorld(String worldName) {
        checkNotNull(worldName, "worldName");
        return loadWorld(this.worldByName.get(worldName));
    }

    /**
     * Loads a {@link World} for the world entry if possible.
     * 
     * @param worldEntry the world entry
     * @return the world, if found
     */
    private Optional<World> loadWorld(@Nullable WorldLookupEntry worldEntry) {
        if (worldEntry == null) {
            return Optional.empty();
        }
        if (worldEntry.world != null) {
            return Optional.of(worldEntry.world);
        }
        WorldConfigResult result;
        try {
            result = getOrCreateWorldConfig(worldEntry.properties.getWorldName());
        } catch (IOException e) {
            this.game.getLogger().error("Unable to read the world config, please fix this issue before loading the world.", e);
            return Optional.empty();
        }
        Scoreboard scoreboard;
        try {
            scoreboard = ScoreboardIO.read(worldEntry.folder);
        } catch (IOException e) {
            this.logger.error("Unable to read the scoreboard data.", e);
            scoreboard = Scoreboard.builder().build();
        }
        // Create the world instance
        final LanternWorld world = new LanternWorld(this.game, result.config, worldEntry.folder, scoreboard, worldEntry.properties);
        // Share the world instance
        worldEntry.world = world;
        worldEntry.properties.setWorld(world);
        // Initialize the world if not done before
        world.initialize();
        // Generate the spawn if needed
        if (worldEntry.properties.doesKeepSpawnLoaded()) {
            world.enableSpawnArea(true);
        }
        // Load the chunk loading tickets, they may load some chunks
        try {
            world.getChunkManager().loadTickets();
        } catch (IOException e) {
            this.logger.warn("An error occurred while loading the chunk loading tickets", e);
        }
        final CauseStack causeStack = CauseStack.currentOrEmpty();
        causeStack.pushCause(world);
        final LoadWorldEvent event = SpongeEventFactory.createLoadWorldEvent(causeStack.getCurrentCause(), world);
        causeStack.popCause();
        Sponge.getEventManager().post(event);
        if (event.isCancelled()) {
            return Optional.empty();
        }
        // The world is ready for ticks
        addWorldTask(world);
        return Optional.of(world);
    }

    private static class WorldConfigResult {

        public final WorldConfig config;
        final boolean newCreated;

        WorldConfigResult(WorldConfig config, boolean newCreated) {
            this.newCreated = newCreated;
            this.config = config;
        }
    }

    /**
     * Gets or creates a new world config for the specified world.
     * 
     * @param worldName The name of the world
     * @return The world config
     * @throws IOException 
     */
    private WorldConfigResult getOrCreateWorldConfig(String worldName) throws IOException {
        checkNotNull(worldName, "worldName");
        final Path path = this.globalConfig.getPath().getParent().resolve("worlds")
                .resolve(worldName).resolve(WORLD_CONFIG);
        boolean newCreated = !Files.exists(path);
        final WorldConfig config = new WorldConfig(this.globalConfig, path);
        config.load();
        return new WorldConfigResult(config, newCreated);
    }

    /**
     * Adds the task for the world to tick it.
     */
    private void addWorldTask(LanternWorld world) {
        if (this.worldThreads.containsKey(world)) {
            return;
        }
        final Thread thread = ThreadHelper.newFastThreadLocalThread(thread0 -> {
            try {
                // Initialize the world cause stack.
                CauseStack.set(new LanternCauseStack());

                while (!thread0.isInterrupted() && !this.tickEnd.isTerminated()) {
                    this.tickBegin.arriveAndAwaitAdvance();
                    try {
                        world.pulse();
                    } catch (Exception e) {
                        this.logger.error("Error occurred while pulsing the world {}", world.getName(), e);
                    } finally {
                        this.tickEnd.arriveAndAwaitAdvance();
                    }
                }
            } finally {
                this.tickBegin.arriveAndDeregister();
                this.tickEnd.arriveAndDeregister();
            }
        }, "world-" + world.getName());
        this.worldThreads.put(world, thread);
        this.tickBegin.register();
        this.tickEnd.register();
        thread.start();
    }

    /**
     * Removes the task for the world to tick it.
     */
    private void removeWorldTask(LanternWorld world) {
        if (!this.worldThreads.containsKey(world)) {
            return;
        }
        this.worldThreads.remove(world).interrupt();
    }

    // The current tick that is executing
    private volatile int currentTick = -1;

    private void tickEnd() {
        int nextTick = this.currentTick + 1;
        // Mark ourselves as arrived so world threads automatically trigger advance once done
        int endPhase = this.tickEnd.arriveAndAwaitAdvance();
        if (endPhase != nextTick) {
            this.logger.warn("Tick end barrier {} has advanced differently from tick begin barrier: {}",
                    endPhase, nextTick);
        }
    }

    /**
     * Pulses the world for the next tick.
     */
    public void pulse() {
        try {
            this.tickEnd.awaitAdvanceInterruptibly(this.currentTick);
            this.currentTick = this.tickBegin.arrive();

            try {
                this.executor.submit(this::tickEnd);
            } catch (RejectedExecutionException ex) {
                shutdown();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Shutdown the all the world threads, the executor and
     * unloads all the active worlds.
     */
    public void shutdown() {
        // Unload all the active worlds
        this.worldByProperties.values().stream().filter(entry -> entry.world != null).forEach(entry -> unloadWorld(entry.world));
        this.tickBegin.forceTermination();
        this.tickEnd.forceTermination();
        this.worldThreads.clear();
        this.executor.shutdown();
    }

    /**
     * Gets the world folder for the dimension id.
     * 
     * @param dimensionId the dimension id
     * @return the world folder
     */
    private Path getWorldFolder(String folderName, int dimensionId) {
        final Path rootWorldDir = this.rootWorldDirectory.get();
        return dimensionId == 0 ? rootWorldDir : rootWorldDir.resolve(folderName);
    }

    private static final int MIN_CUSTOM_DIMENSION_ID = 2;

    /**
     * Gets the next available dimension id.
     * 
     * @return the next dimension id
     */
    private int getNextFreeDimensionId() {
        // Keep the ids -1; 0; 1 safe
        int next = MIN_CUSTOM_DIMENSION_ID;
        while (true) {
            next = this.dimensionMap.nextClearBit(next);
            if (this.worldByDimensionId.containsKey(next)) {
                this.dimensionMap.set(next);
            } else {
                return next;
            }
        }
    }

    private void addUpdatedWorldProperties(LanternWorldProperties properties, Path worldFolder, @Nullable Integer dimensionId) {
        // The world is already added
        if (this.worldByUUID.containsKey(properties.getUniqueId())) {
            return;
        }
        // Get the dimension id and make sure that it's not already used
        if (dimensionId == null || this.worldByDimensionId.containsValue(dimensionId)) {
            dimensionId = getNextFreeDimensionId();
            // Ignore the root dimension
        } else if (dimensionId > MIN_CUSTOM_DIMENSION_ID) {
            this.dimensionMap.set(dimensionId);
        }
        this.addWorldProperties(properties, worldFolder, dimensionId);
    }

    /**
     * Adds the world properties.
     * 
     * @param properties The properties
     * @param worldDirectory The directory of the world
     * @param dimensionId The id of the world (dimension)
     * @return The world lookup entry
     */
    private WorldLookupEntry addWorldProperties(LanternWorldProperties properties, Path worldDirectory, int dimensionId) {
        final WorldLookupEntry entry = new WorldLookupEntry(properties, worldDirectory, dimensionId);
        this.worldByUUID.put(properties.getUniqueId(), entry);
        this.worldByName.put(properties.getWorldName(), entry);
        this.worldByDimensionId.put(dimensionId, entry);
        this.worldByProperties.put(properties, entry);
        return entry;
    }

    /**
     * All the directories that should be ignored while loading worlds. We will
     * also add the nether and the end manually.
     */
    private final Set<String> ignoredDirectoryNames = Sets.newHashSet("data", "playerdata", "region", "stats", "advancements");

    /**
     * Initializes the root world and the dimension id map.
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    public void init() throws IOException {
        final Path rootWorldDir = this.rootWorldDirectory.get();
        // The properties of the root world
        LanternWorldProperties rootWorldProperties = null;
        LevelData levelData;

        if (Files.exists(rootWorldDir)) {
            try {
                levelData = LanternWorldPropertiesIO.read(rootWorldDir, null, null);
                // Create a config
                try {
                    final WorldConfigResult result = getOrCreateWorldConfig(levelData.worldName);
                    rootWorldProperties = LanternWorldPropertiesIO.convert(levelData, result.config, result.newCreated);
                    if (result.newCreated) {
                        result.config.save();
                    }
                } catch (IOException e) {
                    this.logger.error("Unable to read/write the root world config, please fix this issue before loading the world.", e);
                    throw e;
                }
                // Already store the data
                addUpdatedWorldProperties(rootWorldProperties, this.rootWorldDirectory.get(), 0);
            } catch (FileNotFoundException e) {
                // We can ignore this exception, because this means
                // that we have to generate the world
            } catch (IOException e) {
                this.logger.error("Unable to load root world, please fix this issue before starting the server.", e);
                throw e;
            }
        }
        // Always use a new dimension map, we will scan for the worlds
        // through folders and the dimension ids will be generated or
        // refreshed if needed
        this.dimensionMap = new BitSet();

        LanternWorldProperties rootWorldProperties0 = rootWorldProperties;
        // Generate the root (default) world if missing
        if (rootWorldProperties0 == null) {
            final String name = "Overworld";
            rootWorldProperties0 = createWorld(WorldArchetype.builder()
                    .from(WorldArchetypes.OVERWORLD)
                    .generator(GeneratorTypes.OVERWORLD)
                    .build(name, name), "", 0);
        }

        // Get all the dimensions (worlds) that should be loaded
        final List<WorldLookupEntry> loadQueue = new ArrayList<>(1);
        // Add the root dimension
        loadQueue.add(this.worldByDimensionId.get(0));

        final Map<Integer, Tuple<Path, LevelData>> idToLevelData = new HashMap<>();
        final List<Tuple<Path, LevelData>> levelDataWithoutId = new ArrayList<>();
        if (rootWorldProperties != null) {
            for (Path path : Files.list(rootWorldDir).filter(Files::isDirectory).collect(Collectors.toList())) {
                if (Files.list(path).count() == 0 || this.ignoredDirectoryNames.contains(
                        path.getFileName().toString().toLowerCase())) {
                    continue;
                }
                try {
                    try {
                        levelData = LanternWorldPropertiesIO.read(path, null, null);
                    } catch (FileNotFoundException e) {
                        this.logger.info("Found a invalid world directory {} inside the root world directory, the level.dat file is missing",
                                path.getFileName().toString());
                        continue;
                    }
                    final Integer dimensionId;
                    if (path.getFileName().toString().equalsIgnoreCase("DIM1")) {
                        dimensionId = 1;
                    } else if (path.getFileName().toString().equalsIgnoreCase("DIM-1")) {
                        dimensionId = -1;
                    } else if (levelData.dimensionId != null && levelData.dimensionId >= MIN_CUSTOM_DIMENSION_ID) {
                        dimensionId = levelData.dimensionId;
                    } else {
                        dimensionId = null;
                    }

                    final Tuple<Path, LevelData> tuple = Tuple.of(path, levelData);
                    if (dimensionId == null || idToLevelData.containsValue(dimensionId)) {
                        levelDataWithoutId.add(tuple);
                    } else {
                        idToLevelData.put(dimensionId, tuple);
                    }
                } catch (Exception e) {
                    this.logger.info("Unable to load the world in the directory {}",
                            path.getFileName().toString());
                }
            }
        }

        // Generate a dimension id for all the worlds that need it
        for (Tuple<Path, LevelData> tuple : levelDataWithoutId) {
            idToLevelData.put(getNextFreeDimensionId(), tuple);
        }
        levelDataWithoutId.clear();

        // Load the world properties and config files for all the worlds
        for (Map.Entry<Integer, Tuple<Path, LevelData>> entry : idToLevelData.entrySet()) {
            levelData = entry.getValue().getSecond();
            final LanternWorldProperties worldProperties;
            try {
                final WorldConfigResult result = getOrCreateWorldConfig(levelData.worldName);
                worldProperties = LanternWorldPropertiesIO.convert(levelData, result.config, result.newCreated);
                if (result.newCreated) {
                    result.config.save();
                }
            } catch (IOException e) {
                this.logger.error("Unable to read/write the world config, please fix this issue before loading the world.", e);
                throw e;
            }
            // Store the world properties
            final WorldLookupEntry lookupEntry = addWorldProperties(worldProperties,
                    entry.getValue().getFirst(), entry.getKey());
            // Check if it should be loaded on startup
            if (worldProperties.loadOnStartup()) {
                loadQueue.add(lookupEntry);
            }
        }
        if (!this.worldByDimensionId.containsKey(-1)) {
            final String name = "Nether";
            if (createWorld(WorldArchetype.builder()
                    .from(WorldArchetypes.THE_NETHER)
                    .generator(GeneratorTypes.NETHER)
                    .build(name, name), "DIM-1", -1).loadOnStartup()) {
                loadQueue.add(this.worldByDimensionId.get(-1));
            }
        }
        // The end
        if (!this.worldByDimensionId.containsKey(1)) {
            final String name = "TheEnd";
            if (createWorld(WorldArchetype.builder()
                    .from(WorldArchetypes.THE_END)
                    .generator(GeneratorTypes.THE_END)
                    .build(name, name), "DIM1", 1).loadOnStartup()) {
                loadQueue.add(this.worldByDimensionId.get(1));
            }
        }

        // The root world must be enabled
        rootWorldProperties0.setEnabled(true);

        // Load all the worlds
        loadQueue.forEach(this::loadWorld);
    }
}
