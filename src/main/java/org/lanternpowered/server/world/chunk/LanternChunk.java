package org.lanternpowered.server.world.chunk;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.lanternpowered.server.block.LanternBlockSnapshot;
import org.lanternpowered.server.block.LanternBlocks;
import org.lanternpowered.server.block.LanternScheduledBlockUpdate;
import org.lanternpowered.server.util.NibbleArray;
import org.lanternpowered.server.util.VecHelper;
import org.lanternpowered.server.util.concurrent.AtomicByteArray;
import org.lanternpowered.server.util.concurrent.AtomicNibbleArray;
import org.lanternpowered.server.util.concurrent.AtomicShortArray;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.biome.LanternBiomes;
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
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.persistence.InvalidDataException;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.Extent;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_AREA_SIZE;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_SIZE;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_MASK;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_SECTIONS;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_SECTION_SIZE;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_SECTION_MASK;

public class LanternChunk extends AbstractExtent implements Chunk {

    /**
     * Converts the vector2i into a vector3i with x -> x and y -> z.
     * 
     * @param vector2 the vector2
     * @return the vector3
     */
    public static Vector3i fromVector2(Vector2i vector2) {
        return new Vector3i(vector2.getX(), 0, vector2.getY());
    }

    /**
     * Converts the vector3i into a vector2i with x -> x and z -> y.
     * 
     * @param vector3 the vector3
     * @return the vector2
     */
    public static Vector2i toVector2(Vector3i vector3) {
        return vector3.toVector2(true);
    }

    public static class ChunkSection {

        // The block types array.
        public final AtomicShortArray types;

        // The light level arrays.
        public final AtomicNibbleArray lightFromSky;
        public final AtomicNibbleArray lightFromBlock;

        // The amount of non air blocks
        public volatile int count;

        public ChunkSection() {
            int length = CHUNK_SECTION_SIZE.lengthSquared();

            this.lightFromBlock = new AtomicNibbleArray(length);
            this.lightFromSky = new AtomicNibbleArray(length);
            this.types = new AtomicShortArray(length);
        }

        public ChunkSection(short[] types) {
            this.lightFromBlock = new AtomicNibbleArray(types.length);
            this.lightFromSky = new AtomicNibbleArray(types.length);
            this.types = new AtomicShortArray(types);

            // Count the non air blocks.
            this.recount();
        }

        public ChunkSection(short[] types, NibbleArray lightFromSky, NibbleArray lightFromBlock) {
            this.lightFromBlock = new AtomicNibbleArray(lightFromBlock);
            this.lightFromSky = new AtomicNibbleArray(lightFromSky);
            this.types = new AtomicShortArray(types);

            // Count the non air blocks.
            this.recount();
        }

        /**
         * Calculate the index into internal arrays for the given coordinates.
         */
        public int index(int x, int y, int z) {
            if (!VecHelper.inBounds(x, y, z, Vector3i.ZERO, CHUNK_SECTION_MASK)) {
                throw new PositionOutOfBoundsException(new Vector3i(x, y, z), Vector3i.ZERO, CHUNK_SECTION_MASK);
            }
            return (y << 8) | (z << 4) | x;
        }

        /**
         * Recounts the amount of non air blocks.
         */
        public void recount() {
            this.count = 0;
            for (int i = 0; i < this.types.length(); i++) {
                if (this.types.get(i) != 0) {
                    this.count++;
                }
            }
        }

    }

    private final PriorityBlockingQueue<LanternScheduledBlockUpdate> scheduledBlockUpdateQueue =
            new PriorityBlockingQueue<LanternScheduledBlockUpdate>();
    private final AtomicInteger scheduledBlockUpdateCounter = new AtomicInteger();

    private final Vector3i min;
    private final Vector3i max;

    private final Vector2i areaMin;
    private final Vector2i areaMax;

    private final Vector3i pos;
    private final LanternWorld world;

    // Not sure why this is needed
    private UUID uniqueId;

    private final int x;
    private final int z;

    private volatile boolean loaded;
    private volatile boolean populated;

    private AtomicByteArray heightMap;
    private AtomicReferenceArray<ChunkSection> sections = new AtomicReferenceArray<ChunkSection>(CHUNK_SECTIONS);
    private AtomicShortArray biomes;

    public LanternChunk(LanternWorld world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;

        this.pos = new Vector3i(x, 0, z);
        this.min = LanternChunkLayout.INSTANCE.toWorld(this.pos).get();
        this.max = this.min.add(CHUNK_MASK);
        this.areaMin = this.min.toVector2(true);
        this.areaMax = this.max.toVector2(true);
    }

    public void initializeEmpty() {
        if (this.sections != null || this.biomes != null) {
            throw new IllegalStateException("Chunk is already initialized!");
        }
        this.heightMap = new AtomicByteArray(CHUNK_AREA_SIZE.lengthSquared());
        this.sections = new AtomicReferenceArray<ChunkSection>(CHUNK_SECTIONS);
        this.biomes = new AtomicShortArray(CHUNK_AREA_SIZE.lengthSquared());
        this.loaded = true;
    }

    public ChunkSection[] getSections() {
        ChunkSection[] array = new ChunkSection[CHUNK_SECTIONS];
        for (int i = 0; i < array.length; i++) {
            array[i] = this.sections.get(i);
        }
        return array;
    }

    public void initializeSections(ChunkSection[] sections) {
        if (this.sections != null) {
            throw new IllegalStateException("Sections are already initialized!");
        }
        if (sections.length != CHUNK_SECTIONS) {
            throw new IllegalStateException("Sections array not of length " + CHUNK_SECTIONS);
        }
        this.sections = new AtomicReferenceArray<ChunkSection>(sections);
        this.loaded = true;
    }

    public void initializeBiomes(short[] biomes) {
        if (this.biomes != null) {
            throw new IllegalStateException("Biomes are already initialized!");
        }
        if (biomes.length != CHUNK_AREA_SIZE.lengthSquared()) {
            throw new IllegalStateException("Biomes array not of length " + CHUNK_AREA_SIZE.lengthSquared());
        }
        this.biomes = new AtomicShortArray(biomes);
    }

    /**
     * Scan downwards to determine the new height map value.
     */
    private int lowerHeightMap(int x, int y, int z) {
        for (--y; y >= 0; --y) {
            if (getType(x, z, y) != 0) {
                break;
            }
        }
        return y + 1;
    }

    public int[] getHeightMap() {
        int[] heightMap = new int[this.heightMap.length()];
        for (int i = 0; i < heightMap.length; i++) {
            heightMap[i] = this.heightMap.get(i);
        }
        return heightMap;
    }

    public void setHeightMap(int[] heightMap) {
        if (this.heightMap == null) {
            byte[] array = new byte[heightMap.length];
            for (int i = 0; i < array.length; i++) {
                array[i] = (byte) heightMap[i];
            }
            this.heightMap = new AtomicByteArray(array);
        } else {
            for (int i = 0; i < heightMap.length; i++) {
                this.heightMap.set(i, (byte) heightMap[i]);
            }
        }
    }

    /**
     * Automatically fill the height map after chunks have been initialized.
     */
    public void automaticHeightMap() {
        byte[] heightMap = this.heightMap == null ? new byte[CHUNK_AREA_SIZE.lengthSquared()] : null;

        // Determine max Y chunk section at a time
        int sy = this.sections.length() - 1;
        for (; sy >= 0; --sy) {
            if (this.sections.get(sy) != null) {
                break;
            }
        }

        int wx = CHUNK_AREA_SIZE.getX();
        int wz = CHUNK_AREA_SIZE.getY();
        int y = (sy + 1) * 16;

        for (int x = 0; x < wx; ++x) {
            for (int z = 0; z < wz; ++z) {
                byte value = (byte) this.lowerHeightMap(x, y, z);
                int index = z * wx + x;
                if (heightMap != null) {
                    heightMap[index] = value;
                } else {
                    this.heightMap.set(index, value);
                }
            }
        }

        if (heightMap != null) {
            this.heightMap = new AtomicByteArray(heightMap);
        }
    }

    public void setPopulated(boolean populated) {
        this.populated = populated;
    }

    /**
     * Gets the x coordinate of the chunk.
     * 
     * @return the x coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the z coordinate of the chunk.
     * 
     * @return the z coordinate
     */
    public int getZ() {
        return this.z;
    }

    /**
     * Gets a array with all the biomes.
     * 
     * @return the biomes
     */
    public short[] getBiomes() {
        return this.biomes.getArray();
    }

    /**
     * Gets the biome at the coordinates.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the biome value
     */
    public short getBiomeId(int x, int z) {
        this.checkAreaBounds(x, z);
        return this.biomes.get(z << 4 | x & 0xf);
    }

    /**
     * Sets the biome at the coordinates.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @param biome the biome value
     */
    public void setBiomeId(int x, int z, short biome) {
        this.checkAreaBounds(x, z);
        this.biomes.set(z << 4 | x, biome);
    }

    /**
     * Gets the chunk section at the block height.
     * 
     * @param y the y coordinate
     * @return the chunk section
     */
    public ChunkSection getSectionAtHeight(int y) {
        if (this.sections == null) {
            return null;
        }
        y >>= 4;
        if (y < 0 || y > this.sections.length()) {
            return null;
        }
        return this.sections.get(y);
    }

    /**
     * Gets the type of the block at the coordinates.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the block type
     */
    public short getType(int x, int y, int z) {
        this.checkVolumeBounds(x, y, z);
        ChunkSection section = this.getSectionAtHeight(y);
        if (section != null) {
            return section.types.get(section.index(x, y & 0xf, z));
        }
        return 0;
    }

    /**
     * Sets the type of the block at the coordinates.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param type the block type
     */
    public void setType(int x, int y, int z, short type) {
        this.checkVolumeBounds(x, y, z);

        // Air doesn't have metadata values
        if (type >> 4 == 0 && type != 0) {
            type = 0;
        }

        ChunkSection section = this.getSectionAtHeight(y);
        if (section == null) {
            if (type == 0) {
                return;
            } else {
                int y0 = y >> 4;
                if (y0 < 0 || y0 >= this.sections.length()) {
                    return;
                }
                while (section == null) {
                    if (!this.sections.compareAndSet(y0, null, section = new ChunkSection())) {
                        section = this.sections.get(y0);
                    }
                }
            }
        }

        int index = section.index(x, y & 0xf, z);
        if (type == 0) {
            if (section.types.get(index) != 0) {
                section.count--;
            }
        } else {
            if (section.types.get(index) == 0) {
                section.count++;
            }
        }

        // Store the new type
        section.types.set(index, type);

        // Destroy empty sections
        if (section.count <= 0) {
            this.sections.set(y >> 4, null);
        }
    }

    /**
     * Sets the id of the block at the coordinates.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param id the block id
     */
    public void setId(int x, int y, int z, int id) {
        this.setType(x, y, z, (short) ((id << 4) & 0xffff));
    }

    /**
     * Sets the metadata of the block at the coordinates.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param metadata the block metadata
     */
    public void setMetadata(int x, int y, int z, int metadata) {
        ChunkSection section = this.getSectionAtHeight(y);

        if (section != null) {
            int index = section.index(x, y, z);
            short value = section.types.get(index);
            int id = section.types.get(index) >> 4;

            // Only non air blocks can have metadata
            if (id != 0) {
                section.types.compareAndSet(index, value, (short) (id << 4 | metadata & 0xf));
            }
        }
    }

    /**
     * Gets the id of the block at the coordinates.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the block id
     */
    public int getId(int x, int y, int z) {
        return this.getType(x, y, z) >> 4;
    }

    /**
     * Gets the metadata of the block at the coordinates.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the block metadata
     */
    public int getMetadata(int x, int y, int z) {
        return this.getType(x, y, z) & 0xf;
    }

    @Override
    public Location<Chunk> getLocation(Vector3i position) {
        return this.getLocation(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public Location<Chunk> getLocation(Vector3d position) {
        return this.getLocation(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public BlockSnapshot createSnapshot(int x, int y, int z) {
        return new LanternBlockSnapshot(new Location<World>(this.world, this.x << 4 | x, y, this.z << 4 | z),
                this.getBlock(x, y, z));
    }

    @Override
    public boolean restoreSnapshot(int x, int y, int z, BlockSnapshot snapshot, boolean force, boolean notifyNeighbors) {
        // TODO Auto-generated method stub
        return false;
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
    public void interactBlock(int x, int y, int z, Direction side) {
        this.interactBlockWith(x, y, z, null, side);
    }

    @Override
    public void interactBlockWith(int x, int y, int z, ItemStack itemStack, Direction side) {
        this.interactBlockWith(x, y, z, itemStack, side);
    }

    public void interactBlockWith0(int x, int y, int z, ItemStack itemStack, Direction side) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean digBlock(int x, int y, int z) {
        return this.digBlockWith0(x, y, z, null);
    }

    @Override
    public boolean digBlockWith(int x, int y, int z, ItemStack itemStack) {
        return this.digBlockWith0(x, y, z, itemStack);
    }

    public boolean digBlockWith0(int x, int y, int z, ItemStack itemStack) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getBlockDigTimeWith(int x, int y, int z, ItemStack itemStack) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isBlockFacePowered(int x, int y, int z, Direction direction) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(int x, int y, int z, Direction direction) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Collection<Direction> getPoweredBlockFaces(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Direction> getIndirectlyPoweredBlockFaces(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isBlockFlammable(int x, int y, int z, Direction faceDirection) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(int x, int y, int z) {
        ImmutableSet.Builder<ScheduledBlockUpdate> builder = ImmutableSet.builder();
        Vector3i position = LanternChunkLayout.INSTANCE.toWorld(x, y, z).get();
        for (ScheduledBlockUpdate update : this.scheduledBlockUpdateQueue) {
            if (update.getLocation().getBlockPosition().equals(position)) {
                builder.add(update);
            }
        }
        return builder.build();
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(int x, int y, int z, int priority, int ticks) {
        int entryId = this.scheduledBlockUpdateCounter.getAndIncrement();
        Location<World> location = new Location<World>(this.world, LanternChunkLayout.INSTANCE.toWorld(x, y, z).get());
        LanternScheduledBlockUpdate update = new LanternScheduledBlockUpdate(entryId, location, ticks, priority);
        this.scheduledBlockUpdateQueue.add(update);
        return update;
    }

    @Override
    public void removeScheduledUpdate(int x, int y, int z, ScheduledBlockUpdate update) {
        this.scheduledBlockUpdateQueue.remove(update);
    }

    public void processScheduledUpdates() {
        LanternScheduledBlockUpdate update;
        while ((update = this.scheduledBlockUpdateQueue.peek()) != null && update.getTicks() <= 0) {
            this.scheduledBlockUpdateQueue.poll();
            // TODO: Update
        }
    }

    @Override
    public boolean isLoaded() {
        return this.loaded;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState block) {
        this.setType(x, y, z, LanternBlocks.getStateId(block));
    }

    private void checkAreaBounds(int x, int z) {
        if (!this.containsBiome(x, z)) {
            throw new PositionOutOfBoundsException(new Vector2i(x, z), this.areaMin, this.areaMax);
        }
    }

    private void checkVolumeBounds(int x, int y, int z) {
        if (!this.containsBlock(x, y, z)) {
            throw new PositionOutOfBoundsException(new Vector3i(x, y, z), this.min, this.max);
        }
    }

    @Override
    public Vector3i getBlockMin() {
        return this.min;
    }

    @Override
    public Vector3i getBlockMax() {
        return this.max;
    }

    @Override
    public Vector3i getBlockSize() {
        return CHUNK_SIZE;
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState block, boolean notifyNeighbors) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean containsBlock(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, Vector3i.ZERO, CHUNK_MASK);
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        return LanternBlocks.getStateById(this.getType(x, y, z));
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        return this.getBlock(x, y, z).getType();
    }

    @Override
    public void setBiome(int x, int z, BiomeType biome) {
        this.setBiomeId(x, z, LanternBiomes.getId(biome));
    }

    @Override
    public Vector2i getBiomeMin() {
        return this.areaMin;
    }

    @Override
    public Vector2i getBiomeMax() {
        return this.areaMax;
    }

    @Override
    public Vector2i getBiomeSize() {
        return CHUNK_AREA_SIZE;
    }

    @Override
    public boolean containsBiome(int x, int z) {
        return VecHelper.inBounds(x, z, this.areaMin, this.areaMax);
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        return LanternBiomes.getById(this.getBiomeId(x, z));
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Class<T> propertyClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Property<?, ?>> getProperties(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> Optional<E> get(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> get(int x, int y, int z, Class<T> manipulatorClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(int x, int y, int z, Class<T> manipulatorClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> E getOrNull(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> E getOrElse(int x, int y, int z, Key<? extends BaseValue<E>> key, E defaultValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(int x, int y, int z, Key<V> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supports(int x, int y, int z, Key<?> key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(int x, int y, int z, BaseValue<?> value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(int x, int y, int z, DataManipulator<?, ?> manipulator) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ImmutableSet<Key<?>> getKeys(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ImmutableSet<ImmutableValue<?>> getValues(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> DataTransactionResult transform(int x, int y, int z, Key<? extends BaseValue<E>> key, Function<E, E> function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> DataTransactionResult offer(int x, int y, int z, Key<? extends BaseValue<E>> key, E value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> DataTransactionResult offer(int x, int y, int z, BaseValue<E> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, Iterable<DataManipulator<?, ?>> manipulators) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(Vector3i blockPosition, Iterable<DataManipulator<?, ?>> values, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Key<?> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult undo(int x, int y, int z, DataTransactionResult result) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, DataHolder from) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, int xFrom, int yFrom, int zFrom) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, DataHolder from, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, int xFrom, int yFrom, int zFrom, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<DataManipulator<?, ?>> getManipulators(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean validateRawData(int x, int y, int z, DataView container) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setRawData(int x, int y, int z, DataView container) throws InvalidDataException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public UUID getUniqueId() {
        if (this.uniqueId == null) {
            this.uniqueId = UUID.nameUUIDFromBytes(this.toChunkString().getBytes(StandardCharsets.UTF_8));
        }
        return this.uniqueId;
    }

    protected String toChunkString() {
        return "\"Chunk\":{\"World\":\"" + this.world.getName() + "\",\"X\":\"" + this.x + "\",\"Z\":\"" + this.z + "\"}";
    }

    @Override
    public Location<Chunk> getLocation(int x, int y, int z) {
        return new Location<Chunk>(this, x, y, z);
    }

    @Override
    public Location<Chunk> getLocation(double x, double y, double z) {
        return new Location<Chunk>(this, x, y, z);
    }

    @Override
    public Vector3i getPosition() {
        return this.pos;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public boolean isPopulated() {
        return this.populated;
    }

    @Override
    public boolean loadChunk(boolean generate) {
        if (this.world.getChunkManager().load(this, generate)) {
            this.loaded = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean unloadChunk() {
        if (this.world.getChunkManager().unload(this)) {
            this.loaded = false;
            return true;
        }
        return false;
    }

}
