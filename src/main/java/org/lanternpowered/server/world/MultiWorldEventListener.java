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

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.block.action.BlockAction;
import org.lanternpowered.server.world.chunk.LanternChunk;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

import java.util.ArrayList;
import java.util.List;

public final class MultiWorldEventListener implements WorldEventListener {

    private final List<WorldEventListener> listeners = new ArrayList<>();

    public void add(WorldEventListener listener) {
        this.listeners.add(checkNotNull(listener, "listener"));
    }

    @Override
    public void onLoadChunk(LanternChunk chunk) {
        this.listeners.forEach(listener -> listener.onLoadChunk(chunk));
    }

    @Override
    public void onUnloadChunk(LanternChunk chunk) {
        this.listeners.forEach(listener -> listener.onUnloadChunk(chunk));
    }

    @Override
    public void onPopulateChunk(LanternChunk chunk) {
        this.listeners.forEach(listener -> listener.onPopulateChunk(chunk));
    }

    @Override
    public void onBlockChange(int x, int y, int z, BlockState oldBlockState, BlockState newBlockState) {
        this.listeners.forEach(listener -> listener.onBlockChange(x, y, z, oldBlockState, newBlockState));
    }

    @Override
    public void onBlockAction(int x, int y, int z, BlockType blockType, BlockAction blockAction) {
        this.listeners.forEach(listener -> listener.onBlockAction(x, y, z, blockType, blockAction));
    }
}
