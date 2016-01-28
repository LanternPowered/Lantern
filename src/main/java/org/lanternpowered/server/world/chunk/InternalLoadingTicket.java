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
package org.lanternpowered.server.world.chunk;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.game.LanternGame;

final class InternalLoadingTicket implements ChunkLoadingTicket {

    private static final ImmutableSet<Vector3i> CHUNK_LIST = ImmutableSet.of();

    @Override
    public boolean setNumChunks(int numChunks) {
        return false;
    }

    @Override
    public int getNumChunks() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxNumChunks() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getPlugin() {
        return LanternGame.MINECRAFT_ID;
    }

    @Override
    public ImmutableSet<Vector3i> getChunkList() {
        return CHUNK_LIST;
    }

    @Override
    public void forceChunk(Vector3i chunk) {
    }

    @Override
    public void unforceChunk(Vector3i chunk) {
    }

    @Override
    public void prioritizeChunk(Vector3i chunk) {
    }

    @Override
    public void release() {
    }

    @Override
    public void forceChunk(Vector2i chunk) {
    }

    @Override
    public void unforceChunk(Vector2i chunk) {
    }

    @Override
    public void unforceChunks() {
    }

    @Override
    public boolean isReleased() {
        return false;
    }
}
