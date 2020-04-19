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
package org.lanternpowered.server.world.gen;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.gen.BiomeGenerator;
import org.spongepowered.math.vector.Vector3i;

public class SingleBiomeGenerator implements BiomeGenerator {

    private final BiomeType biomeType;

    public SingleBiomeGenerator(BiomeType biomeType) {
        this.biomeType = checkNotNull(biomeType, "biomeType");
    }

    @Override
    public void generateBiomes(MutableBiomeVolume buffer) {
        final Vector3i min = buffer.getBiomeMin();
        final Vector3i max = buffer.getBiomeMax();
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    buffer.setBiome(x, y, z, this.biomeType);
                }
            }
        }
    }

}
