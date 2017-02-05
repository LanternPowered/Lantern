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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.world.chunk.LanternChunk.fixEntityYSection;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.SPACE_MAX;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.SPACE_MIN;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.lanternpowered.api.world.weather.WeatherUniverse;
import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorContextImpl;
import org.lanternpowered.server.behavior.Parameters;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.action.BlockAction;
import org.lanternpowered.server.block.behavior.types.InteractWithBlockBehavior;
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;
import org.lanternpowered.server.config.world.WorldConfig;
import org.lanternpowered.server.data.io.ChunkIOService;
import org.lanternpowered.server.data.io.ScoreboardIO;
import org.lanternpowered.server.data.io.anvil.AnvilChunkIOService;
import org.lanternpowered.server.effect.AbstractViewer;
import org.lanternpowered.server.effect.sound.LanternSoundType;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.LanternEntityType;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.entity.living.player.ObservedChunkManager;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.entity.EntityProtocolManager;
import org.lanternpowered.server.network.entity.EntityProtocolType;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutParticleEffect;
import org.lanternpowered.server.text.chat.LanternChatType;
import org.lanternpowered.server.text.title.LanternTitles;
import org.lanternpowered.server.util.VecHelper;
import org.lanternpowered.server.world.chunk.ChunkLoadingTicket;
import org.lanternpowered.server.world.chunk.LanternChunk;
import org.lanternpowered.server.world.chunk.LanternChunkManager;
import org.lanternpowered.server.world.chunk.LanternChunkTicketManager;
import org.lanternpowered.server.world.dimension.LanternDimensionType;
import org.lanternpowered.server.world.extent.AbstractExtent;
import org.lanternpowered.server.world.extent.ExtentViewDownsize;
import org.lanternpowered.server.world.extent.worker.LanternMutableBiomeVolumeWorker;
import org.lanternpowered.server.world.extent.worker.LanternMutableBlockVolumeWorker;
import org.lanternpowered.server.world.pregen.LanternChunkPreGenerateTask;
import org.lanternpowered.server.world.rules.Rule;
import org.lanternpowered.server.world.rules.RuleHolder;
import org.lanternpowered.server.world.rules.RuleType;
import org.lanternpowered.server.world.weather.LanternWeather;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.ScheduledBlockUpdate;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.ChunkPreGenerate;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.PortalAgent;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.api.world.extent.ArchetypeVolume;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.extent.worker.MutableBiomeVolumeWorker;
import org.spongepowered.api.world.extent.worker.MutableBlockVolumeWorker;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.storage.WorldStorage;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class LanternWorld implements AbstractExtent, org.lanternpowered.api.world.World, AbstractViewer, RuleHolder {

    public static final Vector3i BLOCK_MIN = new Vector3i(-30000000, 0, -30000000);
    public static final Vector3i BLOCK_MAX = new Vector3i(30000000, 256, 30000000).sub(1, 1, 1);
    public static final Vector3i BLOCK_SIZE = BLOCK_MAX.sub(BLOCK_MIN).add(1, 1, 1);
    public static final Vector3i BIOME_MIN = BLOCK_MIN.mul(1, 0, 1);
    public static final Vector3i BIOME_MAX = BLOCK_MAX.mul(1, 0, 1);
    public static final Vector3i BIOME_SIZE = BIOME_MAX.sub(BLOCK_MIN).add(1, 1, 1);

    // The spawn size starting from the spawn point and expanded
    // by this size in the directions +x, +z, -x, -z
    private final static int SPAWN_SIZE = 12;

    // The loading ticket to keep the spawn chunks loaded
    @Nullable private volatile ChunkLoadingTicket spawnLoadingTicket;

    // The game instance
    final LanternGame game;

    // The world border
    final LanternWorldBorder worldBorder;

    // The weather universe
    @Nullable final LanternWeatherUniverse weatherUniverse;
    private final TimeUniverse timeUniverse;

    // All the players in this world
    private final Set<LanternPlayer> players = Sets.newConcurrentHashSet();

    // All the players in this world
    private final Collection<Player> unmodifiablePlayers = Collections.unmodifiableCollection(this.players);

    // The chunk manager of this world
    private final LanternChunkManager chunkManager;

    /**
     * The entities mapped by their unique id.
     */
    private final Map<UUID, LanternEntity> entitiesByUniqueId = new ConcurrentHashMap<>();

    /**
     * The chunk manager that will allows observers to track
     * changes in chunks.
     */
    private final ObservedChunkManager observedChunkManager = new ObservedChunkManager(this);

    /**
     * The {@link Scoreboard} that is attached to this {@link World}.
     */
    private final Scoreboard scoreboard;

    // The dimension instance attached to this world
    private final Dimension dimension;

    // The world configuration
    private final WorldConfig worldConfig;

    // The properties of this world
    final LanternWorldProperties properties;

    private final PortalAgent portalAgent;

    // The context of this world
    private final Context worldContext;

    private final MultiWorldEventListener worldEventListener = new MultiWorldEventListener();

    /**
     * The directory where all the data of the
     * world is stored.
     */
    private final Path directory;

    /**
     * The message channel of the world.
     */
    private MessageChannel messageChannel = MessageChannel.world(this);

    /**
     * The entity protocol manager.
     */
    private EntityProtocolManager entityProtocolManager = new EntityProtocolManager();

    public LanternWorld(LanternGame game, WorldConfig worldConfig, Path directory,
            Scoreboard scoreboard, LanternWorldProperties properties) {
        this.directory = directory;
        this.worldConfig = worldConfig;
        this.scoreboard = scoreboard;
        this.properties = properties;
        this.game = game;
        // Create the chunk io service
        final ChunkIOService chunkIOService = new AnvilChunkIOService(directory, this);
        // Get the chunk load service
        final LanternChunkTicketManager chunkLoadService = game.getChunkTicketManager();
        // Get the dimension type
        final LanternDimensionType<?> dimensionType = (LanternDimensionType<?>) properties.getDimensionType();
        // Create the weather universe if needed
        if (dimensionType.hasSky()) {
            this.weatherUniverse = new LanternWeatherUniverse(this);
        } else {
            this.weatherUniverse = null;
        }
        this.timeUniverse = new TimeUniverse(this);
        // Create the world border
        this.worldBorder = new LanternWorldBorder(this);
        // Create the dimension
        this.dimension = dimensionType.newDimension(this);
        // Create the portal agent
        this.portalAgent = properties.getPortalAgentType().newPortalAgent(this);
        // Create a new world generator
        final WorldGenerator worldGenerator = properties.getGeneratorType().createGenerator(this);
        // Finally, create the chunk manager
        this.chunkManager = new LanternChunkManager(this.game, this, this.worldConfig, chunkLoadService,
                chunkIOService, worldGenerator, directory);
        this.worldContext = new Context(Context.WORLD_KEY, this.getName());
        this.worldEventListener.add(this.observedChunkManager);
    }

    @Override
    public Optional<WeatherUniverse> getWeatherUniverse() {
        return Optional.ofNullable(this.weatherUniverse);
    }

    public ObservedChunkManager getObservedChunkManager() {
        return this.observedChunkManager;
    }

    public void initialize() {
        // Initialize the world if needed
        if (this.properties.isInitialized()) {
            return;
        }
        this.properties.setInitialized(true);
    }

    /**
     * Gets the {@link Scoreboard} of this world.
     *
     * @return The scoreboard
     */
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    /**
     * Shuts the world down and saves all the
     * data in the process.
     */
    void shutdown() {
        // Release the spawn ticket to avoid it
        // getting saved
        if (this.spawnLoadingTicket != null) {
            this.spawnLoadingTicket.release();
            this.spawnLoadingTicket = null;
        }
        // Shut the chunk manager down
        this.chunkManager.shutdown();
    }

    /**
     * Enables whether the spawn volume should be generated and keeping it loaded.
     * 
     * @param keepSpawnLoaded keep spawn loaded
     */
    void enableSpawnArea(boolean keepSpawnLoaded) {
        if (keepSpawnLoaded) {
            final Vector3i spawnPoint = this.properties.getSpawnPosition();

            if (this.spawnLoadingTicket == null) {
                this.spawnLoadingTicket = (ChunkLoadingTicket) this.chunkManager.createTicket(
                        this.game.getMinecraftPlugin()).get();
            } else {
                this.spawnLoadingTicket.unforceChunks();
            }

            final int chunkX = spawnPoint.getX() >> 4;
            final int chunkZ = spawnPoint.getZ() >> 4;

            this.game.getLogger().info("Generating spawn volume...");

            for (int x = chunkX - SPAWN_SIZE; x < chunkX + SPAWN_SIZE; x++) {
                for (int z = chunkZ - SPAWN_SIZE; z < chunkZ + SPAWN_SIZE; z++) {
                    this.chunkManager.getOrCreateChunk(x, z,
                            () -> Cause.source(this.game.getMinecraftPlugin()).owner(this).build(), true);
                    this.spawnLoadingTicket.forceChunk(new Vector2i(x, z));
                }
            }

            this.game.getLogger().info("Finished generating spawn volume.");
        } else if (this.spawnLoadingTicket != null) {
            this.spawnLoadingTicket.unforceChunks();
        }
    }

    /**
     * Gets the players that are currently in this world.
     * 
     * @return The players
     */
    @Override
    public Collection<Player> getPlayers() {
        return this.unmodifiablePlayers;
    }

    /**
     * Gets a raw list with all the players that are currently in this world.
     *
     * @return The players
     */
    public Set<LanternPlayer> getRawPlayers() {
        return this.players;
    }

    public void addPlayer(LanternPlayer player) {
        this.players.add(player);
        checkArgument(addEntity(player) == null);
    }

    public void removePlayer(LanternPlayer player) {
        this.players.remove(player);
        this.entitiesByUniqueId.remove(player.getUniqueId());
        final Vector3i lastChunkSection = player.getLastChunkSectionCoords();
        LanternChunk chunk;
        if (lastChunkSection != null && (chunk = this.chunkManager.getChunkIfLoaded(
                lastChunkSection.getX(), lastChunkSection.getZ())) != null) {
            chunk.removeEntity(player, lastChunkSection.getY());
        }
        this.entityProtocolManager.remove(player);
    }

    /**
     * Gets the chunk manager of this world.
     * 
     * @return the chunk manager.
     */
    public LanternChunkManager getChunkManager() {
        return this.chunkManager;
    }

    @Override
    public Location<World> getLocation(Vector3i position) {
        return getLocation(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public Location<World> getLocation(Vector3d position) {
        return getLocation(position.getX(), position.getY(), position.getZ());
    }

    public int getHighestBlockAt(int x, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getHighestBlockAt(x, z);
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(int x, int y, int z) {
        LanternChunk chunk = this.chunkManager.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getScheduledUpdates(x, y, z);
        }
        return ImmutableSet.of();
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(int x, int y, int z, int priority, int ticks) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeScheduledUpdate(int x, int y, int z, ScheduledBlockUpdate update) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isLoaded() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Extent getExtentView(Vector3i newMin, Vector3i newMax) {
        checkVolumeBounds(newMin);
        checkVolumeBounds(newMax);
        return new ExtentViewDownsize(this, newMin, newMax);
    }

    @Override
    public MutableBiomeVolumeWorker<World> getBiomeWorker() {
        return new LanternMutableBiomeVolumeWorker<>(this);
    }

    @Override
    public MutableBlockVolumeWorker<World> getBlockWorker(Cause cause) {
        return new LanternMutableBlockVolumeWorker<>(this, cause);
    }

    @Override
    public boolean save() throws IOException {
        this.chunkManager.save();
        // Save the scoreboard
        ScoreboardIO.write(this.directory, this.scoreboard);
        // Save the world properties
        Lantern.getServer().getWorldManager().saveWorldProperties(this.properties);
        return true; // TODO
    }

    @Override
    public Optional<AABB> getBlockSelectionBox(int x, int y, int z) {
        final LanternChunk chunk = getChunkManager().getChunkIfLoaded(x >> 4, z >> 4);
        return chunk == null ? Optional.empty() : chunk.getBlockSelectionBox(x, y, z);
    }

    @Override
    public Set<AABB> getIntersectingCollisionBoxes(Entity owner, AABB box) {
        checkNotNull(owner, "owner");
        return getIntersectingBlockCollisionBoxes(box, entity -> entity != owner);
    }

    @Override
    public Set<AABB> getIntersectingBlockCollisionBoxes(AABB box) {
        return getIntersectingBlockCollisionBoxes(box, null);
    }

    public Set<AABB> getIntersectingBlockCollisionBoxes(AABB box, @Nullable Predicate<Entity> filter) {
        checkNotNull(box, "box");
        final ImmutableSet.Builder<AABB> boxes = ImmutableSet.builder();
        int minY = box.getMin().getFloorY();
        final int maxY = box.getMax().getFloorY();
        if (minY >= LanternWorld.BLOCK_MAX.getY() || maxY < 0) {
            return boxes.build();
        }
        minY = Math.max(0, minY);
        final int maxX = box.getMax().getFloorX();
        final int minX = box.getMin().getFloorX();
        final int maxZ = box.getMax().getFloorZ();
        final int minZ = box.getMin().getFloorZ();
        final int maxChunkX = maxX >> 4;
        final int minChunkX = minX >> 4;
        final int maxChunkZ = maxZ >> 4;
        final int minChunkZ = minZ >> 4;
        final int maxChunkSection = maxY >> 4;
        final int minChunkSection = minY >> 4;
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                final LanternChunk chunk = getChunkManager().getChunkIfLoaded(chunkX, chunkZ);
                if (chunk == null) {
                    continue;
                }
                final int startX = Math.max(minX, chunkX << 4);
                final int endX = Math.min(maxX, (chunkX << 4) | 0xf);
                final int startZ = Math.max(minZ, chunkZ << 4);
                final int endZ = Math.min(maxZ, (chunkZ << 4) | 0xf);
                for (int x = startX; x <= endX; x++) {
                    for (int z = startZ; z <= endZ; z++) {
                        for (int y = minY; y <= maxY; y++) {
                            final Optional<AABB> boundingBox = chunk.getBlockSelectionBox(x, y, z);
                            if (boundingBox.isPresent() && boundingBox.get().intersects(box)) {
                                boxes.add(boundingBox.get());
                            }
                        }
                    }
                }
                if (filter != null) {
                    chunk.addIntersectingEntitiesBoxes(boxes, maxChunkSection, minChunkSection, box, filter);
                }
            }
        }
        return boxes.build();
    }

    @Override
    public Set<Entity> getIntersectingEntities(AABB box, Predicate<Entity> filter) {
        checkNotNull(box, "box");
        checkNotNull(filter, "filter");
        final ImmutableSet.Builder<Entity> entities = ImmutableSet.builder();
        final int maxX = ((int) Math.ceil(box.getMax().getX() + 2.0)) >> 4;
        final int minX = ((int) Math.floor(box.getMin().getX() - 2.0)) >> 4;
        final int maxYSection = fixEntityYSection(((int) Math.round(box.getMax().getY() + 2.0)) >> 4);
        final int minYSection = fixEntityYSection(((int) Math.round(box.getMin().getY() - 2.0)) >> 4);
        final int maxZ = ((int) Math.ceil(box.getMax().getZ() + 2.0)) >> 4;
        final int minZ = ((int) Math.floor(box.getMin().getZ() - 2.0)) >> 4;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                final LanternChunk chunk = getChunkManager().getChunkIfLoaded(x, z);
                if (chunk != null) {
                    chunk.addIntersectingEntities(entities, maxYSection, minYSection, box, filter);
                }
            }
        }
        return entities.build();
    }

    @Override
    public Set<EntityHit> getIntersectingEntities(Vector3d start, Vector3d end, Predicate<EntityHit> filter) {
        return Collections.emptySet();
    }

    @Override
    public Set<EntityHit> getIntersectingEntities(Vector3d start, Vector3d direction, double distance, Predicate<EntityHit> filter) {
        return Collections.emptySet();
    }

    @Override
    public Optional<UUID> getCreator(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getCreator(x, y, z);
    }

    @Override
    public Optional<UUID> getNotifier(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getNotifier(x, y, z);
    }

    @Override
    public void setCreator(int x, int y, int z, @Nullable UUID uuid) {
        this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).setCreator(x, y, z, uuid);
    }

    @Override
    public void setNotifier(int x, int y, int z, @Nullable UUID uuid) {
        this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).setNotifier(x, y, z, uuid);
    }

    @Override
    public ArchetypeVolume createArchetypeVolume(Vector3i min, Vector3i max, Vector3i origin) {
        return null;
    }

    private void forEachEntity(Consumer<LanternEntity> consumer) {
        final Iterator<LanternEntity> iterator = this.entitiesByUniqueId.values().iterator();
        while (iterator.hasNext()) {
            final LanternEntity entity = iterator.next();
            // Only remove the entities that are "destroyed",
            // the other ones can be resurrected after chunk loading
            if (entity.isRemoved()) {
                iterator.remove();
            } else {
                consumer.accept(entity);
            }
        }
    }

    @Override
    public Collection<Entity> getEntities() {
        final ImmutableList.Builder<Entity> entities = ImmutableList.builder();
        forEachEntity(entities::add);
        return entities.build();
    }

    @Override
    public Collection<Entity> getEntities(Predicate<Entity> filter) {
        return getEntities().stream().filter(filter).collect(GuavaCollectors.toImmutableList());
    }

    @Override
    public Entity createEntity(EntityType type, Vector3d position) {
        checkNotNull(position, "position");
        final LanternEntityType entityType = (LanternEntityType) checkNotNull(type, "type");
        //noinspection unchecked
        final LanternEntity entity = (LanternEntity) entityType.getEntityConstructor().apply(UUID.randomUUID());
        entity.setPositionAndWorld(this, position);
        return entity;
    }

    @Override
    public Entity createEntityNaturally(EntityType type, Vector3d position) throws IllegalArgumentException, IllegalStateException {
        return createEntity(type, position); // TODO: Naturally
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer, Vector3d position) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public boolean spawnEntities(Iterable<? extends Entity> entities, Cause cause) {
        boolean spawned = true;
        for (Entity entity : entities) {
            spawned &= spawnEntity(entity, cause);
        }
        return spawned;
    }

    @Override
    public Collection<TileEntity> getTileEntities() {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public Collection<TileEntity> getTileEntities(Predicate<TileEntity> filter) {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public Optional<TileEntity> getTileEntity(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getTileEntity(x, y, z);
    }

    @Override
    public boolean containsBlock(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, BLOCK_MIN, BLOCK_MAX);
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getBlock(x, y, z);
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        return getBlock(x, y, z).getType();
    }

    @Override
    public void setBiome(int x, int y, int z, BiomeType biome) {
        this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).setBiome(x, y, z, biome);
    }

    @Override
    public Vector3i getBiomeMin() {
        return BIOME_MIN;
    }

    @Override
    public Vector3i getBiomeMax() {
        return BIOME_MAX;
    }

    @Override
    public Vector3i getBiomeSize() {
        return BIOME_SIZE;
    }

    @Override
    public Vector3i getBlockMin() {
        return BLOCK_MIN;
    }

    @Override
    public Vector3i getBlockMax() {
        return BLOCK_MAX;
    }

    @Override
    public Vector3i getBlockSize() {
        return BLOCK_SIZE;
    }

    @Override
    public boolean containsBiome(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, BIOME_MIN, BIOME_MAX);
    }

    @Override
    public BiomeType getBiome(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getBiome(x, y, z);
    }

    @Override
    public boolean setBlock(int x, int y, int z, BlockState blockState, Cause cause) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).setBlock(x, y, z, blockState, cause);
    }

    @Override
    public boolean setBlock(int x, int y, int z, BlockState blockState, BlockChangeFlag flag, Cause cause) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).setBlock(x, y, z, blockState, flag, cause);
    }

    @Override
    public BlockSnapshot createSnapshot(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).createSnapshot(x, y, z);
    }

    @Override
    public boolean restoreSnapshot(int x, int y, int z, BlockSnapshot snapshot, boolean force,
            BlockChangeFlag flag, Cause cause) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4)
                .restoreSnapshot(x, y, z, snapshot, force, flag, cause);
    }

    @Override
    public boolean restoreSnapshot(BlockSnapshot snapshot, boolean force, BlockChangeFlag flag, Cause cause) {
        final Vector3i pos = checkNotNull(snapshot, "snapshot").getPosition();
        return this.chunkManager.getOrLoadChunk(pos.getX() >> 4, pos.getZ() >> 4)
                .restoreSnapshot(pos.getX(), pos.getY(), pos.getZ(), snapshot, force, flag, cause);
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Direction direction, Class<T> propertyClass) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getProperty(new Vector3i(x, y, z), direction, propertyClass);
    }

    @Override
    public Collection<Direction> getFacesWithProperty(int x, int y, int z, Class<? extends Property<?, ?>> propertyClass) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getFacesWithProperty(x, y, z, propertyClass);
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Class<T> propertyClass) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getProperty(x, y, z, propertyClass);
    }

    @Override
    public Collection<Property<?, ?>> getProperties(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getProperties(x, y, z);
    }

    @Override
    public <E> Optional<E> get(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).get(x, y, z, key);
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> get(int x, int y, int z, Class<T> manipulatorClass) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).get(x, y, z, manipulatorClass);
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(int x, int y, int z, Class<T> manipulatorClass) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getOrCreate(x, y, z, manipulatorClass);
    }

    @Override
    public <E> E getOrNull(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getOrNull(x, y, z, key);
    }

    @Override
    public <E> E getOrElse(int x, int y, int z, Key<? extends BaseValue<E>> key, E defaultValue) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getOrElse(x, y, z, key, defaultValue);
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(int x, int y, int z, Key<V> key) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getValue(x, y, z, key);
    }

    @Override
    public boolean supports(int x, int y, int z, Key<?> key) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).supports(x, y, z, key);
    }

    @Override
    public boolean supports(int x, int y, int z, BaseValue<?> value) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).supports(x, y, z, value);
    }

    @Override
    public boolean supports(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).supports(x, y, z, manipulatorClass);
    }

    @Override
    public boolean supports(int x, int y, int z, DataManipulator<?, ?> manipulator) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).supports(x, y, z, manipulator);
    }

    @Override
    public ImmutableSet<Key<?>> getKeys(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getKeys(x, y, z);
    }

    @Override
    public ImmutableSet<ImmutableValue<?>> getValues(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getValues(x, y, z);
    }

    @Override
    public <E> DataTransactionResult transform(int x, int y, int z, Key<? extends BaseValue<E>> key, Function<E, E> function) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).transform(x, y, z, key, function);
    }

    @Override
    public <E> DataTransactionResult offer(int x, int y, int z, Key<? extends BaseValue<E>> key, E value) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).offer(x, y, z, key, value);
    }

    @Override
    public <E> DataTransactionResult offer(int x, int y, int z, Key<? extends BaseValue<E>> key, E value, Cause cause) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).offer(x, y, z, key, value, cause);
    }

    @Override
    public <E> DataTransactionResult offer(int x, int y, int z, BaseValue<E> value) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).offer(x, y, z, value);
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).offer(x, y, z, manipulator);
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator, MergeFunction function) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).offer(x, y, z, manipulator, function);
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator, MergeFunction function, Cause cause) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).offer(x, y, z, manipulator, function, cause);
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, Iterable<DataManipulator<?, ?>> manipulators) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).offer(x, y, z, manipulators);
    }

    @Override
    public DataTransactionResult offer(Vector3i coords, Iterable<DataManipulator<?, ?>> values, MergeFunction function) {
        return this.chunkManager.getOrLoadChunk(coords.getX() >> 4, coords.getZ() >> 4).offer(coords, values, function);
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).remove(x, y, z, manipulatorClass);
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Key<?> key) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).remove(x, y, z, key);
    }

    @Override
    public DataTransactionResult undo(int x, int y, int z, DataTransactionResult result) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).undo(x, y, z, result);
    }

    @Override
    public DataTransactionResult copyFrom(int x, int y, int z, DataHolder from) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).copyFrom(x, y, z, from);
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, int xFrom, int yFrom, int zFrom) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(int x, int y, int z, DataHolder from, MergeFunction function) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).copyFrom(x, y, z, from, function);
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, int xFrom, int yFrom, int zFrom, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<DataManipulator<?, ?>> getManipulators(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getManipulators(x, y, z);
    }

    @Override
    public boolean validateRawData(int x, int y, int z, DataView container) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).validateRawData(x, y, z, container);
    }

    @Override
    public void setRawData(int x, int y, int z, DataView container) throws InvalidDataException {
        this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).setRawData(x, y, z, container);
    }

    @Override
    public UUID getUniqueId() {
        return this.properties.uniqueId;
    }

    @Override
    public LanternWeather getWeather() {
        if (this.weatherUniverse != null) {
            return this.weatherUniverse.getWeather();
        }
        return (LanternWeather) Weathers.CLEAR;
    }

    @Override
    public long getRemainingDuration() {
        if (this.weatherUniverse != null) {
            return this.weatherUniverse.getRemainingDuration();
        }
        // Will always be clear
        return Long.MAX_VALUE;
    }

    @Override
    public long getRunningDuration() {
        if (this.weatherUniverse != null) {
            return this.weatherUniverse.getRunningDuration();
        }
        // Will always be clear
        return Long.MAX_VALUE;
    }

    @Override
    public void setWeather(Weather weather) {
        if (this.weatherUniverse != null) {
            this.weatherUniverse.setWeather(weather);
        }
    }

    @Override
    public void setWeather(Weather weather, long duration) {
        if (this.weatherUniverse != null) {
            this.weatherUniverse.setWeather(weather, duration);
        }
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position) {
        checkNotNull(particleEffect, "particleEffect");
        checkNotNull(position, "position");
        this.spawnParticles(this.players.iterator(), particleEffect, position);
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position, int radius) {
        checkNotNull(particleEffect, "particleEffect");
        checkNotNull(position, "position");
        this.spawnParticles(this.players.stream().filter(
                player -> player.getLocation().getPosition().distanceSquared(position) < radius * radius).iterator(),
                particleEffect, position);
    }

    @Override
    public void playSound(SoundType sound, SoundCategory category, Vector3d position, double volume, double pitch, double minVolume) {
        checkNotNull(sound, "sound");
        checkNotNull(position, "position");
        checkNotNull(category, "category");
        this.broadcast(() -> ((LanternSoundType) sound).createMessage(position,
                category, (float) Math.max(minVolume, volume), (float) pitch));
    }

    private void spawnParticles(Iterator<LanternPlayer> players, ParticleEffect particleEffect, Vector3d position) {
        if (!players.hasNext()) {
            return;
        }
        final MessagePlayOutParticleEffect message = new MessagePlayOutParticleEffect(position, particleEffect);
        while (players.hasNext()) {
            players.next().getConnection().send(message);
        }
    }

    @Override
    public void sendMessage(Text message) {
        sendMessage(ChatTypes.CHAT, message);
    }

    @Override
    public MessageChannel getMessageChannel() {
        return this.messageChannel;
    }

    @Override
    public void setMessageChannel(MessageChannel channel) {
        this.messageChannel = checkNotNull(channel, "channel");
    }

    @Override
    public void sendMessage(ChatType type, Text message) {
        checkNotNull(type, "chatType");
        checkNotNull(message, "message");
        if (!this.players.isEmpty()) {
            final Map<Locale, Message> networkMessages = new HashMap<>();
            for (LanternPlayer player : this.players) {
                player.getConnection().send(networkMessages.computeIfAbsent(player.getLocale(),
                        locale -> ((LanternChatType) type).getMessageProvider().apply(message, locale)));
            }
        }
    }

    @Override
    public void sendTitle(Title title) {
        checkNotNull(title, "title");
        if (!this.players.isEmpty()) {
            final List<Message> networkMessages = LanternTitles.getMessages(title);
            this.players.forEach(player -> player.getConnection().send(networkMessages));
        }
    }

    @Override
    public void sendBookView(BookView bookView) {
        checkNotNull(bookView, "bookView");
        this.players.forEach(player -> player.sendBookView(bookView));
    }

    @Override
    public void sendBlockChange(int x, int y, int z, BlockState state) {

    }

    @Override
    public void resetBlockChange(int x, int y, int z) {

    }

    @Override
    public Context getContext() {
        return this.worldContext;
    }

    @Override
    public Location<World> getLocation(int x, int y, int z) {
        return new Location<>(this, x, y, z);
    }

    @Override
    public Location<World> getLocation(double x, double y, double z) {
        return new Location<>(this, x, y, z);
    }

    @Override
    public Difficulty getDifficulty() {
        return this.properties.getDifficulty();
    }

    @Override
    public String getName() {
        return this.properties.getWorldName();
    }

    @Override
    public Optional<Chunk> getChunk(Vector3i position) {
        return Optional.ofNullable(this.chunkManager.getChunk(position.toVector2(true)));
    }

    @Override
    public Optional<Chunk> getChunk(int x, int y, int z) {
        return Optional.ofNullable(this.chunkManager.getChunk(x, z));
    }

    @Override
    public Optional<Chunk> loadChunk(int x, int y, int z, boolean generate) {
        if (!VecHelper.inBounds(x, y, z, SPACE_MIN, SPACE_MAX)) {
            return Optional.empty();
        }
        if (generate) {
            return Optional.of(this.chunkManager.getOrCreateChunk(new Vector2i(x, z),
                    () -> Cause.source(this.game.getMinecraftPlugin()).owner(this).build(), true));
        } else {
            return Optional.ofNullable(this.chunkManager.getChunk(x, z));
        }
    }

    @Override
    public boolean unloadChunk(Chunk chunk) {
        return chunk.unloadChunk();
    }

    @Override
    public Iterable<Chunk> getLoadedChunks() {
        return this.chunkManager.getLoadedChunks();
    }

    @Override
    public Optional<Entity> getEntity(UUID uuid) {
        return Optional.ofNullable(this.entitiesByUniqueId.get(checkNotNull(uuid, "uuid")));
    }

    @Override
    public boolean spawnEntity(Entity entity, Cause cause) {
        checkNotNull(entity, "entity");
        checkArgument(!entity.isRemoved(), "The entity may not be removed.");
        checkArgument(entity.getWorld() == this, "The entity is not be located in this world.");
        checkNotNull(cause, "cause");
        final LanternEntity entity1 = addEntity((LanternEntity) entity);
        if (entity1 != null) {
            if (entity == entity1) {
                throw new IllegalArgumentException("The entity is already spawned.");
            } else {
                throw new IllegalArgumentException("There is already a entity spawned with the unique id.");
            }
        }
        final LanternEntity entity2 = (LanternEntity) entity;
        final Vector3i position = entity2.getPosition().toInt();
        final Vector3i chunkPos = new Vector3i(position.getX() >> 4, fixEntityYSection(position.getY() >> 4), position.getZ() >> 4);
        final LanternChunk chunk = (LanternChunk) loadChunk(chunkPos.getX(), 0, chunkPos.getZ(), true).get();
        chunk.addEntity(entity2, chunkPos.getY());
        return true;
    }

    public void addEntities(Iterable<Entity> entities) {
        for (Entity entity : entities) {
            addEntity((LanternEntity) entity);
        }
    }

    @Nullable
    private LanternEntity addEntity(LanternEntity entity) {
        LanternEntity entity1 = this.entitiesByUniqueId.putIfAbsent(entity.getUniqueId(), entity);
        if (entity1 != null) {
            return entity1;
        }
        final EntityProtocolType entityProtocolType = entity.getEntityProtocolType();
        if (entityProtocolType != null) {
            //noinspection unchecked
            this.entityProtocolManager.add(entity, entityProtocolType);
        }
        entity.setPositionAndWorld(this, entity.getPosition());
        return null;
    }

    private void pulseEntities() {
        // Pulse the entities
        for (LanternEntity entity : new ArrayList<>(this.entitiesByUniqueId.values())) {
            if (entity.isRemoved()) {
                final Vector3i lastChunk = entity.getLastChunkSectionCoords();
                if (lastChunk != null && entity.getRemoveState() == LanternEntity.RemoveState.DESTROYED) {
                    final LanternChunk chunk = this.chunkManager.getChunkIfLoaded(lastChunk.getX(), lastChunk.getZ());
                    if (chunk != null) {
                        chunk.removeEntity(entity, lastChunk.getY());
                    }
                }
                this.entityProtocolManager.remove(entity);
                this.entitiesByUniqueId.remove(entity.getUniqueId());
            } else {
                final Vector3i lastChunkSection = entity.getLastChunkSectionCoords();
                entity.pulse();
                final Vector3i pos = entity.getPosition().toInt();
                final Vector3i newChunk = new Vector3i(pos.getX() >> 4, fixEntityYSection(pos.getY() >> 4), pos.getZ() >> 4);
                if (lastChunkSection == null || !lastChunkSection.equals(newChunk)) {
                    LanternChunk chunk;
                    if (lastChunkSection != null && (chunk = this.chunkManager.getChunkIfLoaded(
                            lastChunkSection.getX(), lastChunkSection.getZ())) != null) {
                        chunk.removeEntity(entity, lastChunkSection.getY());
                    }
                    chunk = this.chunkManager.getOrLoadChunk(newChunk.getX(), newChunk.getZ());
                    chunk.addEntity(entity, newChunk.getY());
                    entity.setLastChunkCoords(newChunk);
                }
            }
        }
    }

    @Override
    public LanternWorldBorder getWorldBorder() {
        return this.worldBorder;
    }

    @Override
    public ChunkPreGenerate.Builder newChunkPreGenerate(Vector3d center, double diameter) {
        checkNotNull(center, "center");
        return new LanternChunkPreGenerateTask.Builder(this, center, diameter);
    }

    @Override
    public Optional<String> getGameRule(String gameRule) {
        return this.properties.getGameRule(gameRule);
    }

    @Override
    public Map<String, String> getGameRules() {
        return this.properties.getGameRules();
    }

    @Override
    public <T> Optional<Rule<T>> getRule(RuleType<T> ruleType) {
        return this.properties.getRules().getRule(ruleType);
    }

    @Override
    public <T> Rule<T> getOrCreateRule(RuleType<T> ruleType) {
        return this.properties.getRules().getOrCreateRule(ruleType);
    }

    @Override
    public Dimension getDimension() {
        return this.dimension;
    }

    @Override
    public WorldGenerator getWorldGenerator() {
        return this.chunkManager.getWorldGenerator();
    }

    @Override
    public boolean doesKeepSpawnLoaded() {
        return this.properties.doesKeepSpawnLoaded();
    }

    @Override
    public void setKeepSpawnLoaded(boolean keepLoaded) {
        this.properties.setKeepSpawnLoaded(keepLoaded);
    }

    @Override
    public WorldStorage getWorldStorage() {
        return this.chunkManager.getChunkIOService();
    }

    @Override
    public LanternWorldProperties getProperties() {
        return this.properties;
    }

    @Override
    public Path getDirectory() {
        return this.directory;
    }

    @Override
    public Location<World> getSpawnLocation() {
        return new Location<>(this, this.properties.getSpawnPosition());
    }

    @Override
    public void triggerExplosion(Explosion explosion, Cause cause) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Optional<Entity> restoreSnapshot(EntitySnapshot snapshot, Vector3d position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PortalAgent getPortalAgent() {
        return this.portalAgent;
    }

    public void pulse() {
        this.chunkManager.pulse();
        this.timeUniverse.pulse();
        if (this.weatherUniverse != null) {
            this.weatherUniverse.pulse();
        }

        // Pulse the entities
        pulseEntities();

        // Pulse the tile entities
        getLoadedChunks().forEach(chunk -> ((LanternChunk) chunk).pulse());

        // TODO: Maybe async?
        this.observedChunkManager.pulse();
        this.entityProtocolManager.updateTrackers(this.players);
    }

    public void broadcast(Supplier<Message> message) {
        this.broadcast(message, null);
    }

    public void broadcast(Supplier<Message> message, @Nullable Predicate<LanternPlayer> filter) {
        Set<LanternPlayer> players = this.players;
        if (filter != null) {
            players = players.stream().filter(filter).collect(Collectors.toSet());
        }
        if (players.isEmpty()) {
            return;
        }
        final Message message0 = message.get();
        players.forEach(player -> player.getConnection().send(message0));
    }

    @Override
    public boolean hitBlock(int x, int y, int z, Direction side, Cause cause) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean interactBlockWith(int x, int y, int z, ItemStack itemStack, Direction side, Cause cause) {
        cause = Cause.builder().from(cause).named(Parameters.USED_ITEM_STACK.getName(), itemStack).build();
        return interactBlock(x, y, z, side, cause, ctx -> ctx.set(Parameters.USED_ITEM_STACK, itemStack));
    }

    @Override
    public boolean interactBlock(int x, int y, int z, Direction side, Cause cause) {
        return interactBlock(x, y, z, side, cause, ctx -> {});
    }

    public boolean interactBlock(int x, int y, int z, Direction side, Cause cause, Consumer<BehaviorContext> consumer) {
        final LanternBlockType blockType = ((LanternBlockType) getBlockType(x, y, z));
        final BehaviorPipeline<Behavior> pipeline = blockType.getPipeline();
        final BehaviorContextImpl context = new BehaviorContextImpl(cause);
        context.set(Parameters.INTERACTION_FACE, side);
        context.set(Parameters.BLOCK_LOCATION, new Location<>(this, x, y, z));
        context.set(Parameters.BLOCK_TYPE, blockType);
        consumer.accept(context);
        // Just pass an object trough to make sure that a value is present when successful
        return context.process(pipeline.pipeline(InteractWithBlockBehavior.class),
                (ctx, behavior) -> behavior.tryInteract(pipeline, ctx));
    }

    @Override
    public boolean placeBlock(int x, int y, int z, BlockState block, Direction side, Cause cause) {
        cause = Cause.builder().from(cause).named(Parameters.USED_BLOCK_STATE.getName(), block).build();
        final LanternBlockType blockType = ((LanternBlockType) getBlockType(x, y, z));
        final BehaviorPipeline<Behavior> pipeline = blockType.getPipeline();
        final BehaviorContextImpl context = new BehaviorContextImpl(cause);
        context.set(Parameters.INTERACTION_FACE, side);
        context.set(Parameters.BLOCK_LOCATION, new Location<>(this, x, y, z));
        context.set(Parameters.BLOCK_TYPE, blockType);
        // Just pass an object trough to make sure that a value is present when successful
        return context.process(pipeline.pipeline(PlaceBlockBehavior.class),
                (ctx, behavior) -> behavior.tryPlace(pipeline, ctx));
    }

    @Override
    public boolean digBlock(int x, int y, int z, Cause cause) {
        return false;
    }

    @Override
    public boolean digBlockWith(int x, int y, int z, ItemStack itemStack, Cause cause) {
        cause = Cause.builder().from(cause).named(Parameters.USED_ITEM_STACK.getName(), itemStack).build();
        return digBlock(x, y, z, cause);
    }

    @Override
    public int getBlockDigTimeWith(int x, int y, int z, ItemStack itemStack, Cause cause) {
        // TODO Auto-generated method stub
        return 0;
    }

    public TimeUniverse getTimeUniverse() {
        return this.timeUniverse;
    }

    public EntityProtocolManager getEntityProtocolManager() {
        return this.entityProtocolManager;
    }

    public MultiWorldEventListener getEventListener() {
        return this.worldEventListener;
    }

    public void addBlockAction(Vector3i position, BlockType blockType, BlockAction blockAction) {
        addBlockAction(position.getX(), position.getY(), position.getZ(), blockType, blockAction);
    }

    public void addBlockAction(int x, int y, int z, BlockType blockType, BlockAction blockAction) {
        final LanternChunk chunk = this.chunkManager.getChunkIfLoaded(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.addBlockAction(x, y, z, blockType, blockAction);
        }
    }
}
