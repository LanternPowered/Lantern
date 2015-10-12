package org.lanternpowered.server.world;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.lanternpowered.server.effect.LanternViewer;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChatMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSoundEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle;
import org.lanternpowered.server.text.title.LanternTitles;
import org.lanternpowered.server.util.VecHelper;
import org.lanternpowered.server.world.chunk.LanternChunk;
import org.lanternpowered.server.world.chunk.LanternChunkManager;
import org.lanternpowered.server.world.extent.AbstractExtent;
import org.lanternpowered.server.world.extent.ExtentViewDownsize;
import org.lanternpowered.server.world.extent.ExtentViewTransform;
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
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.service.persistence.InvalidDataException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.PlayerSimulator;
import org.spongepowered.api.world.TeleporterAgent;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.storage.WorldStorage;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.SPACE_MAX;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.SPACE_MIN;

public class LanternWorld extends AbstractExtent implements World, LanternViewer {

    public static final Vector3i BLOCK_MIN = new Vector3i(-30000000, 0, -30000000);
    public static final Vector3i BLOCK_MAX = new Vector3i(30000000, 256, 30000000).sub(1, 1, 1);
    public static final Vector3i BLOCK_SIZE = BLOCK_MAX.sub(BLOCK_MIN).add(1, 1, 1);
    public static final Vector2i BIOME_MIN = BLOCK_MIN.toVector2(true);
    public static final Vector2i BIOME_MAX = BLOCK_MAX.toVector2(true);
    public static final Vector2i BIOME_SIZE = BIOME_MAX.sub(BIOME_MIN).add(1, 1);

    // The game instance
    final LanternGame game;

    // The world border
    final LanternWorldBorder worldBorder = new LanternWorldBorder(this);

    // The weather universe
    // TODO: This can be null depending on whether the sky can use weather
    @Nullable
    final LanternWeatherUniverse weatherUniverse = new LanternWeatherUniverse(this);

    private final LanternChunkManager chunkManager = null;
    final LanternWorldProperties properties = null;

    private final String name;
    private final UUID uniqueId;

    private final TeleporterAgent teleporterAgent = null;
    private Context worldContext;

    public LanternWorld(LanternGame game, String name, UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.game = game;
        this.name = name;
    }

    /**
     * Gets the players that are currently in this world.
     * 
     * @return the players
     */
    public List<LanternPlayer> getPlayers() {
        return Lists.newArrayList();
    }

    public LanternChunkManager getChunkManager() {
        return this.chunkManager;
    }

    @Override
    public Location<World> getLocation(Vector3i position) {
        return this.getLocation(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public Location<World> getLocation(Vector3d position) {
        return this.getLocation(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(int x, int y, int z) {
        LanternChunk chunk = this.chunkManager.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getScheduledUpdates(x & 0xf, y, z & 0xf);
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

    private void checkVolumeBounds(int x, int y, int z) {
        if (!this.containsBlock(x, y, z)) {
            throw new PositionOutOfBoundsException(new Vector3i(x, y, z), BLOCK_MIN, BLOCK_MAX);
        }
    }

    @Override
    public Extent getExtentView(Vector3i newMin, Vector3i newMax) {
        this.checkVolumeBounds(newMin.getX(), newMin.getY(), newMin.getZ());
        this.checkVolumeBounds(newMax.getX(), newMax.getY(), newMax.getZ());
        return new ExtentViewDownsize(this, newMin, newMax);
    }

    @Override
    public Extent getExtentView(DiscreteTransform3 transform) {
        return new ExtentViewTransform(this, transform);
    }

    @Override
    public Extent getRelativeExtentView() {
        return this.getExtentView(DiscreteTransform3.fromTranslation(this.getBlockMin().negate()));
    }

    @Override
    public Collection<Entity> getEntities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Entity> getEntities(Predicate<Entity> filter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Entity> createEntity(EntityType type, Vector3d position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Entity> createEntity(EntityType type, Vector3i position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer, Vector3d position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<TileEntity> getTileEntities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<TileEntity> getTileEntities(Predicate<TileEntity> filter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<TileEntity> getTileEntity(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getTileEntity(x, y, z);
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState block) {
        this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).setBlock(x & 0xf, y, z & 0xf, block);
    }

    @Override
    public boolean containsBlock(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, BLOCK_MIN, BLOCK_MAX);
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getBlock(x & 0xf, y, z & 0xf);
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        return this.getBlock(x, y, z).getType();
    }

    @Override
    public void setBiome(int x, int z, BiomeType biome) {
        this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).setBiome(x & 0xf, z & 0xf, biome);
    }

    @Override
    public Vector2i getBiomeMin() {
        return BIOME_MIN;
    }

    @Override
    public Vector2i getBiomeMax() {
        return BIOME_MAX;
    }

    @Override
    public Vector2i getBiomeSize() {
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
    public boolean containsBiome(int x, int z) {
        return VecHelper.inBounds(x, z, BIOME_MIN, BIOME_MAX);
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getBiome(x & 0xf, z & 0xf);
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState block, boolean notifyNeighbors) {
        this.chunkManager.getOrCreateChunk(x >> 4, z >> 4).setBlock(x & 0xf, y, z & 0xf, block, notifyNeighbors);
    }

    @Override
    public BlockSnapshot createSnapshot(int x, int y, int z) {
        return this.chunkManager.getOrCreateChunk(x >> 4, z >> 4).createSnapshot(x & 0xf, y, z & 0xf);
    }

    @Override
    public boolean restoreSnapshot(int x, int y, int z, BlockSnapshot snapshot, boolean force, boolean notifyNeighbors) {
        return this.chunkManager.getOrCreateChunk(x >> 4, z >> 4).restoreSnapshot(x & 0xf, y, z & 0xf, snapshot, force, notifyNeighbors);
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Direction direction, Class<T> propertyClass) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getProperty(new Vector3i(x & 0xf, y, z & 0xf),
                direction, propertyClass);
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Class<T> propertyClass) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getProperty(x & 0xf, y, z & 0xf, propertyClass);
    }

    @Override
    public Collection<Property<?, ?>> getProperties(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getProperties(x & 0xf, y, z & 0xf);
    }

    @Override
    public <E> Optional<E> get(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).get(x & 0xf, y, z & 0xf, key);
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> get(int x, int y, int z, Class<T> manipulatorClass) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).get(x & 0xf, y, z & 0xf, manipulatorClass);
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(int x, int y, int z, Class<T> manipulatorClass) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getOrCreate(x & 0xf, y, z & 0xf, manipulatorClass);
    }

    @Override
    public <E> E getOrNull(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getOrNull(x & 0xf, y, z & 0xf, key);
    }

    @Override
    public <E> E getOrElse(int x, int y, int z, Key<? extends BaseValue<E>> key, E defaultValue) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getOrElse(x & 0xf, y, z & 0xf, key, defaultValue);
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(int x, int y, int z, Key<V> key) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getValue(x & 0xf, y, z & 0xf, key);
    }

    @Override
    public boolean supports(int x, int y, int z, Key<?> key) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).supports(x & 0xf, y, z & 0xf, key);
    }

    @Override
    public boolean supports(int x, int y, int z, BaseValue<?> value) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).supports(x & 0xf, y, z & 0xf, value);
    }

    @Override
    public boolean supports(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).supports(x & 0xf, y, z & 0xf, manipulatorClass);
    }

    @Override
    public boolean supports(int x, int y, int z, DataManipulator<?, ?> manipulator) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).supports(x & 0xf, y, z & 0xf, manipulator);
    }

    @Override
    public ImmutableSet<Key<?>> getKeys(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getKeys(x & 0xf, y, z & 0xf);
    }

    @Override
    public ImmutableSet<ImmutableValue<?>> getValues(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getValues(x & 0xf, y, z & 0xf);
    }

    @Override
    public <E> DataTransactionResult transform(int x, int y, int z, Key<? extends BaseValue<E>> key, Function<E, E> function) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).transform(x & 0xf, y, z & 0xf, key, function);
    }

    @Override
    public <E> DataTransactionResult offer(int x, int y, int z, Key<? extends BaseValue<E>> key, E value) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).offer(x & 0xf, y, z & 0xf, key, value);
    }

    @Override
    public <E> DataTransactionResult offer(int x, int y, int z, BaseValue<E> value) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).offer(x & 0xf, y, z & 0xf, value);
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).offer(x & 0xf, y, z & 0xf, manipulator);
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator, MergeFunction function) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).offer(x & 0xf, y, z & 0xf, manipulator, function);
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, Iterable<DataManipulator<?, ?>> manipulators) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).offer(x & 0xf, y, z & 0xf, manipulators);
    }

    @Override
    public DataTransactionResult offer(Vector3i coords, Iterable<DataManipulator<?, ?>> values, MergeFunction function) {
        int x = coords.getX();
        int y = coords.getY();
        int z = coords.getZ();
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).offer(new Vector3i(x & 0xf, y, z & 0xf), values, function);
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).remove(x & 0xf, y, z & 0xf, manipulatorClass);
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Key<?> key) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).remove(x & 0xf, y, z & 0xf, key);
    }

    @Override
    public DataTransactionResult undo(int x, int y, int z, DataTransactionResult result) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).undo(x & 0xf, y, z & 0xf, result);
    }

    @Override
    public DataTransactionResult copyFrom(int x, int y, int z, DataHolder from) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).copyFrom(x & 0xf, y, z & 0xf, from);
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, int xFrom, int yFrom, int zFrom) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(int x, int y, int z, DataHolder from, MergeFunction function) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).copyFrom(x & 0xf, y, z & 0xf, from, function);
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, int xFrom, int yFrom, int zFrom, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<DataManipulator<?, ?>> getManipulators(int x, int y, int z) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).getManipulators(x & 0xf, y, z & 0xf);
    }

    @Override
    public boolean validateRawData(int x, int y, int z, DataView container) {
        return this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).validateRawData(x & 0xf, y, z & 0xf, container);
    }

    @Override
    public void setRawData(int x, int y, int z, DataView container) throws InvalidDataException {
        this.chunkManager.getOrLoadChunk(x >> 4, z >> 4).setRawData(x & 0xf, y, z & 0xf, container);
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public Weather getWeather() {
        if (this.weatherUniverse != null) {
            return this.weatherUniverse.getWeather();
        }
        return Weathers.CLEAR;
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
    public void forecast(Weather weather) {
        if (this.weatherUniverse != null) {
            this.weatherUniverse.forecast(weather);
        }
    }

    @Override
    public void forecast(Weather weather, long duration) {
        if (this.weatherUniverse != null) {
            this.weatherUniverse.forecast(weather, duration);
        }
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position) {
        checkNotNull(particleEffect, "particleEffect");
        checkNotNull(position, "position");
        this.spawnParticles(this.getPlayers().iterator(), particleEffect, position);
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position, int radius) {
        checkNotNull(particleEffect, "particleEffect");
        checkNotNull(position, "position");
        this.spawnParticles(this.getPlayers().stream().filter(
                player -> player.getLocation().getPosition().distanceSquared(position) < radius * radius).iterator(),
                particleEffect, position);
    }

    private void spawnParticles(Iterator<LanternPlayer> players, ParticleEffect particleEffect, Vector3d position) {
        if (!players.hasNext()) {
            return;
        }
        MessagePlayOutParticleEffect message = new MessagePlayOutParticleEffect(position, particleEffect);
        while (players.hasNext()) {
            players.next().getConnection().send(message);
        }
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume, double pitch, double minVolume) {
        List<LanternPlayer> players = this.getPlayers();
        if (!players.isEmpty()) {
            MessagePlayOutSoundEffect message = new MessagePlayOutSoundEffect(sound.getName(), position,
                    (float) Math.max(minVolume, volume), (float) pitch);
            for (LanternPlayer player : players) {
                player.getConnection().send(message);
            }
        }
    }

    @Override
    public void sendMessage(ChatType type, Iterable<Text> messages) {
        checkNotNull(type, "chatType");
        checkNotNull(messages, "messages");
        List<LanternPlayer> players = this.getPlayers();
        if (!players.isEmpty()) {
            List<Message> networkMessages = Lists.newArrayList();
            messages.forEach(message -> networkMessages.add(new MessagePlayOutChatMessage(message, type)));
            players.forEach(player -> player.getConnection().sendAll(networkMessages));
        }
    }

    @Override
    public void sendTitle(Title title) {
        checkNotNull(title, "title");
        List<LanternPlayer> players = this.getPlayers();
        if (!players.isEmpty()) {
            List<Message> networkMessages = LanternTitles.getCachedMessages(title);
            players.forEach(player -> player.getConnection().sendAll(networkMessages));
        }
    }

    @Override
    public void resetTitle() {
        List<LanternPlayer> players = this.getPlayers();
        if (!players.isEmpty()) {
            Message message = new MessagePlayOutTitle.Reset();
            players.forEach(player -> player.getConnection().send(message));
        }
    }

    @Override
    public void clearTitle() {
        List<LanternPlayer> players = this.getPlayers();
        if (!players.isEmpty()) {
            Message message = new MessagePlayOutTitle.Clear();
            players.forEach(player -> player.getConnection().send(message));
        }
    }

    @Override
    public Context getContext() {
        if (this.worldContext == null) {
            this.worldContext = new Context(Context.WORLD_KEY, this.getName());
        }
        return this.worldContext;
    }

    @Override
    public Location<World> getLocation(int x, int y, int z) {
        return new Location<World>(this, x, y, z);
    }

    @Override
    public Location<World> getLocation(double x, double y, double z) {
        return new Location<World>(this, x, y, z);
    }

    @Override
    public Difficulty getDifficulty() {
        return this.properties.getDifficulty();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Optional<Chunk> getChunk(Vector3i position) {
        return Optional.<Chunk>ofNullable(this.chunkManager.getChunk(position));
    }

    @Override
    public Optional<Chunk> getChunk(int x, int y, int z) {
        return this.getChunk(new Vector3i(x, y, z));
    }

    @Override
    public Optional<Chunk> loadChunk(Vector3i position, boolean generate) {
        return this.loadChunk(position.getX(), position.getY(), position.getZ(), generate);
    }

    @Override
    public Optional<Chunk> loadChunk(int x, int y, int z, boolean generate) {
        if (!VecHelper.inBounds(x, y, z, SPACE_MIN, SPACE_MAX)) {
            return Optional.empty();
        }
        Chunk chunk;
        if (generate) {
            chunk = this.chunkManager.getOrLoadChunk(new Vector2i(x, z), generate);
        } else {
            chunk = this.chunkManager.getOrLoadChunkIfPresent(new Vector2i(x, z));
        }
        return Optional.of(chunk);
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LanternWorldBorder getWorldBorder() {
        return this.worldBorder;
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
    public Dimension getDimension() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WorldGenerator getWorldGenerator() {
        return this.chunkManager.getWorldGenerator();
    }

    @Override
    public void setWorldGenerator(WorldGenerator generator) {
        this.chunkManager.setWorldGenerator(generator);
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Scoreboard getScoreboard() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public WorldCreationSettings getCreationSettings() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LanternWorldProperties getProperties() {
        return this.properties;
    }

    @Override
    public Location<World> getSpawnLocation() {
        return new Location<World>(this, this.properties.getSpawnPosition());
    }

    @Override
    public void triggerExplosion(Explosion explosion) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Optional<Entity> restoreSnapshot(EntitySnapshot snapshot, Vector3d position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean spawnEntity(Entity entity, Cause cause) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TeleporterAgent getTeleporterAgent() {
        return this.teleporterAgent;
    }

    @Override
    public PlayerSimulator getPlayerSimulator() {
        // TODO Auto-generated method stub
        return null;
    }

    public void pulse() {
        this.chunkManager.pulse();
        if (++this.properties.time > 24000) {
            this.properties.time %= 24000;
        }
        this.properties.age++;
        this.weatherUniverse.pulse();
    }
}
