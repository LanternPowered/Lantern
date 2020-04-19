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
package org.lanternpowered.server.util.gen.biome;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.util.VecHelper;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.extent.BiomeVolume;
import org.spongepowered.math.vector.Vector3i;

/**
 * Base class for biome areas. This class provides methods for retrieving the
 * size and for range checking.
 */
public abstract class AbstractBiomeBuffer implements BiomeVolume {

    protected Vector3i start;
    protected Vector3i size;
    protected Vector3i end;
    private final int yLine;
    private final int yzSlice;

    protected AbstractBiomeBuffer(Vector3i start, Vector3i size) {
        this.start = start;
        this.size = size;
        this.end = this.start.add(this.size).sub(Vector3i.ONE);
        this.yLine = size.getY();
        this.yzSlice = this.yLine * size.getZ();
    }

    protected final void checkRange(Vector3i position) {
        checkNotNull(position);
        checkRange(position.getX(), position.getY(), position.getZ());
    }

    protected final void checkRange(int x, int y, int z) {
        if (!VecHelper.inBounds(x, y, z, this.start, this.end)) {
            throw new PositionOutOfBoundsException(new Vector3i(x, y, z), this.start, this.end);
        }
    }

    protected int index(int x, int y, int z) {
        return (x - this.start.getX()) * this.yzSlice + (z - this.start.getZ()) * this.yLine + (y - this.start.getY());
    }

    @Override
    public Vector3i getBiomeMin() {
        return this.start;
    }

    @Override
    public Vector3i getBiomeMax() {
        return this.end;
    }

    @Override
    public Vector3i getBiomeSize() {
        return this.size;
    }

    @Override
    public boolean containsBiome(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, this.start, this.end);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("min", this.getBiomeMin())
                .add("max", this.getBiomeMax())
                .toString();
    }
}
