/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.world.chunk;

import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTION_VOLUME;

import org.lanternpowered.server.block.state.LanternBlockState;
import org.lanternpowered.server.game.registry.InternalIDRegistries;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.util.palette.AbstractGlobalPalette;
import org.lanternpowered.server.util.palette.LanternPaletteBasedArray;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.util.Tuple;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("ConstantConditions")
public class ChunkBlockStateArray extends LanternPaletteBasedArray<BlockState> {

    /**
     * This is the integer id that is always assigned to
     * the {@link BlockState} of {@link BlockTypes#AIR}.
     */
    public static final int AIR_ID = 0;

    /**
     * The {@link DataQuery} of the palette
     * data within the {@link DataView}.
     */
    private static final DataQuery PALETTE_QUERY = DataQuery.of("Palette");

    /**
     * The {@link DataQuery} of the block state
     * data within the {@link DataView}.
     */
    private static final DataQuery BLOCK_STATES_QUERY = DataQuery.of("BlockStates");

    /**
     * The global palette of {@link BlockState}s.
     */
    static class LanternGlobalPalette implements AbstractGlobalPalette<BlockState> {

        static final LanternGlobalPalette INSTANCE = new LanternGlobalPalette();

        @Override
        public int getId(BlockState object) {
            return BlockRegistryModule.get().getStateInternalId(object);
        }

        @Override
        public Optional<BlockState> get(int id) {
            return BlockRegistryModule.get().getStateByInternalId(id);
        }

        @Override
        public Collection<BlockState> getEntries() {
            return Sponge.getRegistry().getAllOf(BlockState.class);
        }

        @Override
        public int size() {
            // Reserve all the mappings in the block ids map
            return InternalIDRegistries.BLOCK_STATES_ASSIGNED;
        }
    }

    /**
     * Deserializes the {@link ChunkBlockStateArray}
     * from the given {@link DataView}.
     *
     * @param dataView The data view
     * @return The deserialized block state array
     */
    public static ChunkBlockStateArray deserializeFrom(DataView dataView) {
        final List<BlockState> palette = dataView.getViewList(PALETTE_QUERY).get().stream()
                .map(LanternBlockState::deserialize)
                .collect(Collectors.toList());
        final long[] rawBackingData = (long[]) dataView.get(BLOCK_STATES_QUERY).get();
        return new ChunkBlockStateArray(palette, rawBackingData);
    }

    /**
     * Serializes the {@link ChunkBlockStateArray} into the given {@link DataView}.
     *
     * @param dataView The data view
     * @param blockStatesArray The block state array to serialize
     */
    public static void serializeTo(DataView dataView, ChunkBlockStateArray blockStatesArray) {
        final Tuple<Collection<BlockState>, long[]> serialized = blockStatesArray.serialize();
        dataView.set(BLOCK_STATES_QUERY, serialized.getSecond());
        dataView.set(PALETTE_QUERY, serialized.getFirst().stream()
                .map(LanternBlockState::serialize)
                .collect(Collectors.toList()));
    }

    /**
     * The default block state of air.
     */
    private static final BlockState AIR = BlockTypes.AIR.getDefaultState();

    private ChunkBlockStateArray(ChunkBlockStateArray other) {
        super(other);
    }

    ChunkBlockStateArray(int capacity) {
        super(LanternGlobalPalette.INSTANCE, capacity);
    }

    ChunkBlockStateArray(int[] stateIds) {
        super(LanternGlobalPalette.INSTANCE, stateIds);
    }

    private ChunkBlockStateArray(Collection<BlockState> palette, long[] rawBackingData) {
        super(LanternGlobalPalette.INSTANCE, palette, CHUNK_SECTION_VOLUME, rawBackingData);
    }

    @Override
    protected void init(InternalPalette<BlockState> internalPalette) {
        internalPalette.assign(AIR_ID, AIR);
    }

    @Override
    public ChunkBlockStateArray copy() {
        return new ChunkBlockStateArray(this);
    }
}
