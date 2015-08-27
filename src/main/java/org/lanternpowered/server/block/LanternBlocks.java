package org.lanternpowered.server.block;

import javax.annotation.Nullable;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.block.BlockState;

public class LanternBlocks {

    private static LanternBlockRegistry registry;

    /**
     * Gets the {@link LanternBlockRegistry}.
     * 
     * @return the block registry
     */
    public static LanternBlockRegistry getRegistry() {
        if (registry == null) {
            registry = LanternGame.get().getRegistry().getBlockRegistry();
        }
        return registry;
    }

    @Nullable
    public static BlockState getStateById(int internalId) {
        return getRegistry().getStateById(internalId);
    }

    @Nullable
    public static Short getStateId(BlockState blockState) {
        return getRegistry().getStateId(blockState);
    }

}
