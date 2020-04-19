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
