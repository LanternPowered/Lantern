/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import org.lanternpowered.server.util.VecHelper;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.storage.ChunkLayout;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;

public class LanternChunkLayout implements ChunkLayout {

    /**
     * The instance of the chunk layout.
     */
    public static final LanternChunkLayout INSTANCE = new LanternChunkLayout();

    // The amount of chunk sections
    public static final int CHUNK_SECTIONS = 8;
    // The size of one chunk section
    public static final Vector3i CHUNK_SECTION_SIZE = new Vector3i(16, 16, 16);
    public static final Vector3i CHUNK_SECTION_MASK = CHUNK_SECTION_SIZE.sub(Vector3i.ONE);
    // The size of one chunk
    public static final Vector3i CHUNK_SIZE = CHUNK_SECTION_SIZE.mul(1, CHUNK_SECTIONS, 1);
    public static final Vector3i CHUNK_MASK = CHUNK_SIZE.sub(Vector3i.ONE);

    public static final Vector2i CHUNK_AREA_SIZE = CHUNK_SECTION_SIZE.toVector2(true);

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
    public boolean isValidChunk(Vector3i coords) {
        checkNotNull(coords, "coords");
        return this.isValidChunk(coords.getX(), coords.getY(), coords.getZ());
    }

    @Override
    public boolean isValidChunk(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, SPACE_MIN, SPACE_MAX);
    }

    @Override
    public boolean isInChunk(Vector3i localCoords) {
        checkNotNull(localCoords, "localCoords");
        return this.isInChunk(localCoords.getX(), localCoords.getY(), localCoords.getZ());
    }

    @Override
    public boolean isInChunk(int x, int y, int z) {
        return (x >> 4) == 0 && (y >> 4) == 0 && (z >> 4) == 0;
    }

    @Override
    public boolean isInChunk(Vector3i worldCoords, Vector3i chunkCoords) {
        checkNotNull(worldCoords, "worldCoords");
        checkNotNull(chunkCoords, "chunkCoords");
        return this.isInChunk(worldCoords.getX(), worldCoords.getY(), worldCoords.getZ(), chunkCoords.getX(), chunkCoords.getY(), chunkCoords.getZ());
    }

    @Override
    public boolean isInChunk(int wx, int wy, int wz, int cx, int cy, int cz) {
        return this.isInChunk(wx - (cx << 4), wy - (cy << 4), wz - (cz << 4));
    }

    @Override
    public Optional<Vector3i> toChunk(Vector3i worldCoords) {
        checkNotNull(worldCoords, "worldCoords");
        return this.toChunk(worldCoords.getX(), worldCoords.getY(), worldCoords.getZ());
    }

    @Override
    public Optional<Vector3i> toChunk(int x, int y, int z) {
        Vector3i chunkCoords = new Vector3i(x >> 4, y >> 4, z >> 4);
        return this.isValidChunk(chunkCoords) ? Optional.of(chunkCoords) : Optional.empty();
    }

    @Override
    public Optional<Vector3i> toWorld(Vector3i chunkCoords) {
        checkNotNull(chunkCoords, "chunkCoords");
        return this.toChunk(chunkCoords.getX(), chunkCoords.getY(), chunkCoords.getZ());
    }

    @Override
    public Optional<Vector3i> toWorld(int x, int y, int z) {
        return this.isValidChunk(x, y, z) ? Optional.of(new Vector3i(x << 4, 0, z << 4)) : Optional.empty();
    }

    @Override
    public Optional<Vector3i> addToChunk(Vector3i chunkCoords, Vector3i chunkOffset) {
        checkNotNull(chunkCoords, "chunkCoords");
        checkNotNull(chunkOffset, "chunkOffset");
        return addToChunk(chunkCoords.getX(), chunkCoords.getY(), chunkCoords.getZ(), chunkOffset.getX(), chunkOffset.getY(), chunkOffset.getZ());
    }

    @Override
    public Optional<Vector3i> addToChunk(int cx, int cy, int cz, int ox, int oy, int oz) {
        Vector3i newChunkCoords = new Vector3i(cx + ox, cy + oy, cz + oz);
        return this.isValidChunk(newChunkCoords) ? Optional.of(newChunkCoords) : Optional.empty();
    }

    @Override
    public Optional<Vector3i> moveToChunk(Vector3i chunkCoords, Direction direction) {
        return this.moveToChunk(chunkCoords, direction, 1);
    }

    @Override
    public Optional<Vector3i> moveToChunk(int x, int y, int z, Direction direction) {
        return this.moveToChunk(x, y, z, direction, 1);
    }

    @Override
    public Optional<Vector3i> moveToChunk(Vector3i chunkCoords, Direction direction, int steps) {
        checkNotNull(chunkCoords, "chunkCoords");
        checkNotNull(direction, "direction");
        return this.addToChunk(chunkCoords, direction.toVector3d().ceil().toInt().mul(steps));
    }

    @Override
    public Optional<Vector3i> moveToChunk(int x, int y, int z, Direction direction, int steps) {
        return this.moveToChunk(new Vector3i(x, y, z), direction, steps);
    }

}
