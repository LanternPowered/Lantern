/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
