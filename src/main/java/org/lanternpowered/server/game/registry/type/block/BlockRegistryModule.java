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
import static org.lanternpowered.server.block.LanternBlockType.DEFAULT_ITEM_TYPE_BUILDER;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.state.LanternBlockState;
import org.lanternpowered.server.block.type.BlockAir;
import org.lanternpowered.server.block.type.BlockBarrier;
import org.lanternpowered.server.block.type.BlockBedrock;
import org.lanternpowered.server.block.type.BlockDirt;
import org.lanternpowered.server.block.type.BlockGlass;
import org.lanternpowered.server.block.type.BlockGrass;
import org.lanternpowered.server.block.type.BlockLog;
import org.lanternpowered.server.block.type.BlockLog1;
import org.lanternpowered.server.block.type.BlockLog2;
import org.lanternpowered.server.block.type.BlockPlanks;
import org.lanternpowered.server.block.type.BlockSand;
import org.lanternpowered.server.block.type.BlockSapling;
import org.lanternpowered.server.block.type.BlockSlabBase;
import org.lanternpowered.server.block.type.BlockStone;
import org.lanternpowered.server.block.type.BlockStoneSlab1;
import org.lanternpowered.server.block.type.BlockStoneSlab2;
import org.lanternpowered.server.block.type.BlockStoneSlabBase;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.registry.AlternateCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class BlockRegistryModule implements BlockRegistry, AlternateCatalogRegistryModule<BlockType> {

    private static final BlockRegistryModule INSTANCE = new BlockRegistryModule();

    public static BlockRegistryModule get() {
        return INSTANCE;
    }

    @RegisterCatalog(BlockTypes.class)
    private final Map<String, BlockType> blockTypes = new HashMap<>();

    private final Short2ObjectMap<BlockType> blockTypeByInternalId = new Short2ObjectOpenHashMap<>();
    private final Object2ShortMap<BlockType> internalIdByBlockType = new Object2ShortOpenHashMap<>();

    private final Short2ObjectMap<BlockState> blockStateByPackedType = new Short2ObjectOpenHashMap<>();
    private final Object2ShortMap<BlockState> packedTypeByBlockState = new Object2ShortOpenHashMap<>();

    // The counter for custom block ids. (Non vanilla ones.)
    private int blockIdCounter = 1024;

    private BlockRegistryModule() {
    }

    @Override
    public int getBlockStatesCount() {
        return this.blockStateByPackedType.size();
    }

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

    private void register0(int internalId, LanternBlockType blockType, Function<BlockState, Byte> stateToDataConverter) {
        checkNotNull(stateToDataConverter, "stateToDataConverter");
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
        Byte2ObjectMap<BlockState> usedValues = new Byte2ObjectOpenHashMap<>();
        int internalStateIdBase = (internalId & 0xfff) << 4;
        for (BlockState blockState : blockType.getBlockStateBase().getBlockStates()) {
            if (((LanternBlockState) blockState).isExtended()) {
                continue;
            }
            byte value = checkNotNull(stateToDataConverter.apply(blockState));
            if (usedValues.containsKey(value)) {
                throw new IllegalStateException("The data value " + value + " for state '" + blockState.getId() +
                        "' is already used by '" + usedValues.get(value).getId() + "'");
            }
            usedValues.put(value, blockState);
            final short internalStateId =  (short) (internalStateIdBase | value & 0xf);
            this.blockStateByPackedType.put(internalStateId, blockState);
            this.packedTypeByBlockState.put(blockState, internalStateId);
        }
        BlockState defaultBlockState = blockType.getDefaultState();
        for (byte b = 0; b <= 0xf; b++) {
            if (!usedValues.containsKey(b)) {
                final short internalStateId = (short) (internalStateIdBase | b & 0xf);
                this.blockStateByPackedType.put(internalStateId, defaultBlockState);
            }
        }
        for (BlockState blockState : blockType.getBlockStateBase().getBlockStates()) {
            if (!((LanternBlockState) blockState).isExtended()) {
                continue;
            }
            BlockState blockState1 = blockType.removeExtendedState(blockState);
            this.packedTypeByBlockState.put(blockState, checkNotNull(this.packedTypeByBlockState.get(blockState1)));
        }
        BlockStateRegistryModule blockStateRegistryModule = Lantern.getRegistry()
                .getRegistryModule(BlockStateRegistryModule.class).get();
        blockType.getAllStates().forEach(blockStateRegistryModule::put);
        blockType.getItem().ifPresent(itemType -> ItemRegistryModule.get().register(internalId, itemType));
    }

    @Override
    public void register(int internalId, BlockType blockType, Function<BlockState, Byte> stateToDataConverter) {
        this.register0(internalId, (LanternBlockType) blockType, stateToDataConverter);
    }

    @Override
    public void register(int internalId, BlockType blockType) {
        LanternBlockType blockType0 = (LanternBlockType) checkNotNull(blockType, "blockType");
        checkState(blockType0.getBlockStateBase().getBlockStates().stream()
                        .filter(s -> !((LanternBlockState) s).isExtended()).count() <= 1,
                "You cannot register a blockType with more then one state with this method.");
        this.register0(internalId, blockType0, blockState -> (byte) 0);
    }

    @Override
    public void register(BlockType blockType, Function<BlockState, Byte> stateToDataConverter) {
        this.register(this.nextInternalId(), blockType, stateToDataConverter);
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
        return ImmutableSet.copyOf(this.blockTypes.values());
    }

    @Override
    public void registerDefaults() {
        this.register(0, new BlockAir("minecraft", "air", null));
        this.register(1, new BlockStone("minecraft", "stone", DEFAULT_ITEM_TYPE_BUILDER), blockState ->
                (byte) blockState.getTraitValue(BlockStone.TYPE).get().getInternalId());
        this.register(2, new BlockGrass("minecraft", "grass", DEFAULT_ITEM_TYPE_BUILDER));
        this.register(3, new BlockDirt("minecraft", "dirt", DEFAULT_ITEM_TYPE_BUILDER), blockState ->
                (byte) blockState.getTraitValue(BlockDirt.TYPE).get().getInternalId());
        this.register(5, new BlockPlanks("minecraft", "planks", DEFAULT_ITEM_TYPE_BUILDER), blockState ->
                (byte) blockState.getTraitValue(BlockPlanks.TYPE).get().getInternalId());
        this.register(6, new BlockSapling("minecraft", "sapling", DEFAULT_ITEM_TYPE_BUILDER), blockState -> {
            final byte treeType = (byte) blockState.getTraitValue(BlockSapling.TYPE).get().getInternalId();
            final byte stage = blockState.getTraitValue(BlockSapling.STAGE).get().byteValue();
            return (byte) (stage << 3 | treeType);
        });
        this.register(7, new BlockBedrock("minecraft", "bedrock", DEFAULT_ITEM_TYPE_BUILDER));
        this.register(20, new BlockGlass("minecraft", "glass", DEFAULT_ITEM_TYPE_BUILDER));
        this.register(12, new BlockSand("minecraft", "sand", DEFAULT_ITEM_TYPE_BUILDER), blockState ->
                (byte) blockState.getTraitValue(BlockSand.TYPE).get().getInternalId());
        this.register(17, new BlockLog1("minecraft", "log", DEFAULT_ITEM_TYPE_BUILDER), blockState -> {
            final byte treeType = (byte) blockState.getTraitValue(BlockLog1.TYPE).get().getInternalId();
            final byte axis = (byte) blockState.getTraitValue(BlockLog.AXIS).get().getInternalId();
            return (byte) (axis << 2 | treeType);
        });
        this.register(43, new BlockStoneSlab1("minecraft", "double_stone_slab", BlockSlabBase.ITEM_TYPE_BUILDER, true), blockState -> {
            final byte slabType = (byte) blockState.getTraitValue(BlockStoneSlab1.TYPE).get().getInternalId();
            final byte seamless = (byte) (blockState.getTraitValue(BlockStoneSlabBase.SEAMLESS).get() ? 1 : 0);
            return (byte) (seamless << 3 | slabType);
        });
        this.register(44, new BlockStoneSlab1("minecraft", "stone_slab", BlockSlabBase.ITEM_TYPE_BUILDER, false), blockState -> {
            final byte slabType = (byte) blockState.getTraitValue(BlockStoneSlab1.TYPE).get().getInternalId();
            final byte portion = (byte) blockState.getTraitValue(BlockSlabBase.PORTION).get().getInternalId();
            return (byte) (portion << 3 | slabType);
        });
        this.register(162, new BlockLog2("minecraft", "log2", DEFAULT_ITEM_TYPE_BUILDER), blockState -> {
            final byte treeType = (byte) (blockState.getTraitValue(BlockLog2.TYPE).get().getInternalId() - 4);
            final byte axis = (byte) blockState.getTraitValue(BlockLog.AXIS).get().getInternalId();
            return (byte) (axis << 2 | treeType);
        });
        this.register(166, new BlockBarrier("minecraft", "barrier", DEFAULT_ITEM_TYPE_BUILDER));
        this.register(181, new BlockStoneSlab2("minecraft", "double_stone_slab2", BlockSlabBase.ITEM_TYPE_BUILDER, true), blockState -> {
            final byte slabType = (byte) (blockState.getTraitValue(BlockStoneSlab2.TYPE).get().getInternalId() - 8);
            final byte seamless = (byte) (blockState.getTraitValue(BlockStoneSlabBase.SEAMLESS).get() ? 1 : 0);
            return (byte) (seamless << 3 | slabType);
        });
        this.register(182, new BlockStoneSlab2("minecraft", "stone_slab2", BlockSlabBase.ITEM_TYPE_BUILDER, false), blockState -> {
            final byte slabType = (byte) (blockState.getTraitValue(BlockStoneSlab2.TYPE).get().getInternalId() - 8);
            final byte portion = (byte) blockState.getTraitValue(BlockSlabBase.PORTION).get().getInternalId();
            return (byte) (portion << 3 | slabType);
        });
    }

}
