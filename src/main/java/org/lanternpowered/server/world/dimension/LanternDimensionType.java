/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
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

import org.lanternpowered.server.catalog.LanternPluginCatalogType;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionType;

public abstract class LanternDimensionType extends LanternPluginCatalogType implements DimensionType {

    private final Class<? extends Dimension> dimensionClass;
    private final boolean keepSpawnLoaded;
    private final boolean waterEvaporates;
    private final boolean hasSky;
    private final int internalId;

    public LanternDimensionType(String pluginId, String name, int internalId, Class<? extends Dimension> dimensionClass,
            boolean keepSpawnLoaded, boolean waterEvaporates, boolean hasSky) {
        super(pluginId, name);
        this.keepSpawnLoaded = keepSpawnLoaded;
        this.waterEvaporates = waterEvaporates;
        this.dimensionClass = dimensionClass;
        this.internalId = internalId;
        this.hasSky = hasSky;
    }

    @Override
    public boolean doesKeepSpawnLoaded() {
        return this.keepSpawnLoaded;
    }

    @Override
    public Class<? extends Dimension> getDimensionClass() {
        return this.dimensionClass;
    }

    public boolean doesWaterEvaporate() {
        return this.waterEvaporates;
    }

    public boolean hasSky() {
        return this.hasSky;
    }

    public int getInternalId() {
        return this.internalId;
    }

    public abstract LanternDimension create(LanternWorld world);
}
