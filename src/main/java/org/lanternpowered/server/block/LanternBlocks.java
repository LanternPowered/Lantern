package org.lanternpowered.server.block;

import javax.annotation.Nullable;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

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

    @Nullable
    public static Short getStateId(BlockType blockType) {
        return getRegistry().getStateId(blockType);
    }

    @Nullable
    public static BlockType getTypeById(int internalId) {
        return getRegistry().getTypeById(internalId);
    }

    @Nullable
    public static Short getTypeId(BlockType blockType) {
        return getRegistry().getTypeId(blockType);
    }

    @Nullable
    public static BlockState getStateByIdAndData(int internalId, byte data) {
        return getRegistry().getStateByIdAndData(internalId, data);
    }

    @Nullable
    public static Byte getStateData(BlockState blockState) {
        return getRegistry().getStateData(blockState);
    }

    @Nullable
    public static BlockState getStateByTypeAndData(BlockType blockType, byte data) {
        return getRegistry().getStateByTypeAndData(blockType, data);
    }
}
