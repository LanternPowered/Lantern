/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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

import static com.google.common.base.Preconditions.checkArgument;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_AREA_SIZE;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_MASK;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_SECTION_MASK;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_SIZE;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.block.LanternBlockSnapshot;
import org.lanternpowered.server.block.LanternScheduledBlockUpdate;
import org.lanternpowered.server.game.registry.Registries;
import org.lanternpowered.server.util.NibbleArray;
import org.lanternpowered.server.util.VecHelper;
import org.lanternpowered.server.util.concurrent.AtomicByteArray;
import org.lanternpowered.server.util.concurrent.AtomicNibbleArray;
import org.lanternpowered.server.util.concurrent.AtomicShortArray;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.extent.AbstractExtent;
import org.lanternpowered.server.world.extent.ExtentViewDownsize;
import org.lanternpowered.server.world.extent.ExtentViewTransform;
import org.lanternpowered.server.world.extent.worker.LanternMutableBiomeAreaWorker;
import org.lanternpowered.server.world.extent.worker.LanternMutableBlockVolumeWorker;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
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
import org.spongepowered.api.data.property.block.GroundLuminanceProperty;
import org.spongepowered.api.data.property.block.SkyLuminanceProperty;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.extent.worker.MutableBiomeAreaWorker;
import org.spongepowered.api.world.extent.worker.MutableBlockVolumeWorker;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public class LanternChunk implements AbstractExtent, Chunk {

    // The size of a chunk section in the x, y and z directions
    public static final int CHUNK_SECTION_SIZE = 16;
    // The area of a chunk and a chunk section (xz plane)
    public static final int CHUNK_AREA = CHUNK_SECTION_SIZE * CHUNK_SECTION_SIZE;
    // The volume of a chunk section
    public static final int CHUNK_SECTION_VOLUME = CHUNK_AREA * CHUNK_SECTION_SIZE;
    // The amount of sections inside one chunk
    public static final int CHUNK_SECTIONS = 16;
    // The volume of a chunk
    public static final int CHUNK_VOLUME = CHUNK_SECTION_VOLUME * CHUNK_SECTIONS;
    // The height of a chunk
    public static final int CHUNK_HEIGHT = CHUNK_SECTION_SIZE * CHUNK_SECTIONS;

    public static class ChunkSection {

        // The block types array.
        public final AtomicShortArray types;

        // The light level arrays.
        public final AtomicNibbleArray lightFromSky;
        public final AtomicNibbleArray lightFromBlock;

        // The amount of non air blocks
        public volatile int nonAirCount;

        public ChunkSection() {
            this(null, (Integer) 0);
        }

        public ChunkSection(short[] types) {
            this(types, null);
        }

        public ChunkSection(short[] types, int nonAirCount) {
            this(types, (Integer) nonAirCount);
        }

        private ChunkSection(@Nullable short[] types, @Nullable Integer nonAirCount) {
            if (types != null) {
                checkArgument(types.length == CHUNK_SECTION_VOLUME, "Type array length mismatch: Got "
                        + types.length + ", but expected " + CHUNK_SECTION_VOLUME);
                this.types = new AtomicShortArray(types);
            } else {
                this.types = new AtomicShortArray(CHUNK_SECTION_VOLUME);
            }
            this.lightFromBlock = new AtomicNibbleArray(CHUNK_SECTION_VOLUME);
            this.lightFromSky = new AtomicNibbleArray(CHUNK_SECTION_VOLUME);
            if (nonAirCount != null) {
                this.nonAirCount = nonAirCount;
            } else {
                this.recountNonAir();
            }
        }

        public ChunkSection(short[] types, NibbleArray lightFromSky, NibbleArray lightFromBlock) {
            checkArgument(types.length == CHUNK_SECTION_VOLUME, "Type array length mismatch: Got "
                    + types.length + ", but expected " + CHUNK_SECTION_VOLUME);
            checkArgument(lightFromSky.length() == CHUNK_SECTION_VOLUME, "Sky light nibble array length mismatch: Got "
                    + lightFromSky.length() + ", but expected " + CHUNK_SECTION_VOLUME);
            checkArgument(lightFromSky.length() == CHUNK_SECTION_VOLUME, "Block light nibble array length mismatch: Got "
                    + lightFromBlock.length() + ", but expected " + CHUNK_SECTION_VOLUME);
            this.lightFromBlock = new AtomicNibbleArray(lightFromBlock);
            this.lightFromSky = new AtomicNibbleArray(lightFromSky);
            this.types = new AtomicShortArray(types);

            // Count the non air blocks.
            this.recountNonAir();
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
        public void recountNonAir() {
            this.nonAirCount = 0;
            for (int i = 0; i < this.types.length(); i++) {
                if (this.types.get(i) != 0) {
                    this.nonAirCount++;
                }
            }
        }
    }

    private final PriorityBlockingQueue<LanternScheduledBlockUpdate> scheduledBlockUpdateQueue =
            new PriorityBlockingQueue<>();
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

    @Nullable private AtomicByteArray heightMap;
    @Nullable private AtomicReferenceArray<ChunkSection> sections = new AtomicReferenceArray<>(CHUNK_SECTIONS);
    @Nullable private AtomicShortArray biomes;

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

    /**
     * Initializes the chunk sections.
     * 
     * @param sections the sections
     */
    public void initializeSections(ChunkSection[] sections) {
        checkArgument(sections.length == CHUNK_SECTIONS, "Sections array length mismatch: Got "
                + sections.length + ", but expected " + CHUNK_SECTIONS);
        this.sections = new AtomicReferenceArray<>(sections);
        this.loaded = true;
    }

    public void initializeEmpty() {
        if (this.sections != null || this.biomes != null) {
            throw new IllegalStateException("Chunk is already initialized!");
        }
        this.heightMap = new AtomicByteArray(CHUNK_AREA);
        this.sections = new AtomicReferenceArray<>(CHUNK_SECTIONS);
        this.biomes = new AtomicShortArray(CHUNK_AREA);
        this.loaded = true;
    }

    public ChunkSection[] getSections() {
        ChunkSection[] array = new ChunkSection[CHUNK_SECTIONS];
        for (int i = 0; i < array.length; i++) {
            array[i] = this.sections.get(i);
        }
        return array;
    }

    public void initializeBiomes(short[] biomes) {
        if (this.biomes != null) {
            throw new IllegalStateException("Biomes are already initialized!");
        }
        if (biomes.length != CHUNK_AREA) {
            throw new IllegalStateException("Biomes array not of length " + CHUNK_AREA);
        }
        this.biomes = new AtomicShortArray(biomes);
    }

    /**
     * Scan downwards to determine the new height map value.
     */
    private int lowerHeightMap(int x, int y, int z) {
        for (--y; y >= 0; --y) {
            if (this.getType(x, y, z) != 0) {
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
        byte[] heightMap = this.heightMap == null ? new byte[CHUNK_AREA] : null;

        // Determine max Y chunk section at a time
        int sy = this.sections.length() - 1;
        for (; sy >= 0; --sy) {
            if (this.sections.get(sy) != null) {
                break;
            }
        }

        int y = (sy + 1) * 16;
        for (int x = 0; x < CHUNK_SECTION_SIZE; ++x) {
            for (int z = 0; z < CHUNK_SECTION_SIZE; ++z) {
                byte value = (byte) this.lowerHeightMap(x, y, z);
                int index = z * CHUNK_SECTION_SIZE + x;
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
                section.nonAirCount--;
            }
        } else {
            if (section.types.get(index) == 0) {
                section.nonAirCount++;
            }
        }

        // Store the new type
        section.types.set(index, type);

        // Destroy empty sections
        if (section.nonAirCount <= 0) {
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

    /**
     * Gets the block light at the coordinates.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the biome value
     */
    public byte getBlockLight(int x, int y, int z) {
        ChunkSection section = this.getSectionAtHeight(y);

        if (section != null) {
            return section.lightFromBlock.get(section.index(x, y, z));
        }

        return 0;
    }

    /**
     * Gets the block light at the coordinates.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the biome value
     */
    public byte getSkyLight(int x, int y, int z) {
        ChunkSection section = this.getSectionAtHeight(y);

        if (section != null) {
            return section.lightFromSky.get(section.index(x, y, z));
        }

        return 0;
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
    public MutableBiomeAreaWorker<? extends Extent> getBiomeWorker() {
        return new LanternMutableBiomeAreaWorker<>(this);
    }

    @Override
    public MutableBlockVolumeWorker<? extends Extent> getBlockWorker() {
        return new LanternMutableBlockVolumeWorker<>(this);
    }

    public Entity getEntity(UUID uniqueId) {
        return null;
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
        this.setType(x, y, z, Registries.getBlockRegistry().getStateInternalIdAndData(block));
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
        this.setBlock(x, y, z, block);
    }

    @Override
    public boolean containsBlock(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, Vector3i.ZERO, CHUNK_MASK);
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        return Registries.getBlockRegistry().getStateByInternalIdAndData(this.getType(x, y, z)).orElse(BlockTypes.AIR.getDefaultState());
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        return this.getBlock(x, y, z).getType();
    }

    @Override
    public void setBiome(int x, int z, BiomeType biome) {
        this.setBiomeId(x, z, Registries.getBiomeRegistry().getInternalId(biome));
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
        return Registries.getBiomeRegistry().getByInternalId(this.getBiomeId(x, z)).orElse(BiomeTypes.OCEAN);
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Direction direction, Class<T> propertyClass) {
        return this.getProperty0(x, y, z, direction, propertyClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Class<T> propertyClass) {
        return this.getProperty0(x, y, z, null, propertyClass);
    }

    public <T extends Property<?, ?>> Optional<T> getProperty0(int x, int y, int z, @Nullable Direction direction, Class<T> propertyClass) {
        BlockState blockState = this.getBlock(x, y, z);
        if (propertyClass.equals(GroundLuminanceProperty.class)) {
            return (Optional<T>) Optional.of(new GroundLuminanceProperty(this.getBlockLight(x, y, z) / 15f));
        } else if (propertyClass.equals(SkyLuminanceProperty.class)) {
            return (Optional<T>) Optional.of(new SkyLuminanceProperty(this.getSkyLight(x, y, z) / 15f));
        }
        Optional<T> property = blockState.getProperty(propertyClass);
        if (!property.isPresent()) {
            Optional<TileEntity> tileEntity = this.getTileEntity(x, y, z);
            if (tileEntity.isPresent()) {
                property = tileEntity.get().getProperty(propertyClass);
            }
        }
        return property;
    }

    @Override
    public Collection<Property<?, ?>> getProperties(int x, int y, int z) {
        ImmutableList.Builder<Property<?, ?>> builder = ImmutableList.builder();
        builder.add(new GroundLuminanceProperty(this.getBlockLight(x, y, z) / 15f));
        builder.add(new SkyLuminanceProperty(this.getSkyLight(x, y, z) / 15f));
        builder.addAll(this.getBlock(x, y, z).getApplicableProperties());
        this.getTileEntity(x, y, z).ifPresent(tile -> builder.addAll(tile.getApplicableProperties()));
        return builder.build();
    }

    @Override
    public Collection<Direction> getFacesWithProperty(int x, int y, int z, Class<? extends Property<?, ?>> propertyClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> Optional<E> get(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        BlockState blockState = this.getBlock(x, y, z);
        Optional<E> value = blockState.get(key);
        if (!value.isPresent()) {
            Optional<TileEntity> tileEntity = this.getTileEntity(x, y, z);
            if (tileEntity.isPresent()) {
                value = tileEntity.get().get(key);
            }
        }
        return value;
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(int x, int y, int z, Key<V> key) {
        BlockState blockState = this.getBlock(x, y, z);
        Optional<V> value = blockState.getValue(key);
        if (!value.isPresent()) {
            Optional<TileEntity> tileEntity = this.getTileEntity(x, y, z);
            if (tileEntity.isPresent()) {
                value = tileEntity.get().getValue(key);
            }
        }
        return value;
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
    public boolean supports(int x, int y, int z, Key<?> key) {
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
        return new Location<>(this, x, y, z);
    }

    @Override
    public Location<Chunk> getLocation(double x, double y, double z) {
        return new Location<>(this, x, y, z);
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
        if (this.world.getChunkManager().load(this, Cause.of(this), generate)) {
            this.loaded = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean unloadChunk() {
        if (this.world.getChunkManager().unload(this, Cause.of(this))) {
            this.loaded = false;
            return true;
        }
        return false;
    }

    @Override
    public int getInhabittedTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getRegionalDifficultyFactor() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getRegionalDifficultyPercentage() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean hitBlock(int x, int y, int z, Direction side, Cause cause) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean interactBlock(int x, int y, int z, Direction side, Cause cause) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean interactBlockWith(int x, int y, int z, ItemStack itemStack, Direction side, Cause cause) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean digBlock(int x, int y, int z, Cause cause) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean digBlockWith(int x, int y, int z, ItemStack itemStack, Cause cause) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getBlockDigTimeWith(int x, int y, int z, ItemStack itemStack, Cause cause) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean placeBlock(int x, int y, int z, BlockState block, Direction direction, Cause cause) {
        return false;
    }
}
