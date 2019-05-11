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
package org.lanternpowered.server.world.dimension;

import static com.google.common.base.MoreObjects.toStringHelper;

import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.gen.IGeneratorType;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;

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

    @Override
    public int getMinimumSpawnHeight() {
        return IGeneratorType.getMinimalSpawnHeight(getGeneratorType(), this.world.getProperties().getGeneratorSettings());
    }

    @Override
    public boolean doesWaterEvaporate() {
        return this.world.getProperties().doesWaterEvaporate();
    }

    @Override
    public boolean hasSky() {
        return this.dimensionType.getHasSkylight();
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
