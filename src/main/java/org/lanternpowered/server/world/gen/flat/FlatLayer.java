/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.world.gen.flat;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

public final class FlatLayer {

    private final BlockState blockState;
    private final int depth;

    /**
     * Creates a new flat layer with the specified block
     * type and layer depth.
     * 
     * @param blockType the block type
     * @param depth the layer depth
     */
    public FlatLayer(BlockType blockType, int depth) {
        this(checkNotNull(blockType, "blockType").getDefaultState(), depth);
    }

    /**
     * Creates a new flat layer with the specified block
     * state and layer depth.
     * 
     * @param blockState the block state
     * @param depth the layer depth
     */
    public FlatLayer(BlockState blockState, int depth) {
        checkArgument(depth > 0, "Depth must be at least 1");
        this.blockState = checkNotNull(blockState, "blockState");
        this.depth = depth;
    }

    /**
     * Gets the block state.
     * 
     * @return the block state
     */
    public BlockState getBlockState() {
        return this.blockState;
    }

    /**
     * Gets the layer depth.
     * 
     * @return the layer depth
     */
    public int getDepth() {
        return this.depth;
    }
}
