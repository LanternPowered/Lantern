/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.data.io.anvil;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;

import org.lanternpowered.server.data.io.ChunkIOService;
import org.lanternpowered.server.data.io.nbt.NbtDataContainerInputStream;
import org.lanternpowered.server.data.io.nbt.NbtDataContainerOutputStream;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.util.NibbleArray;
import org.lanternpowered.server.world.chunk.LanternChunk;
import org.lanternpowered.server.world.chunk.LanternChunk.ChunkSection;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.world.storage.ChunkDataStream;
import org.spongepowered.api.world.storage.WorldProperties;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import static org.lanternpowered.server.data.io.anvil.RegionFileCache.REGION_FILE_PATTERN;

public class AnvilChunkIOService implements ChunkIOService {

    private static final int REGION_SIZE = 32;
    private static final int REGION_AREA = REGION_SIZE * REGION_SIZE;
    private static final int REGION_MASK = REGION_SIZE - 1;

    private static final DataQuery LEVEL = DataQuery.of("Level");
    private static final DataQuery SECTIONS = DataQuery.of("Sections");
    private static final DataQuery X = DataQuery.of("xPos");
    private static final DataQuery Z = DataQuery.of("zPos");
    private static final DataQuery Y = DataQuery.of("Y");
    private static final DataQuery BLOCKS = DataQuery.of("Blocks");
    private static final DataQuery EXTRA_TYPES = DataQuery.of("Add");
    private static final DataQuery DATA = DataQuery.of("Data");
    private static final DataQuery BLOCK_LIGHT = DataQuery.of("BlockLight");
    private static final DataQuery SKY_LIGHT = DataQuery.of("SkyLight");
    private static final DataQuery POPULATED = DataQuery.of("TerrainPopulated");
    private static final DataQuery BIOMES = DataQuery.of("Biomes");
    // A extra tag for the biomes to support the custom biomes
    private static final DataQuery BIOMES_EXTRA = DataQuery.of("BiomesExtra");
    private static final DataQuery HEIGHT_MAP = DataQuery.of("HeightMap");
    private static final DataQuery LAST_UPDATE = DataQuery.of("LastUpdate");

    private final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    private final RegionFileCache cache;
    private final File dir;

    // TODO: Consider the session.lock file

    public AnvilChunkIOService(File dir) {
        this.cache = new RegionFileCache(dir, ".mca");
        this.dir = dir;
    }

    public boolean exists(int x, int z) throws IOException {
        RegionFile region = this.cache.getRegionFile(x, z);

        int regionX = x & REGION_MASK;
        int regionZ = z & REGION_MASK;

        return region.hasChunk(regionX, regionZ);
    }

    @Override
    public boolean read(LanternChunk chunk) throws IOException {
        int x = chunk.getX();
        int z = chunk.getZ();

        RegionFile region = this.cache.getRegionFile(x, z);
        int regionX = x & REGION_MASK;
        int regionZ = z & REGION_MASK;

        if (!region.hasChunk(regionX, regionZ)) {
            return false;
        }

        DataInputStream is = region.getChunkDataInputStream(regionX, regionZ);

        DataView levelTag;
        try (NbtDataContainerInputStream nbt = new NbtDataContainerInputStream(is)) {
            levelTag = nbt.read().getView(LEVEL).get();
        }

        // read the vertical sections
        List<DataView> sectionList = levelTag.getViewList(SECTIONS).get();
        ChunkSection[] sections = new ChunkSection[16];

        for (DataView sectionTag : sectionList) {
            int y = (int) sectionTag.getInt(Y).get();
            byte[] rawTypes = (byte[]) sectionTag.get(BLOCKS).get();

            byte[] extTypes = sectionTag.contains(EXTRA_TYPES) ? (byte[]) sectionTag.get(EXTRA_TYPES).get() : null;
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
        chunk.setPopulated(levelTag.getBoolean(POPULATED).orElse(false));

        if (levelTag.contains(BIOMES)) {
            byte[] biomes = (byte[]) levelTag.get(BIOMES).get();
            byte[] biomesExtra = (byte[]) (levelTag.contains(BIOMES_EXTRA) ? levelTag.get(BIOMES_EXTRA).get() : null);
            short[] newBiomes = new short[biomes.length];
            for (int i = 0; i < biomes.length; i++) {
                newBiomes[i] = (short) ((biomesExtra == null ? 0 : biomesExtra[i]) << 8 | biomes[i]);
            }
            chunk.initializeBiomes(newBiomes);
        }

        if (levelTag.contains(HEIGHT_MAP)) {
            Object heightMap = levelTag.get(HEIGHT_MAP).get();
            if (heightMap instanceof int[]) {
                chunk.setHeightMap((int[]) heightMap);
            } else {
                chunk.automaticHeightMap();
            }
        }

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

        RegionFile region = this.cache.getRegionFile(x, z);

        int regionX = x & (REGION_SIZE - 1);
        int regionZ = z & (REGION_SIZE - 1);

        DataContainer root = new MemoryDataContainer();
        DataView levelTags = root.createView(LEVEL);

        // Core properties
        levelTags.set(X, chunk.getX());
        levelTags.set(Z, chunk.getZ());
        levelTags.set(POPULATED, chunk.isPopulated());
        levelTags.set(LAST_UPDATE, 0L);

        // Chunk sections
        ChunkSection[] sections = chunk.getSections();
        List<DataView> sectionTags = Lists.newArrayList();

        for (byte i = 0; i < sections.length; ++i) {
            ChunkSection section = sections[i];
            if (section == null) {
                continue;
            }

            DataContainer sectionTag = new MemoryDataContainer();
            sectionTag.set(Y, i);

            byte[] rawTypes = new byte[section.types.length()];
            short[] types = section.types.getArray();

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
                sectionTag.set(EXTRA_TYPES, extTypes.getPackedArray());
            }
            sectionTag.set(DATA, data.getPackedArray());
            sectionTag.set(BLOCK_LIGHT, section.lightFromBlock.getPackedArray());
            sectionTag.set(SKY_LIGHT, section.lightFromSky.getPackedArray());

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
        this.service.shutdown();
        this.cache.clear();
    }

    @Override
    public ChunkDataStream getGeneratedChunks() {
        return new ChunkDataStream() {

            // All the region files
            private File[] files;

            // The current region file that we opened
            private RegionFile region;

            // The coordinates of the chunk inside the region
            private int chunkX;
            private int chunkZ;

            // The next index of the chunk in the region file
            private int regionChunkIndex = -1;
            // The next index that we are in the file array
            private int regionFileIndex = -1;

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
                    DataInputStream is = region.getChunkDataInputStream(this.chunkX, this.chunkZ);
                    DataContainer data;

                    try (NbtDataContainerInputStream nbt = new NbtDataContainerInputStream(is)) {
                        data = nbt.read();
                    }

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
                if (this.regionChunkIndex != -1) {
                    return true;
                }
                // Try first to search for more chunks in the current region
                while (true) {
                    if (this.region != null) {
                        while (++this.regionChunkIndex < REGION_AREA) {
                            this.chunkX = this.regionChunkIndex / REGION_SIZE;
                            this.chunkZ = this.regionChunkIndex % REGION_SIZE;
                            if (this.region.hasChunk(this.chunkX, this.chunkZ)) {
                                return true;
                            }
                        }
                    }
                    // There no chunk available in the current region,
                    // reset the chunk index for the next one
                    this.regionChunkIndex = -1;
                    // There was no chunk present in the current region,
                    // try the next region
                    this.regionFileIndex++;
                    if (this.regionFileIndex >= this.files.length) {
                        this.done = true;
                        return false;
                    }
                    File nextRegionFile = this.files[this.regionFileIndex];
                    if (nextRegionFile.exists()) {
                        Matcher matcher = REGION_FILE_PATTERN.matcher(nextRegionFile.getName());
                        int regionX = Integer.parseInt(matcher.group(0));
                        int regionZ = Integer.parseInt(matcher.group(1));
                        try {
                            this.region = cache.getRegionFile(regionX << 5, regionZ << 5);
                        } catch (IOException e) {
                            LanternGame.log().error("Failed to read the region file ({};{}) in the world folder {}",
                                    regionX, regionZ, dir.getPath(), e);
                        }
                    }
                }
            }

            @Override
            public int available() {
                // TODO: Not sure how we will be able to do this without opening all
                // the region files
                return 0;
            }

            @Override
            public void reset() {
                this.files = dir.listFiles((file) -> REGION_FILE_PATTERN.matcher(file.getName()).matches());
                this.regionFileIndex = -1;
                this.regionChunkIndex = -1;
                this.region = null;
                this.done = false;
            }

        };
    }

    @Override
    public ListenableFuture<Boolean> doesChunkExist(final Vector3i chunkCoords) {
        return this.service.submit(() -> exists(chunkCoords.getX(), chunkCoords.getZ()));
    }

    @Override
    public ListenableFuture<Optional<DataContainer>> getChunkData(final Vector3i chunkCoords) {
        return this.service.submit(new Callable<Optional<DataContainer>>() {

            @Override
            public Optional<DataContainer> call() throws Exception {
                int x = chunkCoords.getX();
                int z = chunkCoords.getZ();

                RegionFile region = cache.getRegionFile(x, z);
                int regionX = x & (REGION_SIZE - 1);
                int regionZ = z & (REGION_SIZE - 1);

                if (!region.hasChunk(regionX, regionZ)) {
                    return Optional.empty();
                }

                DataInputStream is = region.getChunkDataInputStream(regionX, regionZ);
                DataContainer data;

                try (NbtDataContainerInputStream nbt = new NbtDataContainerInputStream(is)) {
                    data = nbt.read();
                }

                return Optional.of(data);
            }

        });
    }

    @Override
    public WorldProperties getWorldProperties() {
        // TODO Auto-generated method stub
        return null;
    }

}
