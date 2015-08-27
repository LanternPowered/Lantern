package org.lanternpowered.server.world.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nullable;

import org.lanternpowered.server.data.io.ChunkIOService;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.util.gen.ShortArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.ShortArrayMutableBlockBuffer;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.chunk.LanternChunk.ChunkSection;
import org.lanternpowered.server.world.chunk.tickets.LanternLoadingTickets;
import org.lanternpowered.server.world.chunk.tickets.TicketsProvider;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.gen.BiomeGenerator;
import org.spongepowered.api.world.gen.GeneratorPopulator;
import org.spongepowered.api.world.gen.WorldGenerator;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_AREA_SIZE;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_SIZE;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_SECTIONS;
import static org.lanternpowered.server.world.chunk.LanternChunkLayout.CHUNK_SECTION_SIZE;

public class LanternChunkManager {

    // The map with chunks that are currently loaded
    private final ConcurrentMap<Vector2i, LanternChunk> chunks = Maps.newConcurrentMap();

    private final ChunkIOService chunkIOService;
    private final LanternLoadingTickets tickets;
    private final LanternWorld world;

    private WorldGenerator worldGenerator;

    public LanternChunkManager(LanternWorld world, ChunkIOService chunkIOService, TicketsProvider provider) {
        this.tickets = new LanternLoadingTickets(provider);
        this.chunkIOService = chunkIOService;
        this.world = world;
    }

    public ImmutableSet<Chunk> getLoadedChunks() {
        ImmutableSet.Builder<Chunk> builder = ImmutableSet.builder();
        for (LanternChunk chunk : this.chunks.values()) {
            if (chunk.isLoaded()) {
                builder.add(chunk);
            }
        }
        return builder.build();
    }

    /**
     * Gets the loading tickets of the chunk manager.
     * 
     * @return the loading tickets
     */
    public LanternLoadingTickets getLoadingTickets() {
        return this.tickets;
    }

    /**
     * Gets a chunk for the coordinates, may not be loaded yet.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the chunk
     */
    @Nullable
    public LanternChunk getChunk(Vector3i coords) {
        return this.getChunk(coords.toVector2(true));
    }

    /**
     * Gets a chunk for the coordinates, may not be loaded yet.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the chunk
     */
    @Nullable
    public LanternChunk getChunk(Vector2i coords) {
        return this.chunks.get(coords);
    }

    /**
     * Gets a chunk for the coordinates, may not be loaded yet.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the chunk
     */
    @Nullable
    public LanternChunk getChunk(int x, int z) {
        return this.getChunk(new Vector2i(x, z));
    }

    /**
     * Gets a chunk safely (new one will be created) for the coordinates, may
     * not be loaded yet.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the chunk
     */
    public LanternChunk getChunkSafely(int x, int z) {
        Vector2i key = new Vector2i(x, z);

        if (this.chunks.containsKey(key)) {
            return this.chunks.get(key);
        } else {
            LanternChunk chunk = new LanternChunk(this.world, x, z);
            LanternChunk prev = this.chunks.putIfAbsent(key, chunk);
            return prev == null ? chunk : prev;
        }
    }

    /**
     * Check whether a chunk has tickets on it preventing it from being
     * unloaded.
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return whether the chunk is in use
     */
    public boolean isChunkInUse(int x, int z) {
        return this.tickets.hasTicket(x, z);
    }

    public boolean load(int x, int z, boolean generate) {
        return false;
    }

    boolean unload(LanternChunk chunk) {
        return false;
    }

    boolean load(LanternChunk chunk, boolean generate) {
        // Try to load chunk
        try {
            if (this.chunkIOService.read(chunk)) {
                LanternGame.get().getEventManager().post(SpongeEventFactory.createChunkLoad(LanternGame.get(), chunk));
                return true;
            }
        } catch (Exception e) {
            LanternGame.log().error("Error while loading chunk (" + chunk.getX() + "," + chunk.getZ() + ")", e);
            // an error in chunk reading may have left the chunk in an invalid state
            // (i.e. double initialization errors), so it's forcibly unloaded here
            chunk.unloadChunk();
        }
        // Stop here if we can't generate
        if (!generate) {
            chunk.initializeEmpty();
            return false;
        }
        try {
            this.generate(chunk);
        } catch (Throwable e) {
            LanternGame.log().error("Error while generating chunk (" + chunk.getX() + "," + chunk.getZ() + ")", e);
            return false;
        }
        LanternGame.get().getEventManager().post(SpongeEventFactory.createChunkLoad(LanternGame.get(), chunk));
        return true;
    }

    void generate(LanternChunk chunk) {
        ShortArrayMutableBiomeBuffer biomes = new ShortArrayMutableBiomeBuffer(Vector2i.ZERO, CHUNK_AREA_SIZE);

        BiomeGenerator biomeGenerator = this.worldGenerator.getBiomeGenerator();
        biomeGenerator.generateBiomes(biomes);

        chunk.initializeBiomes(biomes.detach());

        ShortArrayMutableBlockBuffer blocks = new ShortArrayMutableBlockBuffer(Vector3i.ZERO, CHUNK_SIZE);
        ImmutableBiomeArea biomeBuffer = biomes.getImmutableBiomeCopy();

        GeneratorPopulator generator = this.worldGenerator.getBaseGeneratorPopulator();
        generator.populate(this.world, blocks, biomeBuffer);

        // Apply the generator populators to complete the blockBuffer
        for (GeneratorPopulator populator : this.worldGenerator.getGeneratorPopulators()) {
            populator.populate(this.world, blocks, biomeBuffer);
        }

        // Get unique biomes to determine what generator populators to run
        List<BiomeType> uniqueBiomes = Lists.newArrayList();
        BiomeType biome;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                biome = biomes.getBiome(x, z);
                if (!uniqueBiomes.contains(biome)) {
                    uniqueBiomes.add(biome);
                }
            }
        }
        
        // Run our generator populators, checking for overrides from the generator
        for (BiomeType type: uniqueBiomes) {
            for (GeneratorPopulator populator : type.getGeneratorPopulators()) {
                populator.populate(this.world, blocks, biomeBuffer);
            }
        }

        short[] blocksArray = blocks.getArray();

        ChunkSection[] sections = new ChunkSection[CHUNK_SECTIONS];
        for (int sy = 0; sy < sections.length; ++sy) {
            int y = sy << 4;
            int start = blocks.getIndex(0, y, 0);
            int end = blocks.getIndex(15, y | 15, 15);

            short[] sectionBlocks = Arrays.copyOfRange(blocksArray, start, end + 1);
            sections[sy] = new ChunkSection(sectionBlocks);
        }

        chunk.initializeSections(sections);
    }
}
