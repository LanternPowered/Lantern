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
package org.lanternpowered.server.world.chunk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_BIOME_VOLUME;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_MASK;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_SIZE;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ShortMap;
import it.unimi.dsi.fastutil.shorts.Short2ShortOpenHashMap;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.block.LanternBlockSnapshot;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.LanternScheduledBlockUpdate;
import org.lanternpowered.server.block.TileEntityProvider;
import org.lanternpowered.server.block.action.BlockAction;
import org.lanternpowered.server.block.provider.CachedSimpleObjectProvider;
import org.lanternpowered.server.block.provider.ConstantObjectProvider;
import org.lanternpowered.server.block.provider.ObjectProvider;
import org.lanternpowered.server.block.provider.SimpleObjectProvider;
import org.lanternpowered.server.block.tile.ITileEntityRefreshBehavior;
import org.lanternpowered.server.block.tile.LanternTileEntity;
import org.lanternpowered.server.block.tile.LanternTileEntityArchetype;
import org.lanternpowered.server.data.property.AbstractDirectionRelativePropertyHolder;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.LanternEntityType;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.world.biome.BiomeRegistryModule;
import org.lanternpowered.server.util.VecHelper;
import org.lanternpowered.server.util.collect.array.NibbleArray;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.TrackerIdAllocator;
import org.lanternpowered.server.world.extent.AbstractExtent;
import org.lanternpowered.server.world.extent.ExtentViewDownsize;
import org.lanternpowered.server.world.extent.worker.LanternMutableBiomeVolumeWorker;
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
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.ArchetypeVolume;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.extent.worker.MutableBiomeVolumeWorker;
import org.spongepowered.api.world.extent.worker.MutableBlockVolumeWorker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class LanternChunk implements AbstractExtent, Chunk {

    public static long key(int cx, int cz) {
        return ((long) cx & 0x3ffffff) << 38 | ((long) cz & 0x3ffffff);
    }

    // The size of a chunk section in the x, y and z directions
    public static final int CHUNK_SECTION_SIZE = 16;
    // The volume of a chunk and a chunk section (xz plane)
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

    public static final class TrackerData {

        private int notifierId;
        private int creatorId;

        TrackerData() {
            this(-1, -1);
        }

        public TrackerData(int notifierId, int creatorId) {
            this.notifierId = notifierId;
            this.creatorId = creatorId;
        }

        public int getNotifierId() {
            return this.notifierId;
        }

        public int getCreatorId() {
            return this.creatorId;
        }
    }

    public static class ChunkSection {

        /**
         * The block types array.
         */
        final short[] types;

        /**
         * The amount of blocks per block type/state in
         * this chunk section.
         */
        final Short2ShortMap typesCountMap = new Short2ShortOpenHashMap();

        /**
         * The light level arrays.
         */
        final NibbleArray lightFromSky;
        final NibbleArray lightFromBlock;

        final Short2ObjectMap<LanternTileEntity> tileEntities;

        /**
         * The amount of non air blocks in this chunk section.
         */
        int nonAirCount;

        ChunkSection() {
            this(null);
        }

        ChunkSection(@Nullable short[] types) {
            if (types != null) {
                checkArgument(types.length == CHUNK_SECTION_VOLUME, "Type array length mismatch: Got "
                        + types.length + ", but expected " + CHUNK_SECTION_VOLUME);
                this.types = new short[CHUNK_SECTION_VOLUME];
                System.arraycopy(types, 0, this.types, 0, CHUNK_SECTION_VOLUME);
                recountTypes();
            } else {
                this.types = new short[CHUNK_SECTION_VOLUME];
            }
            this.tileEntities = new Short2ObjectOpenHashMap<>();
            this.lightFromBlock = new NibbleArray(CHUNK_SECTION_VOLUME);
            this.lightFromSky = new NibbleArray(CHUNK_SECTION_VOLUME);
        }

        public ChunkSection(short[] types, NibbleArray lightFromSky, NibbleArray lightFromBlock,
                Short2ObjectMap<LanternTileEntity> tileEntities) {
            checkArgument(types.length == CHUNK_SECTION_VOLUME, "Type array length mismatch: Got "
                    + types.length + ", but expected " + CHUNK_SECTION_VOLUME);
            checkArgument(lightFromSky.length() == CHUNK_SECTION_VOLUME, "Sky light nibble array length mismatch: Got "
                    + lightFromSky.length() + ", but expected " + CHUNK_SECTION_VOLUME);
            checkArgument(lightFromSky.length() == CHUNK_SECTION_VOLUME, "Block light nibble array length mismatch: Got "
                    + lightFromBlock.length() + ", but expected " + CHUNK_SECTION_VOLUME);
            this.lightFromBlock = lightFromBlock;
            this.lightFromSky = lightFromSky;
            this.tileEntities = tileEntities;
            this.types = types;

            // Count the non air blocks.
            recountTypes();
        }

        public static int index(Vector3i position) {
            return index(position.getX(), position.getY(), position.getZ());
        }

        /**
         * Calculate the index into internal arrays for the given coordinates.
         *
         * @param x The x coordinate
         * @param y The y coordinate
         * @param z The z coordinate
         * @return The index in the array
         */
        public static int index(int x, int y, int z) {
            return (y << 8) | (z << 4) | x;
        }

        /**
         * Recounts the amount of non air blocks.
         */
        private void recountTypes() {
            this.nonAirCount = 0;
            this.typesCountMap.clear();
            for (short type : this.types) {
                if (type != 0) {
                    this.nonAirCount++;
                    this.typesCountMap.put(type, (short) (this.typesCountMap.get(type) + 1));
                }
            }
        }

        private ChunkSectionSnapshot asSnapshot(boolean skylight) {
            final Short2ShortMap typeCounts = new Short2ShortOpenHashMap(this.typesCountMap);
            final int count = this.types.length - this.nonAirCount;
            if (count > 0) {
                typeCounts.put((short) 0, (short) count);
            }
            return new ChunkSectionSnapshot(this.types.clone(), typeCounts, new Short2ObjectOpenHashMap<>(this.tileEntities),
                    this.lightFromBlock.getPackedArray(), skylight ? this.lightFromSky.getPackedArray() : null);
        }
    }

    public static class ChunkSectionSnapshot {

        // The block types array.
        public final short[] types;
        // The types count map.
        public final Short2ShortMap typesCountMap;
        // The tile entities
        public final Short2ObjectMap<LanternTileEntity> tileEntities;

        // The light level arrays.
        @Nullable public final byte[] lightFromSky;
        public final byte[] lightFromBlock;

        private ChunkSectionSnapshot(short[] types, Short2ShortMap typesCountMap, Short2ObjectMap<LanternTileEntity> tileEntities,
                byte[] lightFromBlock, @Nullable byte[] lightFromSky) {
            this.tileEntities = tileEntities;
            this.lightFromBlock = lightFromBlock;
            this.typesCountMap = typesCountMap;
            this.lightFromSky = lightFromSky;
            this.types = types;
        }
    }

    private final PriorityBlockingQueue<LanternScheduledBlockUpdate> scheduledBlockUpdateQueue =
            new PriorityBlockingQueue<>();
    private final AtomicInteger scheduledBlockUpdateCounter = new AtomicInteger();

    private final ConcurrentObjectArray<Short2ObjectMap<TrackerData>> trackerData;

    // The chunk sections column
    private ConcurrentObjectArray<ChunkSection> chunkSections;

    private volatile long inhabitedTime;

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

    private final Vector3i biomeMin;
    private final Vector3i biomeMax;

    private final Vector2i areaMin;
    private final Vector2i areaMax;

    private final Vector3i pos;
    final Vector2i chunkPos;

    private final LanternWorld world;

    // Not sure why this is needed
    private final UUID uniqueId;

    private final int x;
    private final int z;

    private final long key;

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

    private boolean dirtyBlockActions;

    // Whether the light in this chunk is populated
    private boolean lightPopulated;

    // The set which contains all the entities in this chunk
    @SuppressWarnings("unchecked")
    private final Set<LanternEntity>[] entities = new Set[CHUNK_SECTIONS];

    {
        for (int i = 0; i < this.entities.length; i++) {
            this.entities[i] = Sets.newConcurrentHashSet();
        }
    }

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
        this.key = key(x, z);
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
        this.biomeMin = new Vector3i(this.min.getX(), 1, this.min.getZ());
        this.biomeMax = new Vector3i(this.max.getX(), 1, this.max.getZ());

        //noinspection unchecked
        final Short2ObjectMap<TrackerData>[] trackerDataSections = new Short2ObjectMap[CHUNK_SECTIONS];
        for (int i = 0; i < trackerDataSections.length; i++) {
            trackerDataSections[i] = new Short2ObjectOpenHashMap<>();
        }
        this.trackerData = new ConcurrentObjectArray<>(trackerDataSections);
    }

    public long getKey() {
        return this.key;
    }

    public ConcurrentObjectArray<Short2ObjectMap<TrackerData>> getTrackerData() {
        return this.trackerData;
    }

    /**
     * Initializes a empty chunk.
     * (Only used for initializing the chunk.)
     */
    void initializeEmpty() {
        //noinspection ConstantConditions
        if (this.chunkSections != null || this.biomes != null) {
            throw new IllegalStateException("Chunk is already initialized!");
        }
        this.heightMap = new byte[CHUNK_AREA];
        this.chunkSections = new ConcurrentObjectArray<>(new ChunkSection[CHUNK_SECTIONS]);
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
        this.chunkSections = new ConcurrentObjectArray<>(sections);
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
            ChunkSection section = this.chunkSections.getRawObjects()[y];
            if (section != null) {
                // Just fill the light array for now
                this.chunkSections.getRawObjects()[y].lightFromSky.fill((byte) 15);
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
        final ChunkSectionSnapshot[] array = new ChunkSectionSnapshot[CHUNK_SECTIONS];
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
     * @param x The x coordinate
     * @param z The z coordinate
     * @return The y coordinate
     */
    @Override
    public int getHighestYAt(int x, int z) {
        checkAreaBounds(x, z);
        if (!this.loaded) {
            return 0;
        }
        final int index = (z & 0xf) << 4 | x & 0xf;
        long stamp = this.heightMapLock.tryOptimisticRead();
        boolean lower = stamp != 0L && this.heightMapUpdateFlags.get(index);
        int height = stamp == 0L ? 0 : this.heightMap[index] & 0xff;
        if (stamp == 0L || !this.heightMapLock.validate(stamp)) {
            stamp = this.heightMapLock.readLock();
            lower = this.heightMapUpdateFlags.get(index);
            height = this.heightMap[index] & 0xff;
        } else {
            stamp = 0L;
        }
        // We have to update the height map for the coordinates
        if (lower) {
            long stamp1 = this.heightMapLock.tryConvertToWriteLock(stamp);
            if (stamp1 == 0L) {
                // We couldn't convert the lock, so create one anyway
                this.heightMapLock.unlockRead(stamp);
                stamp1 = this.heightMapLock.writeLock();
                // We were to late to acquire the lock, something else modified the index first
                if (!this.heightMapUpdateFlags.get(index)) {
                    return height;
                }
            }
            final int sections = height >> 4;
            // 0: The height we are looping through
            final int[] values0 = { 0 };
            // 0: Finished
            final boolean[] values1 = { false };
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
            this.heightMapLock.unlockWrite(stamp1);
        } else if (stamp != 0L) {
            this.heightMapLock.unlockRead(stamp);
        }
        return height;
    }

    @Override
    public int getPrecipitationLevelAt(int x, int z) {
        // TODO
        return getHighestYAt(x, z);
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
        final boolean[] finished = new boolean[CHUNK_AREA];

        // 0: The amount of finished searches
        final int[] values0 = { 0 };

        // 0: Whether we found the first non-null chunk
        // 1: Finished
        final boolean[] values1 = { false, false };

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
     * @return The x coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the z coordinate of the chunk.
     * 
     * @return The z coordinate
     */
    public int getZ() {
        return this.z;
    }

    /**
     * Gets the coordinates of the chunk.
     *
     * @return The coordinates
     */
    public Vector2i getCoords() {
        return this.chunkPos;
    }

    /**
     * Gets a copy of the biomes array.
     *
     * @return The biomes
     */
    public short[] getBiomes() {
        long stamp = this.biomesLock.tryOptimisticRead();
        short[] biomes = stamp == 0L ? null : this.biomes.clone();
        if (biomes == null || !this.biomesLock.validate(stamp)) {
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
        final long stamp = this.biomesLock.writeLock();
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
    public short getBiomeId(int x, int y, int z) {
        checkBiomeBounds(x, y, z);
        if (!this.loaded) {
            return 0;
        }
        final int index = (z & 0xf) << 4 | x & 0xf;
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
    public void setBiomeId(int x, int y, int z, short biome) {
        checkBiomeBounds(x, y, z);
        if (!this.loaded) {
            return;
        }
        final int index = (z & 0xf) << 4 | x & 0xf;
        final long stamp = this.biomesLock.writeLock();
        try {
            this.biomes[index] = biome;
        } finally {
            this.biomesLock.unlockWrite(stamp);
        }
    }

    public short getType(Vector3i coordinates) {
        return getType(coordinates.getX(), coordinates.getY(), coordinates.getZ());
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
        checkVolumeBounds(x, y, z);
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

    @Override
    public boolean setBlock(int x, int y, int z, BlockState block) {
        return setBlock(x, y, z, block, BlockChangeFlags.ALL);
    }

    @Override
    public boolean setBlock(int x, int y, int z, BlockState block, BlockChangeFlag flag) {
        checkNotNull(block, "block");
        checkNotNull(flag, "flag");
        checkVolumeBounds(x, y, z);
        if (!this.loaded) {
            return false;
        }

        final short type = BlockRegistryModule.get().getStateInternalIdAndData(block);
        final short type1;
        // Air doesn't have metadata values
        if (type >> 4 == 0 && type != 0) {
            type1 = 0;
        } else {
            type1 = type;
        }

        final BlockState[] changeData = new BlockState[1];

        final int rx = x & 0xf;
        final int rz = z & 0xf;
        this.chunkSections.work(y >> 4, section -> {
            if (section == null) {
                // The section is already filled with air,
                // so we can fail fast
                if (type1 == 0) {
                    return section;
                }
                // Create a new section
                section = new ChunkSection();
            }
            final int index = ChunkSection.index(rx, y & 0xf, rz);
            final short oldType = section.types[index];
            if (oldType == type1) {
                return section;
            }
            if (oldType != 0) {
                short count = section.typesCountMap.get(oldType);
                if (count > 0) {
                    if (--count <= 0) {
                        section.typesCountMap.remove(oldType);
                    } else {
                        section.typesCountMap.put(oldType, count);
                    }
                }
            }
            if (type1 != 0) {
                section.typesCountMap.put(type1, (short) (section.typesCountMap.get(type1) + 1));
                if (oldType == 0) {
                    section.nonAirCount++;
                }
            } else {
                section.nonAirCount--;
            }
            final BlockState oldState = BlockRegistryModule.get().getStateByInternalIdAndData(oldType).get();
            changeData[0] = oldState;
            // The section is empty, destroy it
            if (section.nonAirCount <= 0) {
                return null;
            }
            final LanternTileEntity tileEntity = section.tileEntities.get((short) index);
            boolean remove = false;
            boolean refresh = false;
            final Optional<TileEntityProvider> tileEntityProvider = ((LanternBlockType) block.getType()).getTileEntityProvider();
            if (tileEntity != null) {
                if (oldType == 0 || type1 == 0) {
                    remove = true;
                } else if (tileEntity instanceof ITileEntityRefreshBehavior) {
                    if (((ITileEntityRefreshBehavior) tileEntity).shouldRefresh(oldState, block)) {
                        remove = true;
                        refresh = true;
                    }
                } else if (oldType >> 4 != type1 >> 4) {
                    // The default behavior will only refresh if the
                    // block type is changed and not the block state
                    remove = true;
                    refresh = true;
                }
                if (refresh && !tileEntityProvider.isPresent()) {
                    refresh = false;
                }
            } else if (tileEntityProvider.isPresent()) {
                refresh = true;
            }
            if (remove) {
                tileEntity.setValid(false);
            }
            if (refresh) {
                final Location<World> location = tileEntity != null ? tileEntity.getLocation() : new Location<>(this.world, x, y, z);
                final LanternTileEntity newTileEntity = (LanternTileEntity) tileEntityProvider.get().get(block, location, null);
                section.tileEntities.put((short) index, newTileEntity);
                newTileEntity.setLocation(location);
                newTileEntity.setBlock(block);
                newTileEntity.setValid(true);
            } else if (remove) {
                section.tileEntities.remove((short) index);
            } else if (tileEntity != null) {
                tileEntity.setBlock(block);
            }
            section.types[index] = type1;
            return section;
        });

        final int index = rz << 4 | rx;
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

        if (changeData[0] != null) {
            this.world.getEventListener().onBlockChange(x, y, z, changeData[0], block);
        }

        return true;
    }

    public void addBlockAction(int x, int y, int z, BlockType blockType, BlockAction blockAction) {
        checkVolumeBounds(x, y, z);
        if (!this.loaded) {
            return;
        }
        if (this.getBlock(x, y, z).getType() == blockType) {
            this.world.getEventListener().onBlockAction(x, y, z, blockType, blockAction);
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
        checkVolumeBounds(x, y, z);
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
        checkVolumeBounds(x, y, z);
        if (!this.loaded) {
            return 15;
        }
        return this.chunkSections.work(y >> 4, section -> section == null ? 15 :
                section.lightFromSky.get(ChunkSection.index(x & 0xf, y & 0xf, z & 0xf)), false);
    }

    @Override
    public Location<Chunk> getLocation(Vector3i position) {
        return getLocation(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public Location<Chunk> getLocation(Vector3d position) {
        return getLocation(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public BlockSnapshot createSnapshot(int x, int y, int z) {
        final BlockState state = getBlock(x, y, z);
        final Location<World> loc = new Location<>(this.world, x, y, z);
        final LanternTileEntity tileEntity = getTileEntity(x, y, z)
                .map(tile -> LanternTileEntityArchetype.copy((LanternTileEntity) tile))
                .orElse(null);
        return new LanternBlockSnapshot(loc, state, ((LanternBlockType) state.getType()).getExtendedBlockStateProvider().get(state, loc, null),
                getCreator(x, y, z).orElse(null), getNotifier(x, y, z).orElse(null), tileEntity);
    }

    @Override
    public boolean restoreSnapshot(int x, int y, int z, BlockSnapshot snapshot, boolean force, BlockChangeFlag flag) {
        return ((LanternBlockSnapshot) snapshot).restoreAt(this, x, y, z, force, flag);
    }

    @Override
    public Optional<Entity> restoreSnapshot(EntitySnapshot snapshot, Vector3d position) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public boolean spawnEntity(Entity entity) {
        final Vector3d pos = entity.getLocation().getPosition();
        checkRange(pos.getX(), pos.getY(), pos.getZ());
        return this.world.spawnEntity(entity);
    }

    @Override
    public Collection<Entity> spawnEntities(Iterable<? extends Entity> entities) {
        for (Entity entity : entities) {
            final Vector3d pos = entity.getLocation().getPosition();
            checkRange(pos.getX(), pos.getY(), pos.getZ());
        }
        return this.world.spawnEntities(entities);
    }

    @Override
    public Set<EntityHit> getIntersectingEntities(Vector3d start, Vector3d end, Predicate<EntityHit> filter) {
        return Collections.emptySet();
    }

    @Override
    public Set<EntityHit> getIntersectingEntities(Vector3d start, Vector3d direction, double distance, Predicate<EntityHit> filter) {
        return Collections.emptySet();
    }

    public PriorityBlockingQueue<LanternScheduledBlockUpdate> getScheduledBlockUpdateQueue() {
        return this.scheduledBlockUpdateQueue;
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(int x, int y, int z) {
        checkVolumeBounds(x, y, z);
        if (!this.loaded) {
            return Collections.emptyList();
        }
        final Vector3i position = new Vector3i(x, y, z);
        return this.scheduledBlockUpdateQueue.stream()
                .filter(update -> update.getLocation().getBlockPosition().equals(position))
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(int x, int y, int z, int priority, int ticks) {
        checkVolumeBounds(x, y, z);
        final int entryId = this.scheduledBlockUpdateCounter.getAndIncrement();
        final Location<World> location = new Location<>(this.world, new Vector3i(x, y, z));
        final LanternScheduledBlockUpdate update = new LanternScheduledBlockUpdate(entryId, location, ticks, priority);
        this.scheduledBlockUpdateQueue.add(update);
        return update;
    }

    @Override
    public void removeScheduledUpdate(int x, int y, int z, ScheduledBlockUpdate update) {
        checkVolumeBounds(x, y, z);
        this.scheduledBlockUpdateQueue.remove(update);
    }

    public void pulse() {
        // The update entry
        LanternScheduledBlockUpdate update;
        while ((update = this.scheduledBlockUpdateQueue.peek()) != null && update.getTicks() <= 0) {
            // Remove the entry from the queue
            this.scheduledBlockUpdateQueue.poll();
            // TODO: Update
        }

        final CauseStack causeStack = CauseStack.Companion.current();
        causeStack.pushCause(this); // Add the chunk that is being pulsed
        getTileEntities().forEach(tileEntity -> {
            causeStack.pushCause(tileEntity); // Add the tile entity to the cause
            try {
                ((LanternTileEntity) tileEntity).pulse();
            } catch (Throwable t) {
                final Vector3i pos = tileEntity.getLocation().getBlockPosition();
                Lantern.getLogger().error("Failed to pulse TileEntity at ({};{};{})", pos.getX(), pos.getY(), pos.getZ(), t);
            } finally {
                causeStack.popCause(); // Pop the tile entity
            }
        });
        causeStack.popCause(); // Pop the chunk
    }

    @Override
    public boolean isLoaded() {
        return this.loaded;
    }

    @Override
    public Extent getExtentView(Vector3i newMin, Vector3i newMax) {
        checkVolumeBounds(newMin);
        checkVolumeBounds(newMax);
        return new ExtentViewDownsize(this, newMin, newMax);
    }

    @Override
    public MutableBiomeVolumeWorker<Chunk> getBiomeWorker() {
        return new LanternMutableBiomeVolumeWorker<>(this);
    }

    @Override
    public MutableBlockVolumeWorker<Chunk> getBlockWorker() {
        return new LanternMutableBlockVolumeWorker<>(this);
    }

    @Override
    public Optional<UUID> getCreator(int x, int y, int z) {
        checkVolumeBounds(x, y, z);
        final int creatorId = this.trackerData.work(y >> 4, trackerDataMap -> {
            final TrackerData trackerData = trackerDataMap.get((short) ChunkSection.index(x & 0xf, y & 0xf, z & 0xf));
            return trackerData == null ? TrackerIdAllocator.INVALID_ID : trackerData.creatorId;
        }, false);
        return creatorId == TrackerIdAllocator.INVALID_ID ? Optional.empty() : this.world.getProperties().getTrackerIdAllocator().get(creatorId);
    }

    @Override
    public Optional<UUID> getNotifier(int x, int y, int z) {
        checkVolumeBounds(x, y, z);
        final int notifierId = this.trackerData.work(y >> 4, trackerDataMap -> {
            final TrackerData trackerData = trackerDataMap.get((short) ChunkSection.index(x & 0xf, y & 0xf, z & 0xf));
            return trackerData == null ? TrackerIdAllocator.INVALID_ID : trackerData.notifierId;
        }, false);
        return notifierId == TrackerIdAllocator.INVALID_ID ? Optional.empty() : this.world.getProperties().getTrackerIdAllocator().get(notifierId);
    }

    @Override
    public void setCreator(int x, int y, int z, @Nullable UUID uuid) {
        checkVolumeBounds(x, y, z);
        final int creatorId = uuid == null ? -1 : this.world.getProperties().getTrackerIdAllocator().get(uuid);
        this.trackerData.work(y >> 4, trackerDataMap -> {
            final short index = (short) ChunkSection.index(x & 0xf, y & 0xf, z & 0xf);
            TrackerData trackerData = trackerDataMap.get(index);
            if (creatorId == -1) {
                if (trackerData != null && trackerData.notifierId == -1) {
                    trackerDataMap.remove(index);
                    return;
                } else if (trackerData == null) {
                    return;
                }
            }
            if (trackerData == null) {
                trackerData = new TrackerData();
                trackerDataMap.put(index, trackerData);
            }
            trackerData.creatorId = index;
        }, true);
    }

    @Override
    public void setNotifier(int x, int y, int z, @Nullable UUID uuid) {
        checkVolumeBounds(x, y, z);
        final int notifierId = uuid == null ? -1 : this.world.getProperties().getTrackerIdAllocator().get(uuid);
        this.trackerData.work(y >> 4, trackerDataMap -> {
            final short index = (short) ChunkSection.index(x & 0xf, y & 0xf, z & 0xf);
            TrackerData trackerData = trackerDataMap.get(index);
            if (notifierId == -1) {
                if (trackerData != null && trackerData.creatorId == -1) {
                    trackerDataMap.remove(index);
                    return;
                } else if (trackerData == null) {
                    return;
                }
            }
            if (trackerData == null) {
                trackerData = new TrackerData();
                trackerDataMap.put(index, trackerData);
            }
            trackerData.notifierId = index;
        }, true);
    }

    @Override
    public Optional<AABB> getBlockSelectionBox(int x, int y, int z) {
        final BlockState block = getBlock(x, y, z);
        if (block.getType() == BlockTypes.AIR) {
            return Optional.empty();
        }
        final ObjectProvider<AABB> aabbObjectProvider = ((LanternBlockType) block.getType()).getSelectionBoxProvider();
        if (aabbObjectProvider == null) {
            return Optional.empty();
        }
        final AABB aabb;
        if (aabbObjectProvider instanceof ConstantObjectProvider
                || aabbObjectProvider instanceof CachedSimpleObjectProvider
                || aabbObjectProvider instanceof SimpleObjectProvider) {
            aabb = aabbObjectProvider.get(block, null, null);
        } else {
            aabb = aabbObjectProvider.get(block, new Location<>(this.world, x, y, z), null);
        }
        return aabb == null ? Optional.empty() : Optional.of(aabb.offset(x, y, z));
    }

    @Override
    public Collection<AABB> getBlockCollisionBoxes(int x, int y, int z) {
        final BlockState block = getBlock(x, y, z);
        if (block.getType() == BlockTypes.AIR) {
            return Collections.emptySet();
        }
        final ObjectProvider<Collection<AABB>> aabbObjectProvider = ((LanternBlockType) block.getType()).getCollisionBoxesProvider();
        if (aabbObjectProvider == null) {
            return Collections.emptySet();
        }
        final Collection<AABB> collisionBoxes;
        if (aabbObjectProvider instanceof ConstantObjectProvider
                || aabbObjectProvider instanceof CachedSimpleObjectProvider
                || aabbObjectProvider instanceof SimpleObjectProvider) {
            collisionBoxes = aabbObjectProvider.get(block, null, null);
        } else {
            collisionBoxes = aabbObjectProvider.get(block, new Location<>(this.world, x, y, z), null);
        }
        return collisionBoxes == null || collisionBoxes.isEmpty() ? Collections.emptySet() : collisionBoxes.stream()
                .map(aabb -> aabb.offset(x, y, z))
                .collect(Collectors.toList());
    }

    @Override
    public Set<AABB> getIntersectingBlockCollisionBoxes(AABB box) {
        checkNotNull(box, "box");
        final Vector3i min = box.getMin().toInt();
        final Vector3i max = box.getMax().toInt();
        checkVolumeBounds(min);
        checkVolumeBounds(max);
        final ImmutableSet.Builder<AABB> builder = ImmutableSet.builder();
        final int minX = min.getX();
        final int minY = min.getY();
        final int minZ = min.getZ();
        final int maxX = max.getX();
        final int maxY = max.getY();
        final int maxZ = max.getZ();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = minY; y <= maxY; y++) {
                    final Collection<AABB> collisionBoxes = getBlockCollisionBoxes(x, y, z);
                    for (AABB collisionBox : collisionBoxes) {
                        if (collisionBox.intersects(box)) {
                            builder.add(collisionBox);
                        }
                    }
                }
            }
        }
        return builder.build();
    }

    @Override
    public Set<AABB> getIntersectingCollisionBoxes(Entity owner, AABB box) {
        checkNotNull(owner, "owner");
        checkNotNull(box, "box");
        final ImmutableSet.Builder<AABB> collisionBoxes = ImmutableSet.builder();
        final int maxYSection = fixEntityYSection(((int) Math.ceil(box.getMax().getY() + 2.0)) >> 4);
        final int minYSection = fixEntityYSection(((int) Math.floor(box.getMin().getY() - 2.0)) >> 4);
        for (int i = minYSection; i <= maxYSection; i++) {
            forEachEntity(i, entity -> {
                if (entity == owner) { // Ignore the owner
                    return;
                }
                final Optional<AABB> aabb = entity.getBoundingBox();
                if (aabb.isPresent() && aabb.get().intersects(box)) {
                    collisionBoxes.add(aabb.get());
                }
            });
        }
        final Vector3i min = box.getMin().toInt();
        final Vector3i max = box.getMax().toInt();
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    final Collection<AABB> blockCollisionBoxes = getBlockCollisionBoxes(x, y, z);
                    for (AABB collisionBox : blockCollisionBoxes) {
                        if (collisionBox.intersects(box)) {
                            collisionBoxes.add(collisionBox);
                        }
                    }
                }
            }
        }
        return collisionBoxes.build();
    }

    @Override
    public boolean hasIntersectingEntities(AABB box, Predicate<Entity> filter) {
        checkNotNull(box, "box");
        final int maxYSection = fixEntityYSection(((int) Math.ceil(box.getMax().getY() + 2.0)) >> 4);
        final int minYSection = fixEntityYSection(((int) Math.floor(box.getMin().getY() - 2.0)) >> 4);
        return hasIntersectingEntities(maxYSection, minYSection, box, filter);
    }

    @Override
    public Set<Entity> getIntersectingEntities(AABB box, Predicate<Entity> filter) {
        checkNotNull(box, "box");
        checkNotNull(filter, "filter");
        final ImmutableSet.Builder<Entity> entities = ImmutableSet.builder();
        final int maxYSection = fixEntityYSection(((int) Math.ceil(box.getMax().getY() + 2.0)) >> 4);
        final int minYSection = fixEntityYSection(((int) Math.floor(box.getMin().getY() - 2.0)) >> 4);
        addIntersectingEntities(entities, maxYSection, minYSection, box, filter);
        return entities.build();
    }

    public static int fixEntityYSection(int section) {
        return section < 0 ? 0 : section >= CHUNK_SECTIONS ? CHUNK_SECTIONS - 1 : section;
    }

    public boolean hasIntersectingEntities(int maxYSection, int minYSection, AABB box, @Nullable Predicate<Entity> filter) {
        for (int i = minYSection; i <= maxYSection; i++) {
            final Iterator<LanternEntity> iterator = this.entities[i].iterator();
            while (iterator.hasNext()) {
                final LanternEntity entity = iterator.next();
                // Only remove the entities that are "destroyed",
                // the other ones can be resurrected after chunk loading
                if (entity.getRemoveState() == LanternEntity.RemoveState.DESTROYED) {
                    iterator.remove();
                } else {
                    final Optional<AABB> aabb = entity.getBoundingBox();
                    if (aabb.isPresent()) {
                        if (aabb.get().intersects(box) && (filter == null || filter.test(entity))) {
                            return true;
                        }
                    } else if (box.contains(entity.getPosition()) && (filter == null || filter.test(entity))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void addIntersectingEntities(ImmutableSet.Builder<Entity> builder, int maxYSection, int minYSection, AABB box, Predicate<Entity> filter) {
        for (int i = minYSection; i <= maxYSection; i++) {
            forEachEntity(i, entity -> {
                final Optional<AABB> aabb = entity.getBoundingBox();
                if (aabb.isPresent()) {
                    if (aabb.get().intersects(box) && filter.test(entity)) {
                        builder.add(entity);
                    }
                } else if (box.contains(entity.getPosition()) && filter.test(entity)) {
                    builder.add(entity);
                }
            });
        }
    }

    public void addIntersectingEntitiesBoxes(ImmutableSet.Builder<AABB> builder, int maxYSection, int minYSection,
            AABB box, Predicate<Entity> filter) {
        for (int i = minYSection; i <= maxYSection; i++) {
            forEachEntity(i, entity -> {
                final Optional<AABB> aabb = entity.getBoundingBox();
                if (aabb.isPresent()) {
                    if (aabb.get().intersects(box) && filter.test(entity)) {
                        builder.add(aabb.get());
                    }
                } else if (box.contains(entity.getPosition()) && filter.test(entity)) {
                    builder.add(aabb.get());
                }
            });
        }
    }

    @Override
    public ArchetypeVolume createArchetypeVolume(Vector3i min, Vector3i max, Vector3i origin) {
        return null;
    }

    private void forEachEntity(int section, Consumer<LanternEntity> consumer) {
        final Iterator<LanternEntity> iterator = this.entities[section].iterator();
        while (iterator.hasNext()) {
            final LanternEntity entity = iterator.next();
            // Only remove the entities that are "destroyed",
            // the other ones can be resurrected after chunk loading
            if (entity.getRemoveState() == LanternEntity.RemoveState.DESTROYED) {
                iterator.remove();
            } else {
                consumer.accept(entity);
            }
        }
    }

    private void forEachEntity(Consumer<LanternEntity> consumer) {
        for (int i = 0; i < this.entities.length; i++) {
            forEachEntity(i, consumer);
        }
    }

    /**
     * Resurrects all the {@link Entity}s that
     * were temporarily "disabled" (chunk being unloaded).
     */
    void resurrectEntities() {
        forEachEntity(LanternEntity::resurrect);
    }

    /**
     * Bury all the {@link Entity}s that will be
     * temporarily "disabled" (chunk being unloaded).
     */
    void buryEntities() {
        forEachEntity(entity -> entity.remove(LanternEntity.RemoveState.CHUNK_UNLOAD));
    }

    public void addEntity(LanternEntity entity, int section) {
        this.entities[section].add(entity);
    }

    public void removeEntity(LanternEntity entity, int section) {
        this.entities[section].remove(entity);
    }

    @Override
    public Optional<Entity> getEntity(UUID uniqueId) {
        final Optional<Entity> optEntity = this.world.getEntity(uniqueId);
        if (optEntity.isPresent()) {
            final Vector3d pos = ((LanternEntity) optEntity.get()).getPosition();
            if (VecHelper.inBounds(pos.getFloorX(), pos.getFloorZ(), this.areaMin, this.areaMax)) {
                return optEntity;
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<Entity> getEntities() {
        final ImmutableList.Builder<Entity> entities = ImmutableList.builder();
        forEachEntity(entities::add);
        return entities.build();
    }

    @Override
    public Collection<Entity> getEntities(Predicate<Entity> filter) {
        return getEntities().stream().filter(filter).collect(ImmutableList.toImmutableList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Entity createEntity(EntityType type, Vector3d position) {
        checkNotNull(position, "position");
        final LanternEntityType entityType = (LanternEntityType) checkNotNull(type, "type");
        checkVolumeBounds(position.getFloorX(), position.getFloorY(), position.getFloorZ());
        //noinspection unchecked
        final LanternEntity entity = (LanternEntity) entityType.getEntityConstructor().apply(UUID.randomUUID());
        entity.setPositionAndWorld(this.world, position);
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
        checkNotNull(position, "position");
        checkVolumeBounds(position.getFloorX(), position.getFloorY(), position.getFloorZ());
        final Optional<Entity> optEntity = createEntity(entityContainer);
        optEntity.ifPresent(entity -> ((LanternEntity) entity).setPosition(position));
        return optEntity;
    }

    @Override
    public Collection<TileEntity> getTileEntities() {
        if (this.chunkSections == null) {
            return Collections.emptyList();
        }
        final ImmutableSet.Builder<TileEntity> tileEntities = ImmutableSet.builder();
        for (int i = 0; i < CHUNK_SECTIONS; i++) {
            this.chunkSections.work(i, chunkSection -> {
                if (chunkSection == null) {
                    return;
                }
                final ObjectIterator<LanternTileEntity> it = chunkSection.tileEntities.values().iterator();
                while (it.hasNext()) {
                    final LanternTileEntity tileEntity = it.next();
                    if (tileEntity.isValid()) {
                        tileEntities.add(tileEntity);
                    } else {
                        it.remove();
                    }
                }
            }, true);
        }
        return tileEntities.build();
    }

    @Override
    public Collection<TileEntity> getTileEntities(Predicate<TileEntity> filter) {
        return getTileEntities().stream().filter(filter).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public Optional<TileEntity> getTileEntity(int x, int y, int z) {
        checkVolumeBounds(x, y, z);
        final short index = (short) ChunkSection.index(x & 0xf, y & 0xf, z & 0xf);
        return this.chunkSections.work(y >> 4, chunkSection -> {
            if (chunkSection == null) {
                return Optional.empty();
            }
            final LanternTileEntity tileEntity = chunkSection.tileEntities.get(index);
            // Remove invalid tile entities
            if (tileEntity != null && !tileEntity.isValid()) {
                chunkSection.tileEntities.remove(index);
                return Optional.empty();
            }
            return Optional.ofNullable(tileEntity);
        }, true);
    }

    private void checkAreaBounds(int x, int z) {
        if (!VecHelper.inBounds(x, z, this.areaMin, this.areaMax)) {
            throw new PositionOutOfBoundsException(new Vector2i(x, z), this.areaMin, this.areaMax);
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
    public boolean containsBlock(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, this.min, this.max);
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        return BlockRegistryModule.get().getStateByInternalIdAndData(getType(x, y, z)).orElse(BlockTypes.AIR.getDefaultState());
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        return getBlock(x, y, z).getType();
    }

    @Override
    public void setBiome(int x, int y, int z, BiomeType biome) {
        setBiomeId(x, y, z, BiomeRegistryModule.get().getInternalId(biome));
    }

    @Override
    public Vector3i getBiomeMin() {
        return this.biomeMax;
    }

    @Override
    public Vector3i getBiomeMax() {
        return this.biomeMin;
    }

    @Override
    public Vector3i getBiomeSize() {
        return CHUNK_BIOME_VOLUME;
    }

    @Override
    public boolean containsBiome(int x, int y, int z) {
        return VecHelper.inBounds(x, z, this.areaMin, this.areaMax);
    }

    @Override
    public BiomeType getBiome(int x, int y, int z) {
        return BiomeRegistryModule.get().getByInternalId(getBiomeId(x, y, z)).orElse(BiomeTypes.OCEAN);
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Direction direction, Class<T> propertyClass) {
        return getProperty0(x, y, z, direction, propertyClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Class<T> propertyClass) {
        return getProperty0(x, y, z, null, propertyClass);
    }

    private <T extends Property<?, ?>> Optional<T> getProperty0(int x, int y, int z, @Nullable Direction direction, Class<T> propertyClass) {
        checkVolumeBounds(x, y, z);
        if (!this.loaded) {
            return Optional.empty();
        }
        final Location<World> location = new Location<>(this.world, x, y, z);
        Optional<T> property;
        if (direction != null) {
            property = AbstractDirectionRelativePropertyHolder.getPropertyFor(location, direction, propertyClass);
        } else {
            property = AbstractPropertyHolder.getPropertyFor(location, propertyClass);
        }
        if (direction == null && !property.isPresent()) {
            final Optional<TileEntity> tileEntity = getTileEntity(x, y, z);
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
        final Location<World> location = new Location<>(this.world, x, y, z);
        final ImmutableList.Builder<Property<?, ?>> builder = ImmutableList.builder();
        builder.addAll(Lantern.getGame().getPropertyRegistry().getPropertiesFor(location));
        getTileEntity(x, y, z).ifPresent(tile -> builder.addAll(tile.getApplicableProperties()));
        return builder.build();
    }

    private static final Direction[] CARDINAL_FACES = Arrays.stream(Direction.values()).filter(Direction::isCardinal).toArray(Direction[]::new);

    @Override
    public Collection<Direction> getFacesWithProperty(int x, int y, int z, Class<? extends Property<?, ?>> propertyClass) {
        if (!this.loaded) {
            return Collections.emptyList();
        }
        final Location<World> location = new Location<>(this.world, x, y, z);
        //noinspection unchecked
        final Optional<PropertyStore<?>> store = (Optional) Lantern.getGame().getPropertyRegistry().getStore(propertyClass);
        //noinspection OptionalIsPresent
        if (!store.isPresent()) {
            return Collections.emptyList();
        }
        return Arrays.stream(CARDINAL_FACES)
                .filter(direction -> AbstractDirectionRelativePropertyHolder.getPropertyFor(location, direction, store.get()).isPresent())
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    public <E> Optional<E> get(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        if (!this.loaded) {
            return Optional.empty();
        }
        final BlockState blockState = getBlock(x, y, z);
        Optional<E> value = blockState.get(key);
        if (!value.isPresent()) {
            final Optional<TileEntity> tileEntity = getTileEntity(x, y, z);
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
        final BlockState blockState = getBlock(x, y, z);
        Optional<V> value = blockState.getValue(key);
        if (!value.isPresent()) {
            final Optional<TileEntity> tileEntity = getTileEntity(x, y, z);
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
    public LanternWorld getWorld() {
        return this.world;
    }

    @Override
    public boolean isPopulated() {
        return this.populated;
    }

    @Override
    public boolean loadChunk(boolean generate) {
        if (this.world.getChunkManager().load(this, CauseStack.currentOrEmpty(), generate)) {
            this.loaded = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean unloadChunk() {
        if (this.world.getChunkManager().unload(this, CauseStack.currentOrEmpty())) {
            this.loaded = false;
            return true;
        }
        return false;
    }

    @Deprecated
    @Override
    public int getInhabittedTime() {
        return getInhabitedTime();
    }

    @Override
    public int getInhabitedTime() {
        return (int) Math.min(Integer.MAX_VALUE, this.inhabitedTime);
    }

    public long getLongInhabitedTime() {
        return this.inhabitedTime;
    }

    public void setInhabitedTime(long inhabitedTime) {
        this.inhabitedTime = inhabitedTime;
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
    public boolean hitBlock(int x, int y, int z, Direction side, GameProfile profile) {
        checkVolumeBounds(x, y, z);
        return this.world.hitBlock(this.min.getX() + x, this.min.getY() + y, this.min.getZ() + z, side, profile);
    }

    @Override
    public boolean interactBlock(int x, int y, int z, Direction side, GameProfile profile) {
        checkVolumeBounds(x, y, z);
        return this.world.interactBlock(this.min.getX() + x, this.min.getY() + y, this.min.getZ() + z, side, profile);
    }

    @Override
    public boolean interactBlockWith(int x, int y, int z, ItemStack itemStack, Direction side, GameProfile profile) {
        checkVolumeBounds(x, y, z);
        return this.world.interactBlockWith(this.min.getX() + x, this.min.getY() + y, this.min.getZ() + z, itemStack, side, profile);
    }

    @Override
    public boolean digBlock(int x, int y, int z, GameProfile profile) {
        checkVolumeBounds(x, y, z);
        return this.world.digBlock(this.min.getX() + x, this.min.getY() + y, this.min.getZ() + z, profile);
    }

    @Override
    public boolean digBlockWith(int x, int y, int z, ItemStack itemStack, GameProfile profile) {
        checkVolumeBounds(x, y, z);
        return this.world.digBlockWith(this.min.getX() + x, this.min.getY() + y, this.min.getZ() + z, itemStack, profile);
    }

    @Override
    public int getBlockDigTimeWith(int x, int y, int z, ItemStack itemStack, GameProfile profile) {
        checkVolumeBounds(x, y, z);
        return this.world.getBlockDigTimeWith(this.min.getX() + x, this.min.getY() + y, this.min.getZ() + z, itemStack, profile);
    }

    @Override
    public boolean placeBlock(int x, int y, int z, BlockState block, Direction direction, GameProfile profile) {
        checkVolumeBounds(x, y, z);
        return this.world.placeBlock(this.min.getX() + x, this.min.getY() + y, this.min.getZ() + z, block, direction, profile);
    }
}
