/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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

import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTIONS;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTION_SIZE;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.world.storage.ChunkLayout;

public class LanternChunkLayout implements ChunkLayout {

    // The instance of the chunk layout.
    public static final LanternChunkLayout INSTANCE = new LanternChunkLayout();

    // The size of one chunk section
    public static final Vector3i CHUNK_SECTION_SIZE_VECTOR = new Vector3i(CHUNK_SECTION_SIZE, CHUNK_SECTION_SIZE, CHUNK_SECTION_SIZE);
    public static final Vector3i CHUNK_SECTION_MASK = CHUNK_SECTION_SIZE_VECTOR.sub(Vector3i.ONE);
    // The size of one chunk
    public static final Vector3i CHUNK_SIZE = CHUNK_SECTION_SIZE_VECTOR.mul(1, CHUNK_SECTIONS, 1);
    public static final Vector3i CHUNK_MASK = CHUNK_SIZE.sub(Vector3i.ONE);

    public static final Vector2i CHUNK_AREA_SIZE = CHUNK_SECTION_SIZE_VECTOR.toVector2(true);

    public static final Vector3i SPACE_MAX = LanternWorld.BLOCK_MAX.div(CHUNK_SIZE);
    public static final Vector3i SPACE_MIN = LanternWorld.BLOCK_MIN.div(CHUNK_SIZE);
    public static final Vector3i SPACE_SIZE = SPACE_MAX.sub(SPACE_MIN).add(1, 1, 1);

    @Override
    public Vector3i getChunkSize() {
        return CHUNK_SIZE;
    }

    @Override
    public Vector3i getSpaceMax() {
        return SPACE_MAX;
    }

    @Override
    public Vector3i getSpaceMin() {
        return SPACE_MIN;
    }

    @Override
    public Vector3i getSpaceSize() {
        return SPACE_SIZE;
    }

    @Override
    public Vector3i getSpaceOrigin() {
        return Vector3i.ZERO;
    }

    @Override
    public boolean isInChunk(int x, int y, int z) {
        // No bits allowed outside the mask!
        return (x & ~CHUNK_MASK.getX()) == 0 && (y & ~CHUNK_MASK.getY()) == 0 && (z & ~CHUNK_MASK.getZ()) == 0;
    }

    @Override
    public boolean isInChunk(int wx, int wy, int wz, int cx, int cy, int cz) {
        return this.isInChunk(wx - (cx << 4), wy - (cy << 8), wz - (cz << 4));
    }

    @Override
    public Vector3i forceToChunk(int x, int y, int z) {
        return new Vector3i(x >> 4, y >> 8, z >> 4);
    }

    @Override
    public Vector3i forceToWorld(int x, int y, int z) {
        return new Vector3i(x << 4, y << 8, z << 4);
    }

}
