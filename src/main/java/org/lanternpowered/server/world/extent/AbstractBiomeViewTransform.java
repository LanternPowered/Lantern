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

import org.lanternpowered.server.util.gen.biome.AtomicObjectArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.biome.IntArrayImmutableBiomeBuffer;
import org.lanternpowered.server.util.gen.biome.ObjectArrayMutableBiomeBuffer;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.BiomeVolume;
import org.spongepowered.api.world.extent.ImmutableBiomeVolume;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.math.vector.Vector3i;

public abstract class AbstractBiomeViewTransform<V extends BiomeVolume> implements BiomeVolume {

    protected final V volume;
    protected final DiscreteTransform3 transform;
    protected final DiscreteTransform3 inverseTransform;
    protected final Vector3i min;
    protected final Vector3i max;
    protected final Vector3i size;

    public AbstractBiomeViewTransform(V volume, DiscreteTransform3 transform) {
        this.volume = volume;
        this.transform = transform;
        this.inverseTransform = transform.invert();

        final Vector3i a = transform.transform(volume.getBiomeMin());
        final Vector3i b = transform.transform(volume.getBiomeMax());
        this.min = a.min(b);
        this.max = a.max(b);

        this.size = this.max.sub(this.min).add(Vector3i.ONE);
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
        return this.volume.containsBiome(this.inverseTransform.transformX(x, y, z),
                this.inverseTransform.transformY(x, y, z), this.inverseTransform.transformZ(x, y, z));
    }

    @Override
    public BiomeType getBiome(int x, int y, int z) {
        return this.volume.getBiome(this.inverseTransform.transformX(x, y, z),
                this.inverseTransform.transformY(x, y, z), this.inverseTransform.transformZ(x, y, z));
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
