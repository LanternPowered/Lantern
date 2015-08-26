package org.lanternpowered.server.block;

import javax.annotation.Nullable;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.block.BlockState;

public class LanternBlocks {

    @Nullable
    public static BlockState getStateByInternalId(int internalId) {
        return LanternGame.get().getRegistry().getBlockRegistry().getStateByInternalId(internalId);
    }

    @Nullable
    public static Short getInternalId(BlockState blockState) {
        return LanternGame.get().getRegistry().getBlockRegistry().getInternalId(blockState);
    }
}
