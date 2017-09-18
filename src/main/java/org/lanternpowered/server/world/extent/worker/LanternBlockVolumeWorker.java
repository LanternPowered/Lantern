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
package org.lanternpowered.server.world.extent.worker;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.extent.BlockVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;
import org.spongepowered.api.world.extent.worker.BlockVolumeWorker;
import org.spongepowered.api.world.extent.worker.procedure.BlockVolumeMapper;
import org.spongepowered.api.world.extent.worker.procedure.BlockVolumeMerger;
import org.spongepowered.api.world.extent.worker.procedure.BlockVolumeReducer;
import org.spongepowered.api.world.extent.worker.procedure.BlockVolumeVisitor;

import java.util.function.BiFunction;

public class LanternBlockVolumeWorker<V extends BlockVolume> implements BlockVolumeWorker<V> {

    protected final V volume;

    public LanternBlockVolumeWorker(V volume) {
        this.volume = checkNotNull(volume, "volume");
    }

    @Override
    public V getVolume() {
        return this.volume;
    }

    @Override
    public void map(BlockVolumeMapper mapper, MutableBlockVolume destination) {
        final Vector3i offset = align(destination);
        final int xOffset = offset.getX();
        final int yOffset = offset.getY();
        final int zOffset = offset.getZ();
        final UnmodifiableBlockVolume unmodifiableVolume = this.volume.getUnmodifiableBlockView();
        final int xMin = unmodifiableVolume.getBlockMin().getX();
        final int yMin = unmodifiableVolume.getBlockMin().getY();
        final int zMin = unmodifiableVolume.getBlockMin().getZ();
        final int xMax = unmodifiableVolume.getBlockMax().getX();
        final int yMax = unmodifiableVolume.getBlockMax().getY();
        final int zMax = unmodifiableVolume.getBlockMax().getZ();
        for (int z = zMin; z <= zMax; z++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int x = xMin; x <= xMax; x++) {
                    final BlockState block = mapper.map(unmodifiableVolume, x, y, z);
                    destination.setBlock(x + xOffset, y + yOffset, z + zOffset, block);
                }
            }
        }
    }

    @Override
    public void merge(BlockVolume second, BlockVolumeMerger merger, MutableBlockVolume destination) {
        final Vector3i offsetSecond = align(second);
        final int xOffsetSecond = offsetSecond.getX();
        final int yOffsetSecond = offsetSecond.getY();
        final int zOffsetSecond = offsetSecond.getZ();
        final Vector3i offsetDestination = align(destination);
        final int xOffsetDestination = offsetDestination.getX();
        final int yOffsetDestination = offsetDestination.getY();
        final int zOffsetDestination = offsetDestination.getZ();
        final UnmodifiableBlockVolume firstUnmodifiableVolume = this.volume.getUnmodifiableBlockView();
        final int xMin = firstUnmodifiableVolume.getBlockMin().getX();
        final int yMin = firstUnmodifiableVolume.getBlockMin().getY();
        final int zMin = firstUnmodifiableVolume.getBlockMin().getZ();
        final int xMax = firstUnmodifiableVolume.getBlockMax().getX();
        final int yMax = firstUnmodifiableVolume.getBlockMax().getY();
        final int zMax = firstUnmodifiableVolume.getBlockMax().getZ();
        final UnmodifiableBlockVolume secondUnmodifiableVolume = second.getUnmodifiableBlockView();
        for (int z = zMin; z <= zMax; z++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int x = xMin; x <= xMax; x++) {
                    final BlockState block = merger.merge(firstUnmodifiableVolume, x, y, z,
                            secondUnmodifiableVolume, x + xOffsetSecond, y + yOffsetSecond, z + zOffsetSecond);
                    destination.setBlock(x + xOffsetDestination, y + yOffsetDestination, z + zOffsetDestination, block);
                }
            }
        }
    }

    @Override
    public void iterate(BlockVolumeVisitor<V> visitor) {
        final int xMin = this.volume.getBlockMin().getX();
        final int yMin = this.volume.getBlockMin().getY();
        final int zMin = this.volume.getBlockMin().getZ();
        final int xMax = this.volume.getBlockMax().getX();
        final int yMax = this.volume.getBlockMax().getY();
        final int zMax = this.volume.getBlockMax().getZ();
        for (int z = zMin; z <= zMax; z++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int x = xMin; x <= xMax; x++) {
                    visitor.visit(this.volume, x, y, z);
                }
            }
        }
    }

    @Override
    public <T> T reduce(BlockVolumeReducer<T> reducer, BiFunction<T, T, T> merge, T identity) {
        final UnmodifiableBlockVolume unmodifiableVolume = this.volume.getUnmodifiableBlockView();
        final int xMin = unmodifiableVolume.getBlockMin().getX();
        final int yMin = unmodifiableVolume.getBlockMin().getY();
        final int zMin = unmodifiableVolume.getBlockMin().getZ();
        final int xMax = unmodifiableVolume.getBlockMax().getX();
        final int yMax = unmodifiableVolume.getBlockMax().getY();
        final int zMax = unmodifiableVolume.getBlockMax().getZ();
        T reduction = identity;
        for (int z = zMin; z <= zMax; z++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int x = xMin; x <= xMax; x++) {
                    reduction = reducer.reduce(unmodifiableVolume, x, y, z, reduction);
                }
            }
        }
        return reduction;
    }

    private Vector3i align(BlockVolume other) {
        final Vector3i thisSize = this.volume.getBlockSize();
        final Vector3i otherSize = other.getBlockSize();
        checkArgument(otherSize.getX() >= thisSize.getX() && otherSize.getY() >= thisSize.getY() && otherSize.getY() >= thisSize.getY(),
                "Other volume is smaller than work volume");
        return other.getBlockMin().sub(this.volume.getBlockMin());
    }

}
