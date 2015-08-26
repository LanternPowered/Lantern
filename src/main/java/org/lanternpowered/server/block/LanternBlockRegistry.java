package org.lanternpowered.server.block;

import javax.annotation.Nullable;

import org.spongepowered.api.block.BlockState;

public class LanternBlockRegistry {

    @Nullable
    public BlockState getStateByInternalId(int internalId) {
        if (internalId < 0 || internalId >= Short.MAX_VALUE) {
            return null;
        }
        // TODO
        return null;
    }

    @Nullable
    public Short getInternalId(BlockState blockState) {
        // TODO
        return null;
    }
}
