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

import org.lanternpowered.server.world.extent.worker.LanternBlockVolumeWorker;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.extent.BlockVolume;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;
import org.spongepowered.api.world.extent.worker.BlockVolumeWorker;
import org.spongepowered.math.vector.Vector3i;

public class UnmodifiableBlockViewDownsize extends AbstractBlockViewDownsize<BlockVolume> implements UnmodifiableBlockVolume {

    public UnmodifiableBlockViewDownsize(BlockVolume volume, Vector3i min, Vector3i max) {
        super(volume, min, max);
    }

    @Override
    public UnmodifiableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        checkRange(newMin);
        checkRange(newMax);
        return new UnmodifiableBlockViewDownsize(this.volume, newMin, newMax);
    }

    @Override
    public UnmodifiableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new UnmodifiableBlockViewTransform(this, transform);
    }

    @Override
    public BlockVolumeWorker<? extends UnmodifiableBlockVolume> getBlockWorker() {
        return new LanternBlockVolumeWorker<>(this);
    }
}
