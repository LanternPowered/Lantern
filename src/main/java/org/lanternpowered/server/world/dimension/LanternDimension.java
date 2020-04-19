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
package org.lanternpowered.server.world.dimension;

import static com.google.common.base.MoreObjects.toStringHelper;

import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.gen.IGeneratorType;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionType;

public abstract class LanternDimension implements Dimension {

    private final LanternDimensionType<?> dimensionType;
    private final LanternWorld world;

    protected LanternDimension(LanternWorld world, LanternDimensionType<?> dimensionType) {
        this.dimensionType = dimensionType;
        this.world = world;
    }

    @Override
    public Context getContext() {
        return this.dimensionType.getDimensionContext();
    }

    @Override
    public boolean allowsPlayerRespawns() {
        return this.world.getProperties().allowsPlayerRespawns();
    }

    public int getMinimumSpawnHeight() {
        return IGeneratorType.getMinimalSpawnHeight(getGeneratorType(), this.world.getProperties().getGeneratorSettings());
    }

    @Override
    public boolean doesWaterEvaporate() {
        return this.world.getProperties().doesWaterEvaporate();
    }

    @Override
    public boolean hasSky() {
        return this.dimensionType.hasSkylight();
    }

    @Override
    public DimensionType getType() {
        return this.dimensionType;
    }

    @Override
    public int getHeight() {
        return IGeneratorType.getGeneratorHeight(getGeneratorType(), this.world.getProperties().getGeneratorSettings());
    }

    @Override
    public int getBuildHeight() {
        return this.world.getProperties().getBuildHeight();
    }

    @Override
    public GeneratorType getGeneratorType() {
        return this.world.getProperties().getGeneratorType();
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("worldUUID", this.world.getUniqueId().toString())
                .add("worldName", this.world.getName())
                .add("dimensionType", this.dimensionType.getKey())
                .toString();
    }
}
