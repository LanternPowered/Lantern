/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.server.world.extent;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.api.x.world.extent.XExtent;
import org.lanternpowered.server.util.VecHelper;
import org.lanternpowered.server.util.gen.biome.AtomicObjectArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.biome.ObjectArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.biome.ShortArrayImmutableBiomeBuffer;
import org.lanternpowered.server.util.gen.block.AtomicShortArrayMutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.ShortArrayImmutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.ShortArrayMutableBlockBuffer;
import org.lanternpowered.server.world.extent.worker.LanternMutableBiomeVolumeWorker;
import org.lanternpowered.server.world.extent.worker.LanternMutableBlockVolumeWorker;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
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

public interface AbstractExtent extends XExtent {

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
        final Location<World> location = checkNotNull(snapshot, "snapshot").getLocation().orElse(null);
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
        return ShortArrayImmutableBiomeBuffer.newWithoutArrayClone(ExtentBufferHelper.copyToBiomeArray(this, getBiomeMin(),
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
                return new ShortArrayMutableBlockBuffer(ExtentBufferHelper.copyToBlockArray(this, getBlockMin(),
                        getBlockMax(), getBlockSize()), getBlockMin(), getBlockSize());
            case THREAD_SAFE:
                return new AtomicShortArrayMutableBlockBuffer(ExtentBufferHelper.copyToBlockArray(this, getBlockMin(),
                        getBlockMax(), getBlockSize()), getBlockMin(), getBlockSize());
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    default ImmutableBlockVolume getImmutableBlockCopy() {
        return ShortArrayImmutableBlockBuffer.newWithoutArrayClone(ExtentBufferHelper.copyToBlockArray(this, getBlockMin(),
                getBlockMax(), getBlockSize()), getBlockMin(), getBlockSize());
    }
}
