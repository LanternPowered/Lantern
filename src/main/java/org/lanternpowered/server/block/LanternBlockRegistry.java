package org.lanternpowered.server.block;

import javax.annotation.Nullable;

import org.lanternpowered.server.catalog.LanternCatalogTypeRegistry;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

public class LanternBlockRegistry extends LanternCatalogTypeRegistry<BlockType> {

    @Nullable
    public BlockState getStateById(int internalId) {
        if (internalId < 0 || internalId >= Short.MAX_VALUE) {
            return null;
        }
        // TODO
        return null;
    }

    @Nullable
    public Short getStateId(BlockState blockState) {
        // TODO
        return null;
    }

    @Nullable
    public Short getStateId(BlockType blockType) {
        return this.getStateId(blockType.getDefaultState());
    }

    @Nullable
    public BlockType getTypeById(int internalId) {
        return this.getStateById((internalId & 0xfff) << 4).getType();
    }

    @Nullable
    public Short getTypeId(BlockType blockType) {
        Short id = this.getStateId(blockType);
        return id == null ? null : (short) ((id >> 4) & 0xfff);
    }

    @Nullable
    public BlockState getStateByIdAndData(int internalId, byte data) {
        return this.getStateById((internalId & 0xfff) << 4 | (data & 0xf));
    }

    @Nullable
    public BlockState getStateByTypeAndData(BlockType blockType, byte data) {
        return this.getStateById((this.getTypeId(blockType) & 0xfff) << 4 | (data & 0xf));
    }

    @Nullable
    public Byte getStateData(BlockState blockState) {
        Short id = this.getStateId(blockState);
        return id == null ? null : (byte) (id & 0xf);
    }
}
