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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.util.VecHelper;
import org.lanternpowered.server.util.gen.biome.AtomicObjectArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.biome.IntArrayImmutableBiomeBuffer;
import org.lanternpowered.server.util.gen.biome.ObjectArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.block.AtomicIntArrayMutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.IntArrayImmutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.IntArrayMutableBlockBuffer;
import org.lanternpowered.server.world.extent.worker.LanternMutableBiomeVolumeWorker;
import org.lanternpowered.server.world.extent.worker.LanternMutableBlockVolumeWorker;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.extent.ImmutableBiomeVolume;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.UnmodifiableBiomeVolume;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;
import org.spongepowered.api.world.extent.worker.MutableBiomeVolumeWorker;
import org.spongepowered.api.world.extent.worker.MutableBlockVolumeWorker;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

public interface AbstractExtent extends IExtent {

    default void checkBiomeBounds(int x, int y, int z) {
        if (!containsBiome(x, y, z)) {
            throw new PositionOutOfBoundsException(new Vector3i(x, y, z), getBlockMin(), getBlockMax());
        }
    }

    default void checkVolumeBounds(int x, int y, int z) {
        if (!containsBlock(x, y, z)) {
            throw new PositionOutOfBoundsException(new Vector3i(x, y, z), getBlockMin(), getBlockMax());
        }
    }

    default void checkVolumeBounds(Vector3i position) {
        checkVolumeBounds(position.getX(), position.getY(), position.getZ());
    }

    default void checkRange(double x, double y, double z) {
        if (!VecHelper.inBounds(x, y, z, getBlockMin(), getBlockMax())) {
            throw new PositionOutOfBoundsException(new Vector3d(x, y, z),
                    getBlockMin().toDouble(), getBlockMax().toDouble());
        }
    }

    default void checkRange(int x, int y, int z) {
        if (!VecHelper.inBounds(x, y, z, getBlockMin(), getBlockMax())) {
            throw new PositionOutOfBoundsException(new Vector3i(x, y, z),
                    getBlockMin(), getBlockMax());
        }
    }

    @Override
    default MutableBiomeVolumeWorker<? extends Extent> getBiomeWorker() {
        return new LanternMutableBiomeVolumeWorker<>(this);
    }

    @Override
    default MutableBlockVolumeWorker<? extends Extent> getBlockWorker() {
        return new LanternMutableBlockVolumeWorker<>(this);
    }

    @Override
    default boolean restoreSnapshot(BlockSnapshot snapshot, boolean force, BlockChangeFlag flag) {
        final Location location = checkNotNull(snapshot, "snapshot").getLocation().orElse(null);
        checkArgument(location != null, "location is not present in snapshot");
        return restoreSnapshot(location.getBlockPosition(), snapshot, force, flag);
    }

    @Override
    default MutableBiomeVolume getBiomeView(Vector3i newMin, Vector3i newMax) {
        if (!containsBiome(newMin.getX(), newMin.getY(), newMin.getZ())) {
            throw new PositionOutOfBoundsException(newMin, getBiomeMin(), getBiomeMax());
        }
        if (!containsBiome(newMax.getX(), newMax.getY(), newMax.getZ())) {
            throw new PositionOutOfBoundsException(newMax, getBiomeMin(), getBiomeMax());
        }
        return new MutableBiomeViewDownsize(this, newMin, newMax);
    }

    @Override
    default MutableBiomeVolume getBiomeView(DiscreteTransform3 transform) {
        return new MutableBiomeViewTransform(this, transform);
    }

    @Override
    default UnmodifiableBiomeVolume getUnmodifiableBiomeView() {
        return new UnmodifiableBiomeVolumeWrapper(this);
    }

    @Override
    default MutableBiomeVolume getBiomeCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new ObjectArrayMutableBiomeBuffer(ExtentBufferHelper.copyToBiomeObjectArray(this, getBiomeMin(),
                        getBiomeMax(), getBiomeSize()), getBiomeMin(), getBiomeSize());
            case THREAD_SAFE:
                return new AtomicObjectArrayMutableBiomeBuffer(ExtentBufferHelper.copyToBiomeObjectArray(this, getBiomeMin(),
                        getBiomeMax(), getBiomeSize()), getBiomeMin(), getBiomeSize());
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    default ImmutableBiomeVolume getImmutableBiomeCopy() {
        return IntArrayImmutableBiomeBuffer.newWithoutArrayClone(ExtentBufferHelper.copyToBiomeArray(this, getBiomeMin(),
                getBiomeMax(), getBiomeSize()), getBiomeMin(), getBiomeSize());
    }

    @Override
    default MutableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        if (!containsBlock(newMin.getX(), newMin.getY(), newMin.getZ())) {
            throw new PositionOutOfBoundsException(newMin, getBlockMin(), getBlockMax());
        }
        if (!containsBlock(newMax.getX(), newMax.getY(), newMax.getZ())) {
            throw new PositionOutOfBoundsException(newMax, getBlockMin(), getBlockMax());
        }
        return new MutableBlockViewDownsize(this, newMin, newMax);
    }

    @Override
    default MutableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new MutableBlockViewTransform(this, transform);
    }

    @Override
    default UnmodifiableBlockVolume getUnmodifiableBlockView() {
        return new UnmodifiableBlockVolumeWrapper(this);
    }

    @Override
    default MutableBlockVolume getBlockCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new IntArrayMutableBlockBuffer(ExtentBufferHelper.copyToBlockArray(this, getBlockMin(),
                        getBlockMax(), getBlockSize()), getBlockMin(), getBlockSize());
            case THREAD_SAFE:
                return new AtomicIntArrayMutableBlockBuffer(ExtentBufferHelper.copyToBlockArray(this, getBlockMin(),
                        getBlockMax(), getBlockSize()), getBlockMin(), getBlockSize());
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    default ImmutableBlockVolume getImmutableBlockCopy() {
        return IntArrayImmutableBlockBuffer.newWithoutArrayClone(ExtentBufferHelper.copyToBlockArray(this, getBlockMin(),
                getBlockMax(), getBlockSize()), getBlockMin(), getBlockSize());
    }
}
