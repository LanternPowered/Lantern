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
package org.lanternpowered.server.world.extent;

import org.lanternpowered.server.util.VecHelper;
import org.lanternpowered.server.util.gen.biome.AtomicObjectArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.biome.IntArrayImmutableBiomeBuffer;
import org.lanternpowered.server.util.gen.biome.ObjectArrayMutableBiomeBuffer;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.BiomeVolume;
import org.spongepowered.api.world.extent.ImmutableBiomeVolume;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.math.vector.Vector2i;
import org.spongepowered.math.vector.Vector3i;

public abstract class AbstractBiomeViewDownsize<A extends BiomeVolume> implements BiomeVolume {

    protected final A volume;
    protected final Vector3i min;
    protected final Vector3i max;
    protected final Vector3i size;

    public AbstractBiomeViewDownsize(A area, Vector3i min, Vector3i max) {
        this.volume = area;
        this.min = min;
        this.max = max;
        this.size = max.sub(min).add(Vector3i.ONE);
    }

    @Override
    public Vector3i getBiomeMin() {
        return this.min;
    }

    @Override
    public Vector3i getBiomeMax() {
        return this.max;
    }

    @Override
    public Vector3i getBiomeSize() {
        return this.size;
    }

    @Override
    public boolean containsBiome(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, this.min, this.max);
    }

    protected final void checkRange(Vector3i position) {
        checkRange(position.getX(), position.getY(), position.getZ());
    }

    protected final void checkRange(int x, int y, int z) {
        if (!VecHelper.inBounds(x, y, z, this.min, this.max)) {
            throw new PositionOutOfBoundsException(new Vector2i(x, z), this.min, this.max);
        }
    }

    @Override
    public BiomeType getBiome(int x, int y, int z) {
        checkRange(x, y, z);
        return this.volume.getBiome(x, y, z);
    }

    @Override
    public MutableBiomeVolume getBiomeCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new ObjectArrayMutableBiomeBuffer(ExtentBufferHelper.copyToBiomeObjectArray(
                        this, this.min, this.max, this.size), this.min, this.size);
            case THREAD_SAFE:
                return new AtomicObjectArrayMutableBiomeBuffer(ExtentBufferHelper.copyToBiomeObjectArray(
                        this, this.min, this.max, this.size), this.min, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBiomeVolume getImmutableBiomeCopy() {
        return IntArrayImmutableBiomeBuffer.newWithoutArrayClone(ExtentBufferHelper.copyToBiomeArray(
                this, this.min, this.max, this.size), this.min, this.size);
    }
}
