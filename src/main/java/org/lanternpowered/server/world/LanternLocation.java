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
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.entity.BlockEntity;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.fluid.FluidState;
import org.spongepowered.api.fluid.FluidType;
import org.spongepowered.api.scheduler.ScheduledUpdate;
import org.spongepowered.api.scheduler.TaskPriority;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import java.lang.ref.WeakReference;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class LanternLocation implements Location {

    private final UUID worldUniqueId;
    // A weak reference to the world in case it gets unloaded
    @Nullable private WeakReference<World> world;

    // Lazily computed, either position or blockPosition is set by the constructor
    @Nullable private Vector3d position = null;
    @Nullable private Vector3i blockPosition = null;
    @Nullable private Vector3i chunkPosition = null;
    @Nullable private Vector3i biomePosition = null;
    private int hash = 0;

    /**
     * Create a new instance.
     *
     * @param world The world
     * @param position The position
     */
    public LanternLocation(World world, Vector3d position) {
        this.world = new WeakReference<>(checkNotNull(world, "world"));
        this.worldUniqueId = world.getUniqueId();
        this.position = checkNotNull(position, "position");
    }

    /**
     * Create a new instance.
     *
     * @param world The world
     * @param x The X-axis position
     * @param y The Y-axis position
     * @param z The Z-axis position
     */
    public LanternLocation(World world, double x, double y, double z) {
        this(world, new Vector3d(x, y, z));
    }

    /**
     * Create a new instance.
     *
     * @param world The world
     * @param blockPosition The position
     */
    public LanternLocation(World world, Vector3i blockPosition) {
        this.world = new WeakReference<>(checkNotNull(world, "world"));
        this.worldUniqueId = world.getUniqueId();
        this.blockPosition = checkNotNull(blockPosition, "blockPosition");
    }

    /**
     * Create a new instance.
     *
     * @param world The world
     * @param x The X-axis position
     * @param y The Y-axis position
     * @param z The Z-axis position
     */
    public LanternLocation(World world, int x, int y, int z) {
        this(world, new Vector3i(x, y, z));
    }

    /**
     * Create a new instance.
     *
     * @param worldUniqueId The unique id of the world
     * @param position The position
     */
    public LanternLocation(UUID worldUniqueId, Vector3d position) {
        this.worldUniqueId = checkNotNull(worldUniqueId, "worldUniqueId");
        this.position = checkNotNull(position, "position");
    }

    /**
     * Create a new instance.
     *
     * @param worldUniqueId The unique id of the world
     * @param x The X-axis position
     * @param y The Y-axis position
     * @param z The Z-axis position
     */
    public LanternLocation(UUID worldUniqueId, double x, double y, double z) {
        this(worldUniqueId, new Vector3d(x, y, z));
    }

    /**
     * Create a new instance.
     *
     * @param worldUniqueId The unique id of the world
     * @param blockPosition The position
     */
    public LanternLocation(UUID worldUniqueId, Vector3i blockPosition) {
        this.worldUniqueId = checkNotNull(worldUniqueId, "worldUniqueId");
        this.blockPosition = checkNotNull(blockPosition, "blockPosition");
    }

    /**
     * Create a new instance.
     *
     * @param worldUniqueId The unique id of the world
     * @param x The X-axis position
     * @param y The Y-axis position
     * @param z The Z-axis position
     */
    public LanternLocation(UUID worldUniqueId, int x, int y, int z) {
        this(worldUniqueId, new Vector3i(x, y, z));
    }

    @Override
    public World getWorld() {
        return getWorldIfAvailable().orElseThrow(() ->
                new IllegalStateException(String.format("The world %s is not available", this.worldUniqueId)));
    }

    @Override
    public Optional<World> getWorldIfAvailable() {
        final World currentWorld = this.world == null ? null : this.world.get();
        if (currentWorld != null) {
            return Optional.of(currentWorld);
        }
        final Optional<World> optWorld = Sponge.getServer().getWorldManager().getWorld(this.worldUniqueId);
        if (!optWorld.isPresent()) {
            return Optional.empty();
        }
        this.world = new WeakReference<>(optWorld.get());
        return optWorld;
    }

    @Override
    public UUID getWorldUniqueId() {
        return this.worldUniqueId;
    }

    @Override
    public boolean isAvailable() {
        return getWorldIfAvailable().isPresent();
    }

    @Override
    public boolean isValid() {
        if (isAvailable()) {
            return true;
        }
        return Sponge.getServer().getWorldManager().getProperties(this.worldUniqueId).isPresent();
    }

    @Override
    public Vector3d getPosition() {
        if (this.position == null) {
            checkState(this.blockPosition != null);
            this.position = getBlockPosition().toDouble();
        }
        return this.position;
    }

    @Override
    public Vector3i getBlockPosition() {
        if (this.blockPosition == null) {
            checkState(this.position != null);
            this.blockPosition = getPosition().toInt();
        }
        return this.blockPosition;
    }

    @Override
    public Vector3i getChunkPosition() {
        if (this.chunkPosition == null) {
            this.chunkPosition = Sponge.getServer().getChunkLayout().forceToChunk(getBlockPosition());
        }
        return this.chunkPosition;
    }

    @Override
    public Vector3i getBiomePosition() {
        if (this.biomePosition == null) {
            final Vector3i blockPosition = getBlockPosition();
            this.biomePosition = new Vector3i(blockPosition.getX(), 0, blockPosition.getZ());
        }
        return this.biomePosition;
    }

    @Override
    public double getX() {
        return getPosition().getX();
    }

    @Override
    public double getY() {
        return getPosition().getY();
    }

    @Override
    public double getZ() {
        return getPosition().getZ();
    }

    @Override
    public int getBlockX() {
        return getBlockPosition().getX();
    }

    @Override
    public int getBlockY() {
        return getBlockPosition().getY();
    }

    @Override
    public int getBlockZ() {
        return getBlockPosition().getZ();
    }

    @Override
    public boolean inWorld(World world) {
        return getWorld().equals(world);
    }

    @Override
    public boolean hasBlock() {
        return getWorld().containsBlock(getBlockPosition());
    }

    @Override
    public LocatableBlock asLocatableBlock() {
        return LocatableBlock
                .builder()
                .world(getWorld())
                .position(getBlockPosition())
                .build();
    }

    @Override
    public Location withWorld(World world) {
        checkNotNull(world, "world");
        if (world == getWorld()) {
            return this;
        }
        return new LanternLocation(world, getPosition());
    }

    @Override
    public Location withPosition(Vector3d position) {
        checkNotNull(position, "position");
        if (position == getPosition()) {
            return this;
        }
        return new LanternLocation(getWorld(), position);
    }

    @Override
    public Location withBlockPosition(Vector3i position) {
        checkNotNull(position, "position");
        if (position == getBlockPosition()) {
            return this;
        }
        return new LanternLocation(getWorld(), position);
    }

    @Override
    public Location sub(Vector3d v) {
        return sub(v.getX(), v.getY(), v.getZ());
    }

    @Override
    public Location sub(Vector3i v) {
        return sub(v.getX(), v.getY(), v.getZ());
    }

    @Override
    public Location sub(double x, double y, double z) {
        return withPosition(getPosition().sub(x, y, z));
    }

    @Override
    public Location add(Vector3d v) {
        return add(v.getX(), v.getY(), v.getZ());
    }

    @Override
    public Location add(Vector3i v) {
        return add(v.getX(), v.getY(), v.getZ());
    }

    @Override
    public Location add(double x, double y, double z) {
        return withPosition(getPosition().add(x, y, z));
    }

    @Override
    public <T> T map(BiFunction<World, Vector3d, T> mapper) {
        return mapper.apply(getWorld(), getPosition());
    }

    @Override
    public <T> T mapBlock(BiFunction<World, Vector3i, T> mapper) {
        return mapper.apply(getWorld(), getBlockPosition());
    }

    @Override
    public <T> T mapChunk(BiFunction<World, Vector3i, T> mapper) {
        return mapper.apply(getWorld(), getChunkPosition());
    }

    @Override
    public <T> T mapBiome(BiFunction<World, Vector3i, T> mapper) {
        return mapper.apply(getWorld(), getBiomePosition());
    }

    @Override
    public Location relativeTo(Direction direction) {
        return add(direction.asOffset());
    }

    @Override
    public Location relativeToBlock(Direction direction) {
        checkArgument(!direction.isSecondaryOrdinal(), "Secondary cardinal directions can't be used here");
        return add(direction.asBlockOffset());
    }

    @Override
    public BiomeType getBiome() {
        return getWorld().getBiome(getBiomePosition());
    }

    @Override
    public BlockState getBlock() {
        return getWorld().getBlock(getBlockPosition());
    }

    @Override
    public FluidState getFluid() {
        return getWorld().getFluid(getBlockPosition());
    }

    @Override
    public boolean hasBlockEntity() {
        return getWorld().getBlockEntity(getBlockPosition()).isPresent();
    }

    @Override
    public Optional<BlockEntity> getBlockEntity() {
        return getWorld().getBlockEntity(getBlockPosition());
    }

    @Override
    public boolean setBlock(BlockState state) {
        return getWorld().setBlock(getBlockPosition(), state);
    }

    @Override
    public boolean setBlock(BlockState state, BlockChangeFlag flag) {
        return getWorld().setBlock(getBlockPosition(), state, flag);
    }

    @Override
    public boolean setBlockType(BlockType type) {
        return getWorld().setBlock(getBlockPosition(), type.getDefaultState());
    }

    @Override
    public boolean setBlockType(BlockType type, BlockChangeFlag flag) {
        return getWorld().setBlock(getBlockPosition(), type.getDefaultState(), flag);
    }

    @Override
    public boolean restoreSnapshot(BlockSnapshot snapshot, boolean force, BlockChangeFlag flag) {
        return getWorld().restoreSnapshot(getBlockPosition(), snapshot, force, flag);
    }

    @Override
    public boolean removeBlock() {
        return getWorld().removeBlock(getBlockPosition());
    }

    @Override
    public Entity createEntity(EntityType type) {
        return this.getWorld().createEntity(type, getPosition());
    }

    @Override
    public boolean spawnEntity(Entity entity) {
        return getWorld().spawnEntity(entity);
    }

    @Override
    public Collection<Entity> spawnEntities(Iterable<? extends Entity> entities) {
        return getWorld().spawnEntities(entities);
    }

    @Override
    public Location asHighestLocation() {
        return withBlockPosition(getWorld().getHighestPositionAt(getBlockPosition()));
    }

    @Override
    public <T extends DataManipulator> Optional<T> get(Class<T> containerClass) {
        return Optional.empty(); // TODO
    }

    @Override
    public <T extends DataManipulator> Optional<T> getOrCreate(Class<T> containerClass) {
        return Optional.empty(); // TODO
    }

    @Override
    public boolean supports(Class<? extends DataManipulator> holderClass) {
        return false; // TODO
    }

    @Override
    public <E> DataTransactionResult offer(Key<? extends Value<E>> key, E value) {
        return getWorld().offer(getBlockPosition(), key, value);
    }

    @Override
    public DataTransactionResult offer(DataManipulator valueContainer, MergeFunction function) {
        return null; // TODO
    }

    @Override
    public DataTransactionResult remove(Class<? extends DataManipulator> containerClass) {
        return null; // TODO
    }

    @Override
    public DataTransactionResult remove(Value<?> value) {
        return getWorld().remove(getBlockPosition(), value.getKey());
    }

    @Override
    public DataTransactionResult remove(Key<?> key) {
        return getWorld().remove(getBlockPosition(), key);
    }

    @Override
    public DataTransactionResult undo(DataTransactionResult result) {
        return getWorld().undo(getBlockPosition(), result);
    }

    @Override
    public DataTransactionResult copyFrom(DataHolder that, MergeFunction function) {
        return getWorld().copyFrom(getBlockPosition(), that, function);
    }

    @Override
    public Collection<DataManipulator> getContainers() {
        return null;
    }

    @Override
    public BlockSnapshot createSnapshot() {
        return getWorld().createSnapshot(getBlockPosition());
    }

    @Override
    public Collection<ScheduledUpdate<BlockType>> getScheduledBlockUpdates() {
        return getWorld().getScheduledBlockUpdates().getScheduledAt(getBlockPosition());
    }

    @Override
    public ScheduledUpdate<BlockType> scheduleBlockUpdate(int delay, TemporalUnit temporalUnit) {
        return getWorld().getScheduledBlockUpdates().schedule(getBlockPosition(), getBlock().getType(), delay, temporalUnit);
    }

    @Override
    public ScheduledUpdate<BlockType> scheduleBlockUpdate(int delay, TemporalUnit temporalUnit, TaskPriority priority) {
        return getWorld().getScheduledBlockUpdates().schedule(getBlockPosition(), getBlock().getType(), delay, temporalUnit, priority);
    }

    @Override
    public ScheduledUpdate<BlockType> scheduleBlockUpdate(Duration delay) {
        return getWorld().getScheduledBlockUpdates().schedule(getBlockPosition(), getBlock().getType(), delay);
    }

    @Override
    public ScheduledUpdate<BlockType> scheduleBlockUpdate(Duration delay, TaskPriority priority) {
        return getWorld().getScheduledBlockUpdates().schedule(getBlockPosition(), getBlock().getType(), delay, priority);
    }

    @Override
    public Collection<ScheduledUpdate<FluidType>> getScheduledFluidUpdates() {
        return getWorld().getScheduledFluidUpdates().getScheduledAt(getBlockPosition());
    }

    @Override
    public ScheduledUpdate<FluidType> scheduleFluidUpdate(int delay, TemporalUnit temporalUnit) {
        return getWorld().getScheduledFluidUpdates().schedule(getBlockPosition(), getFluid().getType(), delay, temporalUnit);
    }

    @Override
    public ScheduledUpdate<FluidType> scheduleFluidUpdate(int delay, TemporalUnit temporalUnit, TaskPriority priority) {
        return getWorld().getScheduledFluidUpdates().schedule(getBlockPosition(), getFluid().getType(), delay, temporalUnit, priority);
    }

    @Override
    public ScheduledUpdate<FluidType> scheduleFluidUpdate(Duration delay) {
        return getWorld().getScheduledFluidUpdates().schedule(getBlockPosition(), getFluid().getType(), delay);
    }

    @Override
    public ScheduledUpdate<FluidType> scheduleFluidUpdate(Duration delay, TaskPriority priority) {
        return getWorld().getScheduledFluidUpdates().schedule(getBlockPosition(), getFluid().getType(), delay, priority);
    }

    @Override
    public <V> Optional<V> getProperty(Property<V> property) {
        return getWorld().getProperty(getBlockPosition(), property);
    }

    @Override
    public OptionalInt getIntProperty(Property<Integer> property) {
        return getWorld().getIntProperty(getBlockPosition(), property);
    }

    @Override
    public OptionalDouble getDoubleProperty(Property<Double> property) {
        return getWorld().getDoubleProperty(getBlockPosition(), property);
    }

    @Override
    public Map<Property<?>, ?> getProperties() {
        return getWorld().getProperties(getBlockPosition());
    }

    @Override
    public <V> Optional<V> getProperty(Direction direction, Property<V> property) {
        return getWorld().getProperty(getBlockPosition(), direction, property);
    }

    @Override
    public OptionalInt getIntProperty(Direction direction, Property<Integer> property) {
        return getWorld().getIntProperty(getBlockPosition(), direction, property);
    }

    @Override
    public OptionalDouble getDoubleProperty(Direction direction, Property<Double> property) {
        return getWorld().getDoubleProperty(getBlockPosition(), direction, property);
    }

    @Override
    public boolean validateRawData(DataView container) {
        return getWorld().validateRawData(getBlockPosition(), container);
    }

    @Override
    public void setRawData(DataView container) throws InvalidDataException {
        getWorld().setRawData(getBlockPosition(), container);
    }

    @Override
    public int getContentVersion() {
        // 1 - Legacy Extent generic location that stored only block types and positions
        // 2 - World based locations that stores the position
        return 2;
    }

    @Override
    public DataContainer toContainer() {
        final DataContainer container = DataContainer.createNew();
        container.set(Queries.CONTENT_VERSION, getContentVersion());
        container.set(Queries.WORLD_ID, getWorld().getUniqueId().toString());
        container.set(Queries.POSITION_X, getX());
        container.set(Queries.POSITION_Y, getY());
        container.set(Queries.POSITION_Z, getZ());
        return container;
    }

    @Override
    public <E> Optional<E> get(Key<? extends Value<E>> key) {
        return getWorld().get(getBlockPosition(), key);
    }

    @Override
    public <E, V extends Value<E>> Optional<V> getValue(Key<V> key) {
        return getWorld().getValue(getBlockPosition(), key);
    }

    @Override
    public boolean supports(Key<?> key) {
        return getWorld().supports(getBlockPosition(), key);
    }

    @Override
    public Location copy() {
        return this;
    }

    @Override
    public Set<Key<?>> getKeys() {
        return getWorld().getKeys(getBlockPosition());
    }

    @Override
    public Set<Value.Immutable<?>> getValues() {
        return getWorld().getValues(getBlockPosition());
    }

    @Override
    public String toString() {
        final String name = getWorldIfAvailable().map(World::getName).orElse(null);
        return "Location{" + getPosition() + " in " + getWorldUniqueId() + (name == null ? "" : " (" + name + ")") + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LanternLocation)) {
            return false;
        }
        final LanternLocation other = (LanternLocation) obj;
        return other.worldUniqueId.equals(this.worldUniqueId) &&
                other.getPosition().equals(getPosition());
    }

    @Override
    public int hashCode() {
        int hash = this.hash;
        if (hash == 0) {
            this.hash = hash = Objects.hash(getWorldUniqueId(), getPosition());
        }
        return hash;
    }
}
