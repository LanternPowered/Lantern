/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
