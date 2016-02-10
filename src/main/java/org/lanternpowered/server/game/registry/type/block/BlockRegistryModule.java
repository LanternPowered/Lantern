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
package org.lanternpowered.server.game.registry.type.block;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TObjectShortHashMap;
import gnu.trove.map.hash.TShortObjectHashMap;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.type.BlockAir;
import org.lanternpowered.server.block.type.BlockBedrock;
import org.lanternpowered.server.block.type.BlockDirt;
import org.lanternpowered.server.block.type.BlockGrass;
import org.lanternpowered.server.block.type.BlockStone;
import org.lanternpowered.server.data.type.LanternDirtType;
import org.lanternpowered.server.data.type.LanternStoneType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.registry.AlternateCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

public final class BlockRegistryModule implements BlockRegistry, AlternateCatalogRegistryModule<BlockType> {

    @RegisterCatalog(BlockTypes.class) private final Map<String, BlockType> blockTypes = Maps.newHashMap();

    private final TShortObjectMap<BlockType> blockTypeByInternalId = new TShortObjectHashMap<>();
    private final TObjectShortMap<BlockType> internalIdByBlockType = new TObjectShortHashMap<>();

    private final TShortObjectMap<BlockState> blockStateByPackedType = new TShortObjectHashMap<>();
    private final TObjectShortMap<BlockState> packedTypeByBlockState = new TObjectShortHashMap<>();

    // The counter for custom block ids. (Non vanilla ones.)
    private int blockIdCounter = 1024;

    @Override
    public Map<String, BlockType> provideCatalogMap() {
        Map<String, BlockType> mappings = Maps.newHashMap();
        for (Map.Entry<String, BlockType> entry : this.blockTypes.entrySet()) {
            String identifier = entry.getKey();
            if (identifier.startsWith("minecraft:")) {
                mappings.put(identifier.replace("minecraft:", ""), entry.getValue());
            }
        }
        return mappings;
    }

    private void register0(int internalId, LanternBlockType blockType, @Nullable BiFunction<Byte, BlockState, BlockState> dataToStateConverter) {
        final String id = checkNotNull(blockType, "blockType").getId().toLowerCase();
        checkState(!this.blockTypes.containsValue(checkNotNull(blockType, "blockType")), "The block type (" + id + ") is already registered.");
        checkState(!this.blockTypes.containsKey(id), "The identifier (" + id + ") is already used.");
        checkState(internalId >= 0, "Internal id cannot be negative.");
        checkState(internalId <= 0xfff, "Exceeded the internal id limit. (" + 0xfff + ")");
        final short internalId0 = (short) internalId;
        checkState(!this.blockTypeByInternalId.containsKey(internalId0), "Internal id (" + internalId + ") is already used!");
        this.blockTypes.put(id, blockType);
        this.blockTypeByInternalId.put(internalId0, blockType);
        this.internalIdByBlockType.put(blockType, internalId0);
        if (dataToStateConverter != null) {
            int internalStateIdBase = (internalId & 0xfff) << 4;
            for (byte b = 0; b <= 0xf; b++) {
                BlockState blockState = dataToStateConverter.apply(b, blockType.getDefaultState());
                if (blockState == null) {
                    blockState = blockType.getDefaultState();
                }
                short internalStateId = (short) (internalStateIdBase | b & 0xf);
                this.blockStateByPackedType.put(internalStateId, blockState);
                this.packedTypeByBlockState.put(blockState, internalStateId);
            }
        } else {
            BlockState state = blockType.getDefaultState();
            short internalStateId = (short) ((internalId & 0xfff) << 4);
            this.blockStateByPackedType.put(internalStateId, state);
            this.packedTypeByBlockState.put(state, internalStateId);
        }
    }

    @Override
    public void register(int internalId, BlockType blockType, BiFunction<Byte, BlockState, BlockState> dataToStateConverter) {
        this.register0(internalId, (LanternBlockType) blockType, checkNotNull(dataToStateConverter, "dataToStateConverter"));
    }

    @Override
    public void register(int internalId, BlockType blockType) {
        LanternBlockType blockType0 = (LanternBlockType) checkNotNull(blockType, "blockType");
        checkState(blockType0.getBlockStateBase().getBlockStates().size() <= 1,
                "You cannot register a blockType with more then one state with this method.");
        this.register0(internalId, blockType0, null);
    }

    @Override
    public void register(BlockType blockType, BiFunction<Byte, BlockState, BlockState> dataToStateConverter) {
        this.register(this.nextInternalId(), blockType, checkNotNull(dataToStateConverter, "dataToStateConverter"));
    }

    @Override
    public void register(BlockType catalogType) {
        this.register(this.nextInternalId(), catalogType);
    }

    private int nextInternalId() {
        int internalId;
        do {
            internalId = this.blockIdCounter++;
        } while (this.blockTypeByInternalId.containsKey((short) internalId));
        return internalId;
    }

    @Override
    public Optional<BlockState> getStateByInternalId(int internalId) {
        return Optional.ofNullable(this.blockStateByPackedType.get((short) ((internalId & 0xfff) << 4)));
    }

    @Override
    public Optional<BlockState> getStateByInternalIdAndData(int internalId, byte data) {
        return Optional.ofNullable(this.blockStateByPackedType.get((short) (((internalId & 0xfff) << 4) | (data & 0xf))));
    }

    @Override
    public Optional<BlockState> getStateByTypeAndData(BlockType blockType, byte data) {
        return Optional.ofNullable(this.blockStateByPackedType.get(
                (short) ((this.packedTypeByBlockState.get(blockType.getDefaultState()) & 0xfff0) | (data & 0xf))));
    }

    @Override
    public Optional<BlockState> getStateByInternalIdAndData(int internalIdAndData) {
        return Optional.ofNullable(this.blockStateByPackedType.get((short) (internalIdAndData & 0xffff)));
    }

    @Override
    public byte getStateData(BlockState blockState) {
        return (byte) (this.packedTypeByBlockState.get(checkNotNull(blockState, "blockState")) & 0xf);
    }

    @Override
    public short getStateInternalId(BlockState blockState) {
        return (short) (this.packedTypeByBlockState.get(checkNotNull(blockState, "blockState")) >> 4);
    }

    @Override
    public short getStateInternalIdAndData(BlockState blockState) {
        return this.packedTypeByBlockState.get(checkNotNull(blockState, "blockState"));
    }

    @Override
    public int getPackedVersion(int internalId, byte data) {
        return ((internalId & 0xfff) << 4) | (data & 0xf);
    }

    @Override
    public Optional<BlockType> getById(String id) {
        if (checkNotNull(id, "identifier").indexOf(':') == -1) {
            id = "minecraft:" + id;
        }
        return Optional.ofNullable(this.blockTypes.get(id.toLowerCase()));
    }

    @Override
    public Collection<BlockType> getAll() {
        return ImmutableList.copyOf(this.blockTypes.values());
    }

    @Override
    public void registerDefaults() {
        this.register(0, new BlockAir("minecraft", "air"));
        this.register(1, new BlockStone("minecraft", "stone"), (data, state) -> state.withTrait(BlockStone.TYPE,
                Arrays.stream(LanternStoneType.values()).filter(t -> t.getInternalId() == data).findFirst().orElse(LanternStoneType.STONE)).get());
        this.register(2, new BlockGrass("minecraft", "grass"), (data, state) -> state);
        this.register(3, new BlockDirt("minecraft", "dirt"), (data, state) -> state.withTrait(BlockDirt.TYPE,
                Arrays.stream(LanternDirtType.values()).filter(t -> t.getInternalId() == data).findFirst().orElse(LanternDirtType.DIRT)).get());
        this.register(7, new BlockBedrock("minecraft", "bedrock"));
    }

}
