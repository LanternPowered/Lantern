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
package org.lanternpowered.server.world.extent.worker;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.worker.MutableBlockVolumeWorker;
import org.spongepowered.api.world.extent.worker.procedure.BlockVolumeFiller;

public class LanternMutableBlockVolumeWorker<V extends MutableBlockVolume> extends LanternBlockVolumeWorker<V>
        implements MutableBlockVolumeWorker<V> {

    public LanternMutableBlockVolumeWorker(V volume) {
        super(volume);
    }

    @Override
    public void fill(BlockVolumeFiller filler) {
        final int xMin = this.volume.getBlockMin().getX();
        final int yMin = this.volume.getBlockMin().getY();
        final int zMin = this.volume.getBlockMin().getZ();
        final int xMax = this.volume.getBlockMax().getX();
        final int yMax = this.volume.getBlockMax().getY();
        final int zMax = this.volume.getBlockMax().getZ();
        for (int z = zMin; z <= zMax; z++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int x = xMin; x <= xMax; x++) {
                    final BlockState block = filler.produce(x, y, z);
                    this.volume.setBlock(x, y, z, block);
                }
            }
        }
    }
}
