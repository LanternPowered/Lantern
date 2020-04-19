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

import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.worker.MutableBiomeVolumeWorker;
import org.spongepowered.api.world.extent.worker.procedure.BiomeVolumeFiller;

public class LanternMutableBiomeVolumeWorker<V extends MutableBiomeVolume> extends LanternBiomeVolumeWorker<V> implements MutableBiomeVolumeWorker<V> {

    public LanternMutableBiomeVolumeWorker(V volume) {
        super(volume);
    }

    @Override
    public void fill(BiomeVolumeFiller filler) {
        final int xMin = this.volume.getBiomeMin().getX();
        final int yMin = this.volume.getBiomeMin().getY();
        final int zMin = this.volume.getBiomeMin().getZ();
        final int xMax = this.volume.getBiomeMax().getX();
        final int yMax = this.volume.getBiomeMax().getY();
        final int zMax = this.volume.getBiomeMax().getZ();
        for (int z = zMin; z <= zMax; z++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int x = xMin; x <= xMax; x++) {
                    final BiomeType biome = filler.produce(x, y, z);
                    this.volume.setBiome(x, y, z, biome);
                }
            }
        }
    }
}
