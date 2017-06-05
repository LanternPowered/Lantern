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

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;

import java.util.function.BiFunction;

public final class LanternDimensionType<T extends LanternDimension> extends PluginCatalogType.Base.Internal implements DimensionType {

    private final BiFunction<LanternWorld, LanternDimensionType<T>, T> supplier;
    private final GeneratorType defaultGeneratorType;
    private final Class<T> dimensionClass;
    private final boolean keepSpawnLoaded;
    private final boolean waterEvaporates;
    private final boolean allowsPlayerRespawns;
    private final boolean hasSky;
    private final Context dimContext;

    public LanternDimensionType(String pluginId, String name, int internalId, Class<T> dimensionClass,
            GeneratorType defaultGeneratorType, boolean keepSpawnLoaded, boolean waterEvaporates,
            boolean hasSky, boolean allowsPlayerRespawns, BiFunction<LanternWorld, LanternDimensionType<T>, T> supplier) {
        super(pluginId, name, internalId);
        this.dimContext = new Context(Context.DIMENSION_KEY, this.getId());
        this.defaultGeneratorType = defaultGeneratorType;
        this.allowsPlayerRespawns = allowsPlayerRespawns;
        this.keepSpawnLoaded = keepSpawnLoaded;
        this.waterEvaporates = waterEvaporates;
        this.dimensionClass = dimensionClass;
        this.supplier = supplier;
        this.hasSky = hasSky;
    }

    /**
     * Gets the shared {@link Context} for all the {@link Dimension}s of this type.
     *
     * @return The dimension context
     */
    Context getDimensionContext() {
        return this.dimContext;
    }

    /**
     * Gets the default generator type of this dimension type. This one will be used
     * if there can't be one found in the world data.
     * 
     * @return the default generator type
     */
    public GeneratorType getDefaultGeneratorType() {
        return this.defaultGeneratorType;
    }

    /**
     * Creates a new dimension instance for the specified world.
     * 
     * @param world the world
     * @return the dimension instance
     */
    public T newDimension(LanternWorld world) {
        return this.supplier.apply(world, this);
    }

    public boolean doesKeepSpawnLoaded() {
        return this.keepSpawnLoaded;
    }

    @Override
    public Class<? extends Dimension> getDimensionClass() {
        return this.dimensionClass;
    }

    public boolean allowsPlayerRespawns() {
        return this.allowsPlayerRespawns;
    }

    public boolean doesWaterEvaporate() {
        return this.waterEvaporates;
    }

    public boolean hasSky() {
        return this.hasSky;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("dimensionClass", this.dimensionClass.getName());
    }
}
