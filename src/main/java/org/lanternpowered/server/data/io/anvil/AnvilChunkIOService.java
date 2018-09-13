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
/*
 * Copyright (c) 2011-2014 Glowstone - Tad Hardesty
 * Copyright (c) 2010-2011 Lightstone - Graham Edgecombe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.data.io.anvil;

import static org.lanternpowered.server.data.io.anvil.RegionFileCache.REGION_AREA;
import static org.lanternpowered.server.data.io.anvil.RegionFileCache.REGION_MASK;
import static org.lanternpowered.server.data.io.anvil.RegionFileCache.REGION_SIZE;
import static org.lanternpowered.server.world.chunk.LanternChunk.fixEntityYSection;

import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import org.lanternpowered.server.block.tile.LanternTileEntity;
import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.data.io.ChunkIOService;
import org.lanternpowered.server.data.io.store.ObjectSerializer;
import org.lanternpowered.server.data.io.store.ObjectSerializerRegistry;
import org.lanternpowered.server.data.persistence.nbt.NbtDataContainerInputStream;
import org.lanternpowered.server.data.persistence.nbt.NbtDataContainerOutputStream;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.game.DirectoryKeys;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.scheduler.LanternScheduler;
import org.lanternpowered.server.util.UncheckedThrowables;
import org.lanternpowered.server.util.collect.array.NibbleArray;
import org.lanternpowered.server.world.chunk.LanternChunk;
import org.lanternpowered.server.world.chunk.LanternChunk.ChunkSection;
import org.lanternpowered.server.world.chunk.LanternChunk.ChunkSectionSnapshot;
import org.slf4j.Logger;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.ChunkDataStream;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;

import javax.annotation.Nullable;

@Singleton
public class AnvilChunkIOService implements ChunkIOService {

    private static final DataQuery VERSION = DataQuery.of("V"); // byte
    private static final DataQuery LEVEL = DataQuery.of("Level"); // compound
    private static final DataQuery SECTIONS = DataQuery.of("Sections"); // array
    private static final DataQuery X = DataQuery.of("xPos"); // int
    private static final DataQuery Z = DataQuery.of("zPos"); // int
    private static final DataQuery Y = DataQuery.of("Y"); // byte
    private static final DataQuery BLOCKS = DataQuery.of("Blocks"); // byte array
    private static final DataQuery BLOCKS_EXTRA = DataQuery.of("Add"); // (nibble) byte array
    private static final DataQuery DATA = DataQuery.of("Data"); // (nibble) byte array
    private static final DataQuery BLOCK_LIGHT = DataQuery.of("BlockLight"); // (nibble) byte array
    private static final DataQuery SKY_LIGHT = DataQuery.of("SkyLight"); // (nibble) byte array
    private static final DataQuery TERRAIN_POPULATED = DataQuery.of("TerrainPopulated"); // (boolean) byte
    private static final DataQuery LIGHT_POPULATED = DataQuery.of("LightPopulated"); // (boolean) byte
    private static final DataQuery BIOMES = DataQuery.of("Biomes"); // byte array
    // A extra tag for the biomes to support the custom biomes
    private static final DataQuery BIOMES_EXTRA = DataQuery.of("BiomesE"); // byte array
    private static final DataQuery HEIGHT_MAP = DataQuery.of("HeightMap");  // int array
    private static final DataQuery LAST_UPDATE = DataQuery.of("LastUpdate"); // long
    private static final DataQuery TRACKER_DATA_TABLE = DataQuery.of("BlockPosTable");
    private static final DataQuery TRACKER_BLOCK_POS = DataQuery.of("pos");
    private static final DataQuery TRACKER_ENTRY_CREATOR = DataQuery.of("owner");
    private static final DataQuery TRACKER_ENTRY_NOTIFIER = DataQuery.of("notifier");
    private static final DataQuery TILE_ENTITY_X = DataQuery.of("x");
    private static final DataQuery TILE_ENTITY_Y = DataQuery.of("y");
    private static final DataQuery TILE_ENTITY_Z = DataQuery.of("z");
    private static final DataQuery TILE_ENTITIES = DataQuery.of("TileEntities");
    private static final DataQuery INHABITED_TIME = DataQuery.of("InhabitedTime");
    private static final DataQuery ENTITIES = DataQuery.of("Entities");

    private final World world;
    private final Logger logger;
    private final LanternScheduler scheduler;
    private final RegionFileCache cache;
    private final Path baseDir;

    // TODO: Consider the session.lock file

    @Inject
    public AnvilChunkIOService(@Named(DirectoryKeys.WORLD) Path baseDir, World world, Logger logger, LanternScheduler scheduler) {
        this.cache = new RegionFileCache(baseDir);
        this.scheduler = scheduler;
        this.baseDir = baseDir;
        this.logger = logger;
        this.world = world;
    }

    @Override
    public boolean exists(int x, int z) throws IOException {
        final RegionFile region = this.cache.getRegionFileByChunk(x, z);

        final int regionX = x & REGION_MASK;
        final int regionZ = z & REGION_MASK;

        return region.hasChunk(regionX, regionZ);
    }

    @Override
    public boolean read(LanternChunk chunk) throws IOException {
        final int x = chunk.getX();
        final int z = chunk.getZ();

        final RegionFile region = this.cache.getRegionFileByChunk(x, z);
        final int regionX = x & REGION_MASK;
        final int regionZ = z & REGION_MASK;

        final DataInputStream is = region.getChunkDataInputStream(regionX, regionZ);
        if (is == null) {
            return false;
        }

        final DataView levelDataView;
        try (NbtDataContainerInputStream nbt = new NbtDataContainerInputStream(is)) {
            levelDataView = nbt.read().getView(LEVEL).get();
        }

        // read the vertical sections
        final List<DataView> sectionList = levelDataView.getViewList(SECTIONS).get();
        final ChunkSection[] sections = new ChunkSection[16];
        final short[][] blockTypes = new short[16][];
        //noinspection unchecked
        final Short2ObjectOpenHashMap<LanternTileEntity>[] tileEntitySections = new Short2ObjectOpenHashMap[sections.length];

        for (DataView sectionTag : sectionList) {
            final int y = sectionTag.getInt(Y).get();
            final byte[] rawTypes = (byte[]) sectionTag.get(BLOCKS).get();

            final byte[] extTypes = sectionTag.contains(BLOCKS_EXTRA) ? (byte[]) sectionTag.get(BLOCKS_EXTRA).get() : null;
            final byte[] data = (byte[]) sectionTag.get(DATA).get();
            final byte[] blockLight = (byte[]) sectionTag.get(BLOCK_LIGHT).get();
            final byte[] skyLight = (byte[]) sectionTag.get(SKY_LIGHT).get();

            final NibbleArray dataArray = new NibbleArray(rawTypes.length, data, true);
            final NibbleArray extTypesArray = extTypes == null ? null : new NibbleArray(rawTypes.length, extTypes, true);

            blockTypes[y] = new short[rawTypes.length];
            for (int i = 0; i < rawTypes.length; i++) {
                blockTypes[y][i] = (short) ((extTypesArray == null ? 0 : extTypesArray.get(i)) << 12 | ((rawTypes[i] & 0xff) << 4) | dataArray.get(i));
            }

            tileEntitySections[y] = new Short2ObjectOpenHashMap<>();
            sections[y] = new ChunkSection(blockTypes[y], new NibbleArray(rawTypes.length, skyLight, true),
                    new NibbleArray(rawTypes.length, blockLight, true), tileEntitySections[y]);
        }

        levelDataView.getViewList(TILE_ENTITIES).ifPresent(tileEntityViews -> {
            final ObjectSerializer<LanternTileEntity> tileEntitySerializer = ObjectSerializerRegistry.get().get(LanternTileEntity.class).get();
            for (DataView tileEntityView : tileEntityViews) {
                final int tileY = tileEntityView.getInt(TILE_ENTITY_Y).get();
                final int section = tileY >> 4;
                if (tileEntitySections[section] == null) {
                    continue;
                }
                final int tileZ = tileEntityView.getInt(TILE_ENTITY_Z).get();
                final int tileX = tileEntityView.getInt(TILE_ENTITY_X).get();
                try {
                    final LanternTileEntity tileEntity = tileEntitySerializer.deserialize(tileEntityView);
                    tileEntity.setLocation(new Location<>(this.world, tileX, tileY, tileZ));
                    final short index = (short) ChunkSection.index(tileX & 0xf, tileY & 0xf, tileZ & 0xf);
                    tileEntity.setBlock(BlockRegistryModule.get().getStateByInternalIdAndData(blockTypes[section][index]).get());
                    tileEntity.setValid(true);
                    tileEntitySections[section].put(index, tileEntity);
                } catch (InvalidDataException e) {
                    this.logger.warn("Error loading tile entity at ({};{};{}) in the chunk ({},{}) in the world {}",
                            tileX & 0xf, tileY & 0xf, tileZ & 0xf, x, z, getWorldProperties().getWorldName(), e);
                }
            }
        });

        final DataView spongeDataView = levelDataView.getView(DataQueries.SPONGE_DATA).orElse(null);
        final List<DataView> trackerDataViews = spongeDataView == null ? null : levelDataView.getViewList(TRACKER_DATA_TABLE).orElse(null);

        //noinspection unchecked
        final Short2ObjectMap<LanternChunk.TrackerData>[] trackerData = chunk.getTrackerData().getRawObjects();

        if (trackerDataViews != null) {
            for (DataView dataView : trackerDataViews) {
                final Optional<Short> optIndex = dataView.getShort(TRACKER_BLOCK_POS);
                if (!optIndex.isPresent()) {
                    continue;
                }
                final int creatorId = dataView.getInt(TRACKER_ENTRY_CREATOR).orElse(-1);
                final int notifierId = dataView.getInt(TRACKER_ENTRY_NOTIFIER).orElse(-1);
                // index = z << 12 | y << 4 | x
                int index = optIndex.get() & 0xffff;
                final int section = (index >> 8) & 0xf;
                // Convert the index to the section based system
                // index = y << 8 | z << 4 | x
                index = ChunkSection.index(index & 0xf, (index >> 4) & 0xf, index >> 12);
                trackerData[section].put((short) index, new LanternChunk.TrackerData(creatorId, notifierId));
            }
        }

        // initialize the chunk
        chunk.initializeSections(sections);
        chunk.setPopulated(levelDataView.getInt(TERRAIN_POPULATED).orElse(0) > 0);

        if (levelDataView.contains(BIOMES)) {
            final byte[] biomes = (byte[]) levelDataView.get(BIOMES).get();
            final byte[] biomesExtra = (byte[]) (levelDataView.contains(BIOMES_EXTRA) ? levelDataView.get(BIOMES_EXTRA).get() : null);
            final short[] newBiomes = new short[biomes.length];
            for (int i = 0; i < biomes.length; i++) {
                newBiomes[i] = (short) ((biomesExtra == null ? 0 : biomesExtra[i]) << 8 | biomes[i]);
            }
            chunk.initializeBiomes(newBiomes);
        }

        final Object heightMap;
        if (levelDataView.contains(HEIGHT_MAP) && (heightMap = levelDataView.get(HEIGHT_MAP).get()) instanceof int[]) {
            chunk.initializeHeightMap((int[]) heightMap);
        } else {
            chunk.initializeHeightMap(null);
        }

        levelDataView.getLong(INHABITED_TIME).ifPresent(time -> chunk.setInhabitedTime(time.intValue()));
        chunk.setLightPopulated(levelDataView.getInt(LIGHT_POPULATED).orElse(0) > 0);
        chunk.initializeLight();

        levelDataView.getViewList(ENTITIES).ifPresent(entityViews -> {
            final ObjectSerializer<LanternEntity> entitySerializer = ObjectSerializerRegistry.get().get(LanternEntity.class).get();
            for (DataView entityView : entityViews) {
                try {
                    final LanternEntity entity = entitySerializer.deserialize(entityView);
                    final int ySection = fixEntityYSection(entity.getPosition().getFloorY() >> 4);
                    chunk.addEntity(entity, ySection);
                } catch (InvalidDataException e) {
                    this.logger.warn("Error loading entity in the chunk ({},{}) in the world {}",
                            x, z, getWorldProperties().getWorldName(), e);
                }
            }
        });

        return true;
    }

    @Override
    public void write(LanternChunk chunk) throws IOException {
        final int x = chunk.getX();
        final int z = chunk.getZ();

        final RegionFile region = this.cache.getRegionFileByChunk(x, z);

        final int regionX = x & REGION_MASK;
        final int regionZ = z & REGION_MASK;

        final DataContainer rootView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        final DataView levelDataView = rootView.createView(LEVEL);

        // Core properties
        levelDataView.set(VERSION, (byte) 1);
        levelDataView.set(X, chunk.getX());
        levelDataView.set(Z, chunk.getZ());
        levelDataView.set(TERRAIN_POPULATED, (byte) (chunk.isPopulated() ? 1 : 0));
        levelDataView.set(LIGHT_POPULATED, (byte) (chunk.isLightPopulated() ? 1 : 0));
        levelDataView.set(LAST_UPDATE, 0L);
        levelDataView.set(INHABITED_TIME, chunk.getLongInhabitedTime());

        // Chunk sections
        final ChunkSectionSnapshot[] sections = chunk.getSectionSnapshots(true);
        final List<DataView> sectionDataViews = new ArrayList<>();
        final List<DataView> tileEntityDataViews = new ArrayList<>();

        for (byte i = 0; i < sections.length; ++i) {
            final ChunkSectionSnapshot section = sections[i];
            if (section == null) {
                continue;
            }

            final DataContainer sectionDataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
            sectionDataView.set(Y, i);

            final byte[] rawTypes = new byte[section.types.length];
            final short[] types = section.types;

            NibbleArray extTypes = null;
            final NibbleArray data = new NibbleArray(rawTypes.length);

            for (int j = 0; j < rawTypes.length; j++) {
                rawTypes[j] = (byte) ((types[j] >> 4) & 0xff);
                byte extType = (byte) (types[j] >> 12);
                if (extType != 0) {
                    if (extTypes == null) {
                        extTypes = new NibbleArray(rawTypes.length);
                    }
                    extTypes.set(j, extType);
                }
                data.set(j, (byte) (types[j] & 0xf));
            }
            sectionDataView.set(BLOCKS, rawTypes);
            if (extTypes != null) {
                sectionDataView.set(BLOCKS_EXTRA, extTypes.getPackedArray());
            }
            sectionDataView.set(DATA, data.getPackedArray());
            sectionDataView.set(BLOCK_LIGHT, section.lightFromBlock);

            final byte[] lightFromSky = section.lightFromSky;
            if (lightFromSky != null) {
                sectionDataView.set(SKY_LIGHT, lightFromSky);
            }

            sectionDataViews.add(sectionDataView);

            //noinspection unchecked
            final ObjectSerializer<LanternTileEntity> tileEntitySerializer = ObjectSerializerRegistry.get().get(LanternTileEntity.class).get();
            // Serialize the tile entities
            for (Short2ObjectMap.Entry<LanternTileEntity> tileEntityEntry : section.tileEntities.short2ObjectEntrySet()) {
                if (!tileEntityEntry.getValue().isValid()) {
                    continue;
                }
                final DataView dataView = tileEntitySerializer.serialize(tileEntityEntry.getValue());
                final short pos = tileEntityEntry.getShortKey();
                dataView.set(TILE_ENTITY_X, x * 16 + (pos & 0xf));
                dataView.set(TILE_ENTITY_Y, (i << 4) | (pos >> 8));
                dataView.set(TILE_ENTITY_Z, z * 16 + ((pos >> 4) & 0xf));
                tileEntityDataViews.add(dataView);
            }
        }

        levelDataView.set(TILE_ENTITIES, tileEntityDataViews);
        levelDataView.set(SECTIONS, sectionDataViews);
        levelDataView.set(HEIGHT_MAP, chunk.getHeightMap());

        //noinspection unchecked
        final Short2ObjectMap<LanternChunk.TrackerData>[] trackerData = chunk.getTrackerData().getRawObjects();
        final List<DataView> trackerDataViews = new ArrayList<>();

        for (int i = 0; i < trackerData.length; i++) {
            final Short2ObjectMap<LanternChunk.TrackerData> trackerDataSection = trackerData[i];
            for (Short2ObjectMap.Entry<LanternChunk.TrackerData> entry : trackerDataSection.short2ObjectEntrySet()) {
                // index = y << 8 | z << 4 | x
                int index = entry.getShortKey() & 0xffff;
                // Convert the index to the column based system
                // index = z << 12 | y << 4 | x
                index = ((index >> 4) & 0xf) << 12 | i << 8 | (index >> 4) & 0xf0 | index & 0xf;
                final DataView trackerDataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
                trackerDataView.set(TRACKER_BLOCK_POS, (short) index);
                trackerDataView.set(TRACKER_ENTRY_NOTIFIER, entry.getValue().getNotifierId());
                trackerDataView.set(TRACKER_ENTRY_CREATOR, entry.getValue().getCreatorId());
                trackerDataViews.add(trackerDataView);
            }
        }

        if (!trackerDataViews.isEmpty()) {
            levelDataView.createView(DataQueries.SPONGE_DATA).set(TRACKER_DATA_TABLE, trackerDataViews);
        }

        final short[] biomes = chunk.getBiomes();
        final byte[] biomes0 = new byte[biomes.length];
        byte[] biomes1 = null;

        for (int i = 0; i < biomes.length; i++) {
            biomes0[i] = (byte) (biomes[i] & 0xff);
            byte value = (byte) ((biomes[i] >> 4) & 0xff);
            if (value != 0) {
                if (biomes1 == null) {
                    biomes1 = new byte[biomes0.length];
                }
                biomes1[i] = value;
            }
        }

        levelDataView.set(BIOMES, biomes0);
        if (biomes1 != null) {
            levelDataView.set(BIOMES_EXTRA, biomes1);
        }

        //noinspection unchecked
        final List<LanternEntity> entities = new ArrayList(chunk.getEntities(entity -> !(entity instanceof Player)));
        final ObjectSerializer<LanternEntity> entitySerializer = ObjectSerializerRegistry.get().get(LanternEntity.class).get();

        final List<DataView> entityViews = new ArrayList<>();
        for (LanternEntity entity : entities) {
            if (entity.getRemoveState() == LanternEntity.RemoveState.DESTROYED) {
                continue;
            }
            final DataView entityView = entitySerializer.serialize(entity);
            entityViews.add(entityView);
        }

        levelDataView.set(ENTITIES, entityViews);

        try (NbtDataContainerOutputStream nbt = new NbtDataContainerOutputStream(region.getChunkDataOutputStream(regionX, regionZ))) {
            nbt.write(rootView);
            nbt.flush();
        }
    }

    @Override
    public void unload() throws IOException {
        this.cache.clear();
    }

    @Override
    public ChunkDataStream getGeneratedChunks() {
        return new ChunkDataStream() {

            // All the region files
            private Path[] paths;

            // The current region file that we opened
            @Nullable private RegionFile region;

            // The coordinates of the chunk inside the region
            private int chunkX;
            private int chunkZ;

            // The next index of the chunk in the region file
            private int regionChunkIndex;
            // The next index that we are in the file array
            private int regionFileIndex;

            // Whether the current fields are cached
            private boolean cached;

            // Done, no new chunks can be found
            private boolean done;

            {
                // Use the reset to initialize
                this.reset();
            }

            @Override
            public DataContainer next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }

                try {
                    final DataInputStream is = this.region.getChunkDataInputStream(this.chunkX, this.chunkZ);
                    final DataContainer data;

                    try (NbtDataContainerInputStream nbt = new NbtDataContainerInputStream(is)) {
                        data = nbt.read();
                    }

                    this.cached = false;
                    return data;
                } catch (IOException e) {
                    // This shouldn't happen
                    throw UncheckedThrowables.throwUnchecked(e);
                }
            }

            @Override
            public boolean hasNext() {
                // Fast fail
                if (this.done) {
                    return false;
                }
                // Use the cached index if set
                if (this.cached) {
                    return true;
                }
                // Try first to search for more chunks in the current region
                while (true) {
                    if (this.region != null) {
                        while (++this.regionChunkIndex < REGION_AREA) {
                            this.chunkX = this.regionChunkIndex / REGION_SIZE;
                            this.chunkZ = this.regionChunkIndex % REGION_SIZE;
                            if (this.region.hasChunk(this.chunkX, this.chunkZ)) {
                                this.cached = true;
                                return true;
                            }
                        }
                    }
                    // There no chunk available in the current region,
                    // reset the chunk index for the next one
                    this.regionChunkIndex = -1;
                    // There was no chunk present in the current region,
                    // try the next region
                    if (++this.regionFileIndex >= this.paths.length) {
                        this.region = null;
                        this.done = true;
                        return false;
                    }
                    final Path nextRegionFile = this.paths[this.regionFileIndex];
                    if (Files.exists(nextRegionFile)) {
                        Matcher matcher = cache.getFilePattern().matcher(nextRegionFile.getFileName().toString());
                        int regionX = Integer.parseInt(matcher.group(0));
                        int regionZ = Integer.parseInt(matcher.group(1));
                        try {
                            this.region = cache.getRegionFile(regionX, regionZ);
                        } catch (IOException e) {
                            logger.error("Failed to read the region file ({};{}) in the world folder {}",
                                    regionX, regionZ, baseDir.getFileName().toString(), e);
                            this.region = null;
                        }
                    } else {
                        this.region = null;
                    }
                }
            }

            @Override
            public int available() {
                // TODO: Not sure how we will be able to do this without opening all
                // the region files
                throw new UnsupportedOperationException();
            }

            @Override
            public void reset() {
                this.paths = cache.getRegionFiles();
                this.regionFileIndex = -1;
                this.regionChunkIndex = -1;
                this.region = null;
                this.cached = false;
                this.done = false;
            }
        };
    }

    @Override
    public CompletableFuture<Boolean> doesChunkExist(Vector3i chunkCoords) {
        return this.scheduler.submitAsyncTask(() -> exists(chunkCoords.getX(), chunkCoords.getZ()));
    }

    @Override
    public CompletableFuture<Optional<DataContainer>> getChunkData(Vector3i chunkCoords) {
        return this.scheduler.submitAsyncTask(() -> {
            final int x = chunkCoords.getX();
            final int z = chunkCoords.getZ();

            final RegionFile region = cache.getRegionFileByChunk(x, z);
            final int regionX = x & REGION_MASK;
            final int regionZ = z & REGION_MASK;

            final DataInputStream is = region.getChunkDataInputStream(regionX, regionZ);
            if (is == null) {
                return Optional.empty();
            }

            final DataContainer data;
            try (NbtDataContainerInputStream nbt = new NbtDataContainerInputStream(is)) {
                data = nbt.read();
            }

            return Optional.of(data);
        });
    }

    @Override
    public WorldProperties getWorldProperties() {
        return this.world.getProperties();
    }

}
