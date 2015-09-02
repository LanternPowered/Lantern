package org.lanternpowered.server.block;

import javax.annotation.Nullable;

import org.lanternpowered.server.catalog.SimpleCatalogTypeRegistry;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

public class LanternBlockRegistry extends SimpleCatalogTypeRegistry<BlockType> {

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
        return this.getStateById(internalId & 0xfff).getType();
    }

    @Nullable
    public Short getTypeId(BlockType blockType) {
        Short id = this.getStateId(blockType);
        return id == null ? null : (short) (id & 0xfff);
    }
}
