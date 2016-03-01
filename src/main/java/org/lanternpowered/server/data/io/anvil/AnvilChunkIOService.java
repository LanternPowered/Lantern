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

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import org.lanternpowered.server.data.io.ChunkIOService;
import org.lanternpowered.server.data.persistence.nbt.NbtDataContainerInputStream;
import org.lanternpowered.server.data.persistence.nbt.NbtDataContainerOutputStream;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.scheduler.LanternScheduler;
import org.lanternpowered.server.util.NibbleArray;
import org.lanternpowered.server.world.chunk.LanternChunk;
import org.lanternpowered.server.world.chunk.LanternChunk.ChunkSection;
import org.lanternpowered.server.world.chunk.LanternChunk.ChunkSectionSnapshot;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.storage.ChunkDataStream;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;

import javax.annotation.Nullable;

@NonnullByDefault
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

    private final WorldProperties properties;
    private final RegionFileCache cache;
    private final Path baseDir;

    // TODO: Consider the session.lock file

    public AnvilChunkIOService(Path baseDir, WorldProperties properties) {
        this.cache = new RegionFileCache(baseDir);
        this.properties = properties;
        this.baseDir = baseDir;
    }

    public boolean exists(int x, int z) throws IOException {
        RegionFile region = this.cache.getRegionFileByChunk(x, z);

        int regionX = x & REGION_MASK;
        int regionZ = z & REGION_MASK;

        return region.hasChunk(regionX, regionZ);
    }

    @Override
    public boolean read(LanternChunk chunk) throws IOException {
        int x = chunk.getX();
        int z = chunk.getZ();

        RegionFile region = this.cache.getRegionFileByChunk(x, z);
        int regionX = x & REGION_MASK;
        int regionZ = z & REGION_MASK;

        DataInputStream is = region.getChunkDataInputStream(regionX, regionZ);
        if (is == null) {
            return false;
        }

        DataView levelTag;
        try (NbtDataContainerInputStream nbt = new NbtDataContainerInputStream(is)) {
            levelTag = nbt.read().getView(LEVEL).get();
        }

        // read the vertical sections
        List<DataView> sectionList = levelTag.getViewList(SECTIONS).get();
        ChunkSection[] sections = new ChunkSection[16];

        for (DataView sectionTag : sectionList) {
            int y = sectionTag.getInt(Y).get();
            byte[] rawTypes = (byte[]) sectionTag.get(BLOCKS).get();

            byte[] extTypes = sectionTag.contains(BLOCKS_EXTRA) ? (byte[]) sectionTag.get(BLOCKS_EXTRA).get() : null;
            byte[] data = (byte[]) sectionTag.get(DATA).get();
            byte[] blockLight = (byte[]) sectionTag.get(BLOCK_LIGHT).get();
            byte[] skyLight = (byte[]) sectionTag.get(SKY_LIGHT).get();

            NibbleArray dataArray = new NibbleArray(rawTypes.length, data, true);
            NibbleArray extTypesArray = extTypes == null ? null : new NibbleArray(rawTypes.length, extTypes, true);

            short[] types = new short[rawTypes.length];
            for (int i = 0; i < rawTypes.length; i++) {
                types[i] = (short) ((extTypesArray == null ? 0 : extTypesArray.get(i)) << 12 | ((rawTypes[i] & 0xff) << 4) | dataArray.get(i));
            }

            sections[y] = new ChunkSection(types, new NibbleArray(rawTypes.length, skyLight, true),
                    new NibbleArray(rawTypes.length, blockLight, true));
        }

        // initialize the chunk
        chunk.initializeSections(sections);
        chunk.setPopulated(levelTag.getInt(TERRAIN_POPULATED).orElse(0) > 0);

        if (levelTag.contains(BIOMES)) {
            byte[] biomes = (byte[]) levelTag.get(BIOMES).get();
            byte[] biomesExtra = (byte[]) (levelTag.contains(BIOMES_EXTRA) ? levelTag.get(BIOMES_EXTRA).get() : null);
            short[] newBiomes = new short[biomes.length];
            for (int i = 0; i < biomes.length; i++) {
                newBiomes[i] = (short) ((biomesExtra == null ? 0 : biomesExtra[i]) << 8 | biomes[i]);
            }
            chunk.initializeBiomes(newBiomes);
        }

        Object heightMap;
        if (levelTag.contains(HEIGHT_MAP) && (heightMap = levelTag.get(HEIGHT_MAP).get()) instanceof int[]) {
            chunk.initializeHeightMap((int[]) heightMap);
        } else {
            chunk.initializeHeightMap(null);
        }

        chunk.setLightPopulated(levelTag.getInt(LIGHT_POPULATED).orElse(0) > 0);
        chunk.initializeLight();

        /*
        // read entities
        if (levelTag.isList("Entities", TagType.COMPOUND)) {
            for (CompoundTag entityTag : levelTag.getCompoundList("Entities")) {
                try {
                    // note that creating the entity is sufficient to add it to the world
                    EntityStorage.loadEntity(chunk.getWorld(), entityTag);
                } catch (Exception e) {
                    String id = entityTag.isString("id") ? entityTag.getString("id") : "<missing>";
                    if (e.getMessage() != null && e.getMessage().startsWith("Unknown entity type to load:")) {
                        GlowServer.logger.warning("Unknown entity in " + chunk + ": " + id);
                    } else {
                        GlowServer.logger.log(Level.WARNING, "Error loading entity in " + chunk + ": " + id, e);
                    }
                }
            }
        }
        */

        /*
        // read tile entities
        List<CompoundTag> storedTileEntities = levelTag.getCompoundList("TileEntities");
        for (CompoundTag tileEntityTag : storedTileEntities) {
            int tx = tileEntityTag.getInt("x");
            int ty = tileEntityTag.getInt("y");
            int tz = tileEntityTag.getInt("z");
            TileEntity tileEntity = chunk.getEntity(tx & 0xf, ty, tz & 0xf);
            if (tileEntity != null) {
                try {
                    tileEntity.loadNbt(tileEntityTag);
                } catch (Exception ex) {
                    String id = tileEntityTag.isString("id") ? tileEntityTag.getString("id") : "<missing>";
                    GlowServer.logger.log(Level.SEVERE, "Error loading tile entity at " + tileEntity.getBlock() + ": " + id, ex);
                }
            } else {
                String id = tileEntityTag.isString("id") ? tileEntityTag.getString("id") : "<missing>";
                GlowServer.logger.warning("Unknown tile entity at " + chunk.getWorld().getName() + "," + tx + "," + ty + "," + tz + ": " + id);
            }
        }
        */

        return true;
    }

    @Override
    public void write(LanternChunk chunk) throws IOException {
        int x = chunk.getX();
        int z = chunk.getZ();

        RegionFile region = this.cache.getRegionFileByChunk(x, z);

        int regionX = x & REGION_MASK;
        int regionZ = z & REGION_MASK;

        DataContainer root = new MemoryDataContainer();
        DataView levelTags = root.createView(LEVEL);

        // Core properties
        levelTags.set(VERSION, (byte) 1);
        levelTags.set(X, chunk.getX());
        levelTags.set(Z, chunk.getZ());
        levelTags.set(TERRAIN_POPULATED, (byte) (chunk.isPopulated() ? 1 : 0));
        levelTags.set(LIGHT_POPULATED, (byte) (chunk.isLightPopulated() ? 1 : 0));
        levelTags.set(LAST_UPDATE, 0L);

        // Chunk sections
        ChunkSectionSnapshot[] sections = chunk.getSectionSnapshots(true);
        List<DataView> sectionTags = Lists.newArrayList();

        for (byte i = 0; i < sections.length; ++i) {
            ChunkSectionSnapshot section = sections[i];
            if (section == null) {
                continue;
            }

            DataContainer sectionTag = new MemoryDataContainer();
            sectionTag.set(Y, i);

            byte[] rawTypes = new byte[section.types.length];
            short[] types = section.types;

            NibbleArray extTypes = null;
            NibbleArray data = new NibbleArray(rawTypes.length);

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
            sectionTag.set(BLOCKS, rawTypes);
            if (extTypes != null) {
                sectionTag.set(BLOCKS_EXTRA, extTypes.getPackedArray());
            }
            sectionTag.set(DATA, data.getPackedArray());
            sectionTag.set(BLOCK_LIGHT, section.lightFromBlock);
            sectionTag.set(SKY_LIGHT, section.lightFromSky);

            sectionTags.add(sectionTag);
        }

        levelTags.set(SECTIONS, sectionTags);
        levelTags.set(HEIGHT_MAP, chunk.getHeightMap());

        short[] biomes = chunk.getBiomes();

        byte[] biomes0 = new byte[biomes.length];
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

        levelTags.set(BIOMES, biomes0);
        if (biomes1 != null) {
            levelTags.set(BIOMES_EXTRA, biomes1);
        }

        try (NbtDataContainerOutputStream nbt = new NbtDataContainerOutputStream(region.getChunkDataOutputStream(regionX, regionZ))) {
            nbt.write(root);
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
            private File[] files;

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
                    DataInputStream is = this.region.getChunkDataInputStream(this.chunkX, this.chunkZ);
                    DataContainer data;

                    try (NbtDataContainerInputStream nbt = new NbtDataContainerInputStream(is)) {
                        data = nbt.read();
                    }

                    this.cached = false;
                    return data;
                } catch (IOException e) {
                    // This shouldn't happen
                    throw new IllegalStateException(e);
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
                    if (++this.regionFileIndex >= this.files.length) {
                        this.region = null;
                        this.done = true;
                        return false;
                    }
                    File nextRegionFile = this.files[this.regionFileIndex];
                    if (nextRegionFile.exists()) {
                        Matcher matcher = cache.getFilePattern().matcher(nextRegionFile.getName());
                        int regionX = Integer.parseInt(matcher.group(0));
                        int regionZ = Integer.parseInt(matcher.group(1));
                        try {
                            this.region = cache.getRegionFile(regionX, regionZ);
                        } catch (IOException e) {
                            LanternGame.log().error("Failed to read the region file ({};{}) in the world folder {}",
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
                this.files = cache.getRegionFiles();
                this.regionFileIndex = -1;
                this.regionChunkIndex = -1;
                this.region = null;
                this.cached = false;
                this.done = false;
            }
        };
    }

    @Override
    public CompletableFuture<Boolean> doesChunkExist(final Vector3i chunkCoords) {
        return LanternScheduler.getInstance().submitAsyncTask(() -> exists(chunkCoords.getX(), chunkCoords.getZ()));
    }

    @Override
    public CompletableFuture<Optional<DataContainer>> getChunkData(final Vector3i chunkCoords) {
        return LanternScheduler.getInstance().submitAsyncTask(() -> {
            int x = chunkCoords.getX();
            int z = chunkCoords.getZ();

            RegionFile region = cache.getRegionFileByChunk(x, z);
            int regionX = x & REGION_MASK;
            int regionZ = z & REGION_MASK;

            DataInputStream is = region.getChunkDataInputStream(regionX, regionZ);
            if (is == null) {
                return Optional.empty();
            }

            DataContainer data;
            try (NbtDataContainerInputStream nbt = new NbtDataContainerInputStream(is)) {
                data = nbt.read();
            }

            return Optional.of(data);
        });
    }

    @Override
    public WorldProperties getWorldProperties() {
        return this.properties;
    }

}
