package org.lanternpowered.server.block;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import gnu.trove.TCollections;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TObjectShortHashMap;
import gnu.trove.map.hash.TShortObjectHashMap;

import org.lanternpowered.server.block.state.LanternBlockState;
import org.lanternpowered.server.catalog.LanternCatalogTypeRegistry;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

public class LanternBlockRegistry extends LanternCatalogTypeRegistry<BlockType> implements BlockRegistry {

    private final TShortObjectMap<BlockState> blockStatesById = TCollections.synchronizedMap(new TShortObjectHashMap<BlockState>());
    private final TObjectShortMap<BlockState> idsByBlockState = TCollections.synchronizedMap(new TObjectShortHashMap<BlockState>());
 
    private final TShortObjectMap<BlockType> blocksById = TCollections.synchronizedMap(new TShortObjectHashMap<BlockType>());
    private final TObjectShortMap<BlockType> idsByBlock = TCollections.synchronizedMap(new TObjectShortHashMap<BlockType>());

    // The counter for custom block ids. (Non vanilla ones.)
    private final AtomicInteger blockIdCounter = new AtomicInteger(1024);

    private void register0(int internalId, LanternBlockType blockType, Function<BlockState, Byte> dataValueGenerator) {
        checkNotNull(blockType, "blockType");
        checkState(internalId >= 0, "Block id cannot be negative.");
        checkState(internalId <= 0xfff, "Exceeded the block id limit. (" + 0xfff + ")");
        short internalId0 = (short) internalId;
        checkState(!this.blocksById.containsKey(internalId0), "Block id already present! (" + internalId + ")");
        super.register(blockType);
        this.blocksById.put(internalId0, blockType);
        this.idsByBlock.put(blockType, internalId0);
        if (dataValueGenerator != null && blockType.blockStateBase.getBlockStates().size() > 1) {
            for (BlockState state : blockType.blockStateBase.getBlockStates()) {
                byte dataValue = dataValueGenerator.apply((LanternBlockState) state);
                short stateValue = (short) ((internalId & 0xfff) << 4 | dataValue & 0xf);
                this.blockStatesById.put(stateValue, state);
                this.idsByBlockState.put(state, stateValue);
            }
        } else {
            BlockState state = blockType.getDefaultState();
            this.blockStatesById.put(internalId0, state);
            this.idsByBlockState.put(state, internalId0);
        }
    }

    @Override
    public void register(int internalId, BlockType blockType, Function<BlockState, Byte> dataValueGenerator) {
        this.register0(internalId, (LanternBlockType) blockType, checkNotNull(dataValueGenerator, "dataValueGenerator"));
    }

    @Override
    public void register(int internalId, BlockType blockType) {
        LanternBlockType blockType0 = (LanternBlockType) checkNotNull(blockType, "blockType");
        checkState(blockType0.blockStateBase.getBlockStates().size() <= 1,
                "You cannot register a blockType with more then one state with this method.");
        this.register0(internalId, blockType0, null);
    }

    @Override
    public void register(BlockType blockType, Function<BlockState, Byte> dataValueGenerator) {
        this.register(this.getNextInternalId(), blockType, dataValueGenerator);
    }

    @Override
    public void register(BlockType catalogType) {
        this.register(this.getNextInternalId(), catalogType);
    }

    private int getNextInternalId() {
        int internalId;
        do {
            internalId = this.blockIdCounter.getAndIncrement();
        } while (this.blocksById.containsKey((short) internalId));
        return internalId;
    }

    @Override
    public BlockState getStateByInternalId(int internalId) {
        if (internalId < 0 || internalId >= Short.MAX_VALUE) {
            return null;
        }
        return this.blockStatesById.get((short) internalId);
    }

    @Override
    public Short getInternalStateId(BlockState blockState) {
        return this.idsByBlockState.containsKey(blockState) ? this.idsByBlockState.get(blockState) : null;
    }

    @Override
    public Short getInternalStateId(BlockType blockType) {
        return this.getInternalStateId(blockType.getDefaultState());
    }

    @Override
    public BlockType getTypeByInternalId(int internalId) {
        return this.blocksById.get((short) (internalId & 0xfff));
    }

    @Override
    public Short getInternalTypeId(BlockType blockType) {
        return this.idsByBlock.containsKey(blockType) ? this.idsByBlock.get(blockType) : null;
    }

    @Override
    public BlockState getStateByInternalIdAndData(int internalId, byte data) {
        return this.getStateByInternalId((internalId & 0xfff) << 4 | (data & 0xf));
    }

    @Override
    public BlockState getStateByTypeAndData(BlockType blockType, byte data) {
        Short blockId = this.getInternalTypeId(blockType);
        if (blockId == null) {
            return null;
        }
        return this.getStateByInternalId((blockId & 0xfff) << 4 | (data & 0xf));
    }

    @Override
    public Byte getStateData(BlockState blockState) {
        return this.idsByBlockState.containsKey(blockState) ? (byte) (this.idsByBlockState.get(blockState) & 0xf) : null;
    }
}
