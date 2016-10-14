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
package org.lanternpowered.server.world;

import org.lanternpowered.server.block.action.BlockAction;
import org.lanternpowered.server.world.chunk.LanternChunk;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

public interface WorldEventListener {

    /**
     * Is called when the specific chunk is loaded.
     *
     * @param chunk The chunk
     */
    void onLoadChunk(LanternChunk chunk);

    /**
     * Is called when the specific chunk is unloaded.
     *
     * @param chunk The chunk
     */
    void onUnloadChunk(LanternChunk chunk);

    /**
     * Is called when the specific chunk is populated.
     *
     * @param chunk The chunk
     */
    void onPopulateChunk(LanternChunk chunk);

    /**
     * Is called when the {@link BlockState} at the specified
     * coordinates is changed.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     * @param oldBlockState The old block state
     * @param newBlockState The new block state
     */
    void onBlockChange(int x, int y, int z, BlockState oldBlockState, BlockState newBlockState);

    /**
     * Is called when the {@link BlockAction} is triggered for the
     * {@link BlockType} at the specified coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     * @param blockType The block type
     * @param blockAction The block action
     */
    void onBlockAction(int x, int y, int z, BlockType blockType, BlockAction blockAction);
}
