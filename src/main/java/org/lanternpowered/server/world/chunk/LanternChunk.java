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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_AREA_SIZE;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_MASK;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_SECTION_MASK;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_SIZE;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gnu.trove.map.TShortShortMap;
import gnu.trove.map.hash.TShortShortHashMap;
import org.lanternpowered.server.block.LanternBlockSnapshot;
import org.lanternpowered.server.block.LanternScheduledBlockUpdate;
import org.lanternpowered.server.data.property.AbstractDirectionRelativePropertyHolder;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.data.property.LanternPropertyRegistry;
import org.lanternpowered.server.game.registry.Registries;
import org.lanternpowered.server.util.NibbleArray;
import org.lanternpowered.server.util.VecHelper;
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
import org.spongepowered.api.data.property.PropertyStore;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.extent.worker.MutableBiomeAreaWorker;
import org.spongepowered.api.world.extent.worker.MutableBlockVolumeWorker;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
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
    // A bit mask that can be used to get ALL the chunk sections
    public static final int ALL_SECTIONS_BIT_MASK = (1 << CHUNK_SECTIONS) - 1;

    public static class ChunkSectionColumn {

        // The locks we will use to lock each section
        private final StampedLock[] locks = new StampedLock[CHUNK_SECTIONS];

        // All the chunk sections in a column
        private final ChunkSection[] sections;

        public ChunkSectionColumn() {
            this(new ChunkSection[CHUNK_SECTIONS]);
        }

        public ChunkSectionColumn(ChunkSection[] sections) {
            this.sections = sections;
            for (int i = 0; i < this.locks.length; i++) {
                this.locks[i] = new StampedLock();
            }
        }

        /**
         * Sets the chunk section at the index.
         *
         * @param index the index
         * @param section the section
         */
        public void set(int index, ChunkSection section) {
            final StampedLock lock = this.locks[index];
            long stamp = lock.writeLock();
            try {
                this.sections[index] = section;
            } finally {
                lock.unlockWrite(stamp);
            }
        }

        public void work(int index, Consumer<ChunkSection> consumer, boolean write) {
            this.work(index, consumer, write, false);
        }

        /**
         * Performs work on the section.
         *
         * @param index the index of the section
         * @param consumer the consumer that will be executed
         * @param write whether you will perform write functions
         * @param forceReadLock whether you want to use the lock without trying without first,
         *                      this should be used when you are almost sure that the normal
         *                      read will fail anyway
         */
        public void work(int index, Consumer<ChunkSection> consumer, boolean write, boolean forceReadLock) {
            final StampedLock lock = this.locks[index];
            if (write) {
                long stamp = lock.writeLock();
                try {
                    consumer.accept(this.sections[index]);
                } finally {
                    lock.unlockWrite(stamp);
                }
            } else {
                long stamp;
                if (!forceReadLock) {
                    stamp = lock.tryOptimisticRead();
                    consumer.accept(this.sections[index]);
                    if (lock.validate(stamp)) {
                        return;
                    }
                }
                stamp = lock.readLock();
                try {
                    consumer.accept(this.sections[index]);
                } finally {
                    lock.unlock(stamp);
                }
            }
        }

        public <T> T work(int index, Function<ChunkSection, T> function, boolean write) {
            return this.work(index, function, write, false);
        }

        public <T> T work(int index, Function<ChunkSection, T> function, boolean write, boolean forceReadLock) {
            final StampedLock lock = this.locks[index];
            if (write) {
                long stamp = lock.writeLock();
                try {
                    return function.apply(this.sections[index]);
                } finally {
                    lock.unlockWrite(stamp);
                }
            } else {
                long stamp;
                if (!forceReadLock) {
                    stamp = lock.tryOptimisticRead();
                    T result = function.apply(this.sections[index]);
                    if (lock.validate(stamp)) {
                        return result;
                    }
                }
                stamp = lock.readLock();
                try {
                    return function.apply(this.sections[index]);
                } finally {
                    lock.unlock(stamp);
                }
            }
        }

        /**
         * Allows us to perform some work on the section, the function may return
         * a null, this can happen if the section was empty (all air), and may return
         * a new section to be set at the index.
         *
         * <p>I know, weird naming, wasn't able to come up with something better.</p>
         *
         * @param index the index
         * @param function the section function
         */
        public void workOnSection(int index, Function<ChunkSection, ChunkSection> function) {
            final StampedLock lock = this.locks[index];
            long stamp = lock.writeLock();
            try {
                this.sections[index] = function.apply(this.sections[index]);
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }

    public static class ChunkSection {

        /**
         * The block types array.
         */
        public final short[] types;

        /**
         * The amount of blocks per block type/state in
         * this chunk section.
         */
        public final TShortShortMap typesCountMap = new TShortShortHashMap();

        /**
         * The light level arrays.
         */
        public final NibbleArray lightFromSky;
        public final NibbleArray lightFromBlock;

        /**
         * The amount of non air blocks in this chunk section.
         */
        public int nonAirCount;

        public ChunkSection() {
            this(null, null);
        }

        public ChunkSection(short[] types) {
            this(checkNotNull(types, "types"), null);
        }

        private ChunkSection(@Nullable short[] types, @Nullable Void unused) {
            if (types != null) {
                checkArgument(types.length == CHUNK_SECTION_VOLUME, "Type array length mismatch: Got "
                        + types.length + ", but expected " + CHUNK_SECTION_VOLUME);
                this.types = types;
                this.recountTypes();
            } else {
                this.types = new short[CHUNK_SECTION_VOLUME];
            }
            this.lightFromBlock = new NibbleArray(CHUNK_SECTION_VOLUME);
            this.lightFromSky = new NibbleArray(CHUNK_SECTION_VOLUME);
        }

        public ChunkSection(short[] types, NibbleArray lightFromSky, NibbleArray lightFromBlock) {
            checkArgument(types.length == CHUNK_SECTION_VOLUME, "Type array length mismatch: Got "
                    + types.length + ", but expected " + CHUNK_SECTION_VOLUME);
            checkArgument(lightFromSky.length() == CHUNK_SECTION_VOLUME, "Sky light nibble array length mismatch: Got "
                    + lightFromSky.length() + ", but expected " + CHUNK_SECTION_VOLUME);
            checkArgument(lightFromSky.length() == CHUNK_SECTION_VOLUME, "Block light nibble array length mismatch: Got "
                    + lightFromBlock.length() + ", but expected " + CHUNK_SECTION_VOLUME);
            this.lightFromBlock = lightFromBlock;
            this.lightFromSky = lightFromSky;
            this.types = types;

            // Count the non air blocks.
            this.recountTypes();
        }

        /**
         * Calculate the index into internal arrays for the given coordinates.
         *
         * @param x the x coordinate
         * @param y the y coordinate
         * @param z the z coordinate
         * @return the index in the array
         */
        public static int index(int x, int y, int z) {
            if (!VecHelper.inBounds(x, y, z, Vector3i.ZERO, CHUNK_SECTION_MASK)) {
                throw new PositionOutOfBoundsException(new Vector3i(x, y, z), Vector3i.ZERO, CHUNK_SECTION_MASK);
            }
            return (y << 8) | (z << 4) | x;
        }

        /**
         * Recounts the amount of non air blocks.
         */
        public void recountTypes() {
            this.nonAirCount = 0;
            this.typesCountMap.clear();
            for (short type : this.types) {
                if (type != 0) {
                    this.nonAirCount++;
                    this.typesCountMap.adjustOrPutValue(type, (short) 1, (short) 1);
                }
            }
        }

        public ChunkSectionSnapshot asSnapshot(boolean skylight) {
            final TShortShortHashMap typeCounts = new TShortShortHashMap(this.typesCountMap);
            int count = this.types.length - this.nonAirCount;
            if (count > 0) {
                typeCounts.put((short) 0, (short) count);
            }
            return new ChunkSectionSnapshot(this.types.clone(), typeCounts,
                    this.lightFromBlock.getPackedArray(), skylight ? this.lightFromSky.getPackedArray() : null);
        }
    }

    public static class ChunkSectionSnapshot {

        // The block types array.
        public final short[] types;
        // The types count map.
        public final TShortShortMap typesCountMap;

        // The light level arrays.
        @Nullable public final byte[] lightFromSky;
        public final byte[] lightFromBlock;

        public ChunkSectionSnapshot(short[] types, TShortShortMap typesCountMap, byte[] lightFromBlock, @Nullable byte[] lightFromSky) {
            this.lightFromBlock = lightFromBlock;
            this.typesCountMap = typesCountMap;
            this.lightFromSky = lightFromSky;
            this.types = types;
        }
    }

    private final PriorityBlockingQueue<LanternScheduledBlockUpdate> scheduledBlockUpdateQueue =
            new PriorityBlockingQueue<>();
    private final AtomicInteger scheduledBlockUpdateCounter = new AtomicInteger();

    // The chunk sections column
    private ChunkSectionColumn chunkSections;

    // The height map of the chunk
    // This is lazily updated, meaning that it won't
    // updated every time a block changes (to avoid
    // looping through all the chunks for the lowest block)
    private byte[] heightMap;

    // The height map update flags
    // Where 0 means no change, and 1 means loop down
    private final BitSet heightMapUpdateFlags = new BitSet(CHUNK_AREA);

    // The lock for the height map and height map update flags
    private final StampedLock heightMapLock = new StampedLock();

    // The biomes array
    private short[] biomes;

    // The lock for the biomes array
    private final StampedLock biomesLock = new StampedLock();

    private final Vector3i min;
    private final Vector3i max;

    private final Vector2i areaMin;
    private final Vector2i areaMax;

    private final Vector3i pos;
    final Vector2i chunkPos;

    private final LanternWorld world;

    // Not sure why this is needed
    private final UUID uniqueId;

    private final int x;
    private final int z;

    // The lock that will be locking the chunk while it's getting loaded/saved
    final ReentrantLock lock = new ReentrantLock();
    // This condition is present while loading the chunk, a thread attempting to
    // load this chunk while it's loading will have to wait for the loading condition to complete.
    final Condition lockCondition = this.lock.newCondition();

    // Whether the chunk loading successful was
    boolean loadingSuccess;

    // Whether the chunk unloading successful was
    boolean unloadingSuccess;

    // Whether this chunk is finished loading
    volatile boolean loaded;

    // Whether this is populated by the world generator
    volatile boolean populated;

    // Whether this chunk is currently being populated
    volatile boolean populating;

    // The state of the lock
    volatile LockState lockState = LockState.NONE;

    // Whether the light in this chunk is populated
    private boolean lightPopulated;

    /**
     * The states that the chunk lock can have.
     */
    public enum LockState {
        /**
         * When the chunk is locked by loading.
         */
        LOADING,
        /**
         * When the chunk is locked by unloading.
         */
        UNLOADING,
        /**
         * When the chunk is locked by saving.
         */
        SAVING,
        /**
         * The chunk isn't locked.
         */
        NONE,
    }

    public LanternChunk(LanternWorld world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;

        final UUID worldUUID = this.world.getUniqueId();
        this.uniqueId = new UUID(worldUUID.getMostSignificantBits() ^ (x * 2 + 1),
                worldUUID.getLeastSignificantBits() ^ (z * 2 + 1));

        this.pos = new Vector3i(x, 0, z);
        this.chunkPos = this.pos.toVector2(true);
        this.min = LanternChunkLayout.INSTANCE.toWorld(this.pos).get();
        this.max = this.min.add(CHUNK_MASK);
        this.areaMin = this.min.toVector2(true);
        this.areaMax = this.max.toVector2(true);
    }

    /**
     * Initializes a empty chunk.
     * (Only used for initializing the chunk.)
     */
    public void initializeEmpty() {
        if (this.chunkSections != null || this.biomes != null) {
            throw new IllegalStateException("Chunk is already initialized!");
        }
        this.heightMap = new byte[CHUNK_AREA];
        this.chunkSections = new ChunkSectionColumn();
        this.biomes = new short[CHUNK_AREA];
        this.loaded = true;
    }

    /**
     * Initializes the sections of the chunk.
     * (Only used for initializing the chunk.)
     * 
     * @param sections the sections
     */
    public void initializeSections(ChunkSection[] sections) {
        checkArgument(sections.length == CHUNK_SECTIONS, "Sections array length mismatch: Got "
                + sections.length + ", but expected " + CHUNK_SECTIONS);
        this.chunkSections = new ChunkSectionColumn(sections);
        this.loaded = true;
    }

    /**
     * Initializes the biomes array of the chunk.
     * (Only used for initializing the chunk.)
     *
     * @param biomes the biomes
     */
    public void initializeBiomes(short[] biomes) {
        checkArgument(biomes.length == CHUNK_AREA, "Biomes array length mismatch: Got "
                + biomes.length + ", but expected " + CHUNK_AREA);
        this.biomes = biomes;
    }

    public void initializeHeightMap(@Nullable int[] heightMap) {
        if (heightMap != null) {
            checkArgument(heightMap.length == CHUNK_AREA, "Height map array length mismatch: Got "
                    + heightMap.length + ", but expected " + CHUNK_AREA);
            final byte[] heightMap0 = new byte[CHUNK_AREA];
            for (int i = 0; i < CHUNK_AREA; i++) {
                final int height = heightMap[i];
                heightMap0[i] = (byte) (height < 0 ? 0 : height > 255 ? 255 : height);
            }
            this.heightMap = heightMap0;
        } else {
            this.heightMap = new byte[CHUNK_AREA];
            this.heightMapUpdateFlags.set(0, this.heightMapUpdateFlags.size(), true);
            this.automaticHeightMap();
        }
    }

    public void initializeLight() {
        if (this.lightPopulated) { // Fast fail
            return;
        }
        for (int y = 0; y < CHUNK_SECTIONS; y++) {
            ChunkSection section = this.chunkSections.sections[y];
            if (section != null) {
                // Just fill the light array for now
                this.chunkSections.sections[y].lightFromSky.fill((byte) 15);
            }
        }
        this.lightPopulated = true;
    }

    public void setLightPopulated(boolean lightPopulated) {
        this.lightPopulated = lightPopulated;
    }

    public boolean isLightPopulated() {
        return this.lightPopulated;
    }

    public ChunkSectionSnapshot[] getSectionSnapshots(boolean skylight) {
        return this.getSectionSnapshots(skylight, ALL_SECTIONS_BIT_MASK);
    }

    public ChunkSectionSnapshot[] getSectionSnapshots(boolean skylight, int sectionBitMask) {
        ChunkSectionSnapshot[] array = new ChunkSectionSnapshot[CHUNK_SECTIONS];
        for (int i = 0; i < array.length; i++) {
            if ((sectionBitMask & (1 << i)) == 0) {
                continue;
            }
            final int index = i;
            this.chunkSections.work(index, section -> {
                if (section != null) {
                    array[index] = section.asSnapshot(skylight);
                }
            }, false, true);
        }
        return array;
    }

    public int[] getHeightMap() {
        final int[] heightMap0 = new int[this.heightMap.length];
        for (int i = 0; i < heightMap0.length; i++) {
            heightMap0[i] = this.heightMap[i];
        }
        return heightMap0;
    }

    /**
     * Gets the highest non air block (y coordinate) at the
     * x and z coordinates.
     *
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the y coordinate
     */
    public int getHighestBlockAt(int x, int z) {
        this.checkAreaBounds(x, z);
        if (!this.loaded) {
            return 0;
        }
        int index = (z & 0xf) << 4 | x & 0xf;
        long stamp = this.heightMapLock.tryOptimisticRead();
        try {
            boolean lower = this.heightMapUpdateFlags.get(index);
            int height = this.heightMap[index] & 0xff;
            if (!this.heightMapLock.validate(stamp)) {
                stamp = this.heightMapLock.readLock();
                lower = this.heightMapUpdateFlags.get(index);
            }
            // We have to update the height map for the coordinates
            if (lower) {
                if (this.heightMapLock.tryConvertToWriteLock(stamp) == 0L) {
                    // We couldn't convert the lock, so create one anyway
                    this.heightMapLock.unlock(stamp);
                    stamp = this.heightMapLock.writeLock();
                    height = this.heightMap[index] & 0xff;
                    // We were to late to acquire the lock, something else modified the index first
                    if (!this.heightMapUpdateFlags.get(index)) {
                        return height;
                    }
                }
                int sections = height >> 4;
                // 0: The height we are looping through
                int[] values0 = { 0 };
                // 0: Finished
                boolean[] values1 = { false };
                // Loop trough all the chunk sections
                for (int i = sections; i >= 0; i--) {
                    final int j = i;
                    // We do this section by section to avoid
                    // having to lock the section too many times
                    this.chunkSections.work(i, section -> {
                        if (section == null) {
                            values0[0] -= CHUNK_SECTION_SIZE;
                        } else {
                            int y = CHUNK_SECTION_SIZE;
                            // Loop down in the section until we may find a
                            // non empty block
                            while (--y >= 0) {
                                if (section.types[(y << 8) | index] != 0) {
                                    values0[0] = j << 4 | y;
                                    values1[0] = true;
                                    break;
                                }
                            }
                        }
                    }, false);
                    if (values1[0]) {
                        break;
                    }
                }
                this.heightMap[index] = (byte) height;
                this.heightMapUpdateFlags.clear(index);
                height = values0[0];
            } else {
                height = this.heightMap[index] & 0xff;
            }
            return height;
        } finally {
            this.heightMapLock.unlock(stamp);
        }
    }

    /**
     * Automatically fill the height map after chunks have been initialized.
     */
    private void automaticHeightMap() {
        // The height map flags before we updated it through this method
        final BitSet heightMapFlags;

        // The height map before we updated it through this method
        final byte[] oldHeightMap;

        long stamp = this.heightMapLock.readLock();
        try {
            heightMapFlags = (BitSet) this.heightMapUpdateFlags.clone();
            oldHeightMap = this.heightMap.clone();
        } finally {
            this.heightMapLock.unlockRead(stamp);
        }

        // The height map before we updated it through this method
        final byte[] heightMap = oldHeightMap.clone();

        // Which coordinates are finished
        boolean[] finished = new boolean[CHUNK_AREA];

        // 0: The amount of finished searches
        int[] values0 = { 0 };

        // 0: Whether we found the first non-null chunk
        // 1: Finished
        boolean[] values1 = { false, false };

        // Loop trough all the chunk sections
        for (int i = CHUNK_SECTIONS - 1; i >= 0; i--) {
            // We do this section by section to avoid
            // having to lock the section too many times
            this.chunkSections.work(i, section -> {
                if (section == null) {
                    return;
                }
                finish:
                for (int index = 0; index < CHUNK_AREA; index++) {
                    if (!finished[index] && heightMapFlags.get(index)) {
                        int y = CHUNK_SECTION_SIZE;
                        // Loop down in the section until we may find a
                        // non empty block
                        while (--y >= 0) {
                            if (section.types[(y << 8) | index] != 0) {
                                finished[index] = true;
                                heightMap[index] = (byte) y;
                                if (++values0[0] >= CHUNK_AREA) {
                                    values1[0] = true;
                                    break finish;
                                }
                                break;
                            }
                        }
                    }
                }
            }, false, true);
            if (values1[1]) {
                break;
            }
        }

        stamp = this.heightMapLock.writeLock();
        try {
            for (int index = 0; index < CHUNK_AREA; index++) {
                if (heightMapFlags.get(index) && this.heightMap[index] == oldHeightMap[index] &&
                        this.heightMapUpdateFlags.get(index)) {
                    this.heightMap[index] = heightMap[index];
                    this.heightMapUpdateFlags.clear(index);
                }
            }
        } finally {
            this.heightMapLock.unlockWrite(stamp);
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
     * Gets the coordinates of the chunk.
     *
     * @return the coordinates
     */
    public Vector2i getCoords() {
        return this.chunkPos;
    }

    /**
     * Gets a copy of the biomes array.
     *
     * @return the biomes
     */
    public short[] getBiomes() {
        long stamp = this.biomesLock.tryOptimisticRead();
        short[] biomes = this.biomes.clone();
        if (!this.biomesLock.validate(stamp)) {
            stamp = this.biomesLock.readLock();
            try {
                biomes = this.biomes.clone();
            } finally {
                this.biomesLock.unlockRead(stamp);
            }
        }
        return biomes;
    }

    /**
     * Sets the biomes array.
     *
     * @param biomes the biomes
     */
    public void setBiomes(short[] biomes) {
        checkArgument(biomes.length == CHUNK_AREA, "Biomes array length mismatch: Got "
                + biomes.length + ", but expected " + CHUNK_AREA);
        biomes = biomes.clone();
        long stamp = this.biomesLock.writeLock();
        try {
            this.biomes = biomes;
        } finally {
            this.biomesLock.unlockWrite(stamp);
        }
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
        if (!this.loaded) {
            return 0;
        }
        int index = (z & 0xf) << 4 | x & 0xf;
        long stamp = this.biomesLock.tryOptimisticRead();
        short biome = this.biomes[index];
        if (!this.biomesLock.validate(stamp)) {
            stamp = this.biomesLock.readLock();
            try {
                biome = this.biomes[index];
            } finally {
                this.biomesLock.unlockRead(stamp);
            }
        }
        return biome;
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
        if (!this.loaded) {
            return;
        }
        int index = (z & 0xf) << 4 | x & 0xf;
        long stamp = this.biomesLock.writeLock();
        try {
            this.biomes[index] = biome;
        } finally {
            this.biomesLock.unlockWrite(stamp);
        }
    }

    public short getType(Vector3i coordinates) {
        return this.getType(coordinates.getX(), coordinates.getY(), coordinates.getZ());
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
        if (!this.loaded) {
            return 0;
        }
        return this.chunkSections.work(y >> 4, section -> {
            if (section != null) {
                return section.types[ChunkSection.index(x & 0xf, y & 0xf, z & 0xf)];
            }
            return (short) 0;
        }, false);
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
        if (!this.loaded) {
            return;
        }

        final short type1;
        // Air doesn't have metadata values
        if (type >> 4 == 0 && type != 0) {
            type1 = 0;
        } else {
            type1 = type;
        }

        x &= 0xf;
        z &= 0xf;

        int x1 = x;
        int z1 = z;
        this.chunkSections.workOnSection(y >> 4, section -> {
            if (section == null) {
                // The section is already filled with air,
                // so we can fail fast
                if (type1 == 0) {
                    return section;
                }
                // Create a new section
                section = new ChunkSection();
            }
            int index = ChunkSection.index(x1, y & 0xf, z1);
            short oldType = section.types[index];
            if (oldType != type1) {
                if (oldType != 0 && section.typesCountMap.adjustOrPutValue(oldType, (short) -1, (short) 0) <= 0) {
                    section.typesCountMap.remove(oldType);
                }
                if (type1 != 0) {
                    section.typesCountMap.adjustOrPutValue(type1, (short) 1, (short) 1);
                    if (oldType == 0) {
                        section.nonAirCount++;
                    }
                } else if (oldType != 0) {
                    section.nonAirCount--;
                }
            }
            // The section is empty, destroy it
            if (section.nonAirCount <= 0) {
                return null;
            }
            section.types[index] = type1;
            return section;
        });

        int index = z << 4 | x;
        long stamp = this.heightMapLock.writeLock();
        try {
            // TODO: Check first and then use the write lock?
            if (type != 0 && (this.heightMap[index] & 0xff) < y) {
                this.heightMap[index] = (byte) y;
                this.heightMapUpdateFlags.clear(index);
            } else if (type == 0 && (this.heightMap[index] & 0xff) == y) {
                this.heightMapUpdateFlags.set(index);
            }
        } finally {
            this.heightMapLock.unlock(stamp);
        }
    }

    /**
     * Gets the block light at the coordinates.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the block light value
     */
    public byte getBlockLight(int x, int y, int z) {
        if (!this.loaded) {
            return 0;
        }
        return this.chunkSections.work(y >> 4, section -> section == null ? 0 :
                section.lightFromBlock.get(ChunkSection.index(x & 0xf, y & 0xf, z & 0xf)), false);
    }

    /**
     * Gets the block light at the coordinates.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the sky light value
     */
    public byte getSkyLight(int x, int y, int z) {
        if (!this.loaded) {
            return 15;
        }
        return this.chunkSections.work(y >> 4, section -> section == null ? 15 :
                section.lightFromSky.get(ChunkSection.index(x & 0xf, y & 0xf, z & 0xf)), false);
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
        this.checkVolumeBounds(x, y, z);
        return new LanternBlockSnapshot(new Location<>(this.world, x, y, z), this.getBlock(x, y, z));
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
        this.checkVolumeBounds(x, y, z);
        if (!this.loaded) {
            return Collections.emptyList();
        }
        final Vector3i position = new Vector3i(x, y, z);
        return this.scheduledBlockUpdateQueue.stream()
                .filter(update -> update.getLocation().getBlockPosition().equals(position))
                .collect(GuavaCollectors.toImmutableSet());
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(int x, int y, int z, int priority, int ticks) {
        this.checkVolumeBounds(x, y, z);
        int entryId = this.scheduledBlockUpdateCounter.getAndIncrement();
        Location<World> location = new Location<>(this.world, new Vector3i(x, y, z));
        LanternScheduledBlockUpdate update = new LanternScheduledBlockUpdate(entryId, location, ticks, priority);
        this.scheduledBlockUpdateQueue.add(update);
        return update;
    }

    @Override
    public void removeScheduledUpdate(int x, int y, int z, ScheduledBlockUpdate update) {
        this.checkVolumeBounds(x, y, z);
        this.scheduledBlockUpdateQueue.remove(update);
    }

    public void processScheduledUpdates() {
        // The update entry
        LanternScheduledBlockUpdate update;
        while ((update = this.scheduledBlockUpdateQueue.peek()) != null && update.getTicks() <= 0) {
            // Remove the entry from the queue
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
    public MutableBiomeAreaWorker<? extends Chunk> getBiomeWorker() {
        return new LanternMutableBiomeAreaWorker<>(this);
    }

    @Override
    public MutableBlockVolumeWorker<? extends Chunk> getBlockWorker() {
        return new LanternMutableBlockVolumeWorker<>(this);
    }

    @Override
    public Optional<UUID> getCreator(int x, int y, int z) {
        return Optional.empty();
    }

    @Override
    public Optional<UUID> getNotifier(int x, int y, int z) {
        return Optional.empty();
    }

    @Override
    public void setCreator(int x, int y, int z, @Nullable UUID uuid) {

    }

    @Override
    public void setNotifier(int x, int y, int z, @Nullable UUID uuid) {

    }

    public Optional<Entity> getEntity(UUID uniqueId) {
        return Optional.empty();
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
        // TODO: Events
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState blockState, boolean notifyNeighbors, Cause cause) {
        this.setBlock(x, y, z, blockState);
        // TODO: Events
    }

    @Override
    public boolean containsBlock(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, this.min, this.max);
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

    private <T extends Property<?, ?>> Optional<T> getProperty0(int x, int y, int z, @Nullable Direction direction, Class<T> propertyClass) {
        if (!this.loaded) {
            return Optional.empty();
        }
        Location<World> location = new Location<>(this.world, this.x << 4 | x, y, this.z << 4 | z);
        Optional<T> property = Optional.empty();
        Optional<PropertyStore<T>> store = LanternPropertyRegistry.getInstance().getStore(propertyClass);
        if (store.isPresent()) {
            if (direction != null) {
                property = AbstractDirectionRelativePropertyHolder.getPropertyFor(location, direction, propertyClass);
            } else {
                property = AbstractPropertyHolder.getPropertyFor(location, propertyClass);
            }
        }
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
        if (!this.loaded) {
            return Collections.emptyList();
        }
        Location<World> location = new Location<>(this.world, this.x << 4 | x, y, this.z << 4 | z);
        ImmutableList.Builder<Property<?, ?>> builder = ImmutableList.builder();
        builder.addAll(LanternPropertyRegistry.getInstance().getPropertiesFor(location));
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
        if (!this.loaded) {
            return Optional.empty();
        }
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
        if (!this.loaded) {
            return Optional.empty();
        }
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
        return this.uniqueId;
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
        if (this.world.getChunkManager().load(this, () -> Cause.source(this.world).named("chunk", this).build(), generate)) {
            this.loaded = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean unloadChunk() {
        if (this.world.getChunkManager().unload(this, () -> Cause.source(this.world).named("chunk", this).build())) {
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
