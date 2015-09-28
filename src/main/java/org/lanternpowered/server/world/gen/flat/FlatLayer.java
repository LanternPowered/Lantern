package org.lanternpowered.server.world.gen.flat;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

public final class FlatLayer {

    private final BlockState blockState;
    private final int depth;

    public FlatLayer(BlockType blockType, int depth) {
        this(blockType.getDefaultState(), depth);
    }

    public FlatLayer(BlockState blockState, int depth) {
        this.blockState = blockState;
        this.depth = depth;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public int getDepth() {
        return this.depth;
    }
}
