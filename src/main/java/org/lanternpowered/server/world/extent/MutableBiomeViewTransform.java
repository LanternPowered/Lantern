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

import org.lanternpowered.server.world.extent.worker.LanternMutableBiomeVolumeWorker;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.UnmodifiableBiomeVolume;
import org.spongepowered.api.world.extent.worker.MutableBiomeVolumeWorker;
import org.spongepowered.math.vector.Vector3i;

public class MutableBiomeViewTransform extends AbstractBiomeViewTransform<MutableBiomeVolume> implements MutableBiomeVolume {

    public MutableBiomeViewTransform(MutableBiomeVolume volume, DiscreteTransform3 transform) {
        super(volume, transform);
    }

    @Override
    public void setBiome(Vector3i position, BiomeType biome) {
        this.setBiome(position.getX(), position.getY(), position.getZ(), biome);
    }

    @Override
    public void setBiome(int x, int y, int z, BiomeType biome) {
        this.volume.setBiome(this.inverseTransform.transformX(x, y, z),
                this.inverseTransform.transformY(x, y, z), this.inverseTransform.transformZ(x, y, z), biome);
    }

    @Override
    public MutableBiomeVolume getBiomeView(Vector3i newMin, Vector3i newMax) {
        return new MutableBiomeViewDownsize(this.volume, this.inverseTransform.transform(newMin),
                this.inverseTransform.transform(newMax)).getBiomeView(this.transform);
    }

    @Override
    public MutableBiomeVolume getBiomeView(DiscreteTransform3 transform) {
        return new MutableBiomeViewTransform(this.volume, this.transform.withTransformation(transform));
    }

    @Override
    public MutableBiomeVolumeWorker<? extends MutableBiomeVolume> getBiomeWorker() {
        return new LanternMutableBiomeVolumeWorker<>(this);
    }

    @Override
    public UnmodifiableBiomeVolume getUnmodifiableBiomeView() {
        return new UnmodifiableBiomeVolumeWrapper(this);
    }

}
