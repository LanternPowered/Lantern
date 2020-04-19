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
package org.lanternpowered.server.world.chunk;

import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTIONS;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTION_SIZE;

import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.world.storage.ChunkLayout;
import org.spongepowered.math.vector.Vector2i;
import org.spongepowered.math.vector.Vector3i;

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
    public static final Vector3i CHUNK_BIOME_VOLUME = new Vector3i(CHUNK_SECTION_SIZE_VECTOR.getX(), 1, CHUNK_SECTION_SIZE_VECTOR.getZ());

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
