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
package org.lanternpowered.server.world.gen.flat;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.biome.BiomeType;

import java.util.List;

public final class FlatGeneratorSettings {

    private final List<FlatLayer> layers;
    private final BiomeType biomeType;
    private final DataContainer extraData;

    public FlatGeneratorSettings(BiomeType biomeType, List<FlatLayer> layers) {
        this(biomeType, layers, DataContainer.createNew());
    }

    public FlatGeneratorSettings(BiomeType biomeType, List<FlatLayer> layers, DataContainer extraData) {
        this.layers = ImmutableList.copyOf(checkNotNull(layers, "layers"));
        this.extraData = checkNotNull(extraData, "extraData");
        this.biomeType = checkNotNull(biomeType, "biomeType");
    }

    public BiomeType getBiomeType() {
        return this.biomeType;
    }

    public List<FlatLayer> getLayers() {
        return this.layers;
    }

    public DataContainer getExtraData() {
        return this.extraData;
    }

    @Override
    public String toString() {
        return FlatGeneratorSettingsParser.toString(this);
    }
}
