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

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.util.ArrayList;
import java.util.List;

public final class FlatNetherGeneratorType extends AbstractFlatGeneratorType {

    public FlatNetherGeneratorType(String pluginId, String name) {
        super(pluginId, name);
        setDefaultGeneratorHeight(128);
        setDefaultMinimalSpawnHeight(4);
    }

    @Override
    protected FlatGeneratorSettings getDefaultSettings() {
        final List<FlatLayer> layers = new ArrayList<>(5);
        layers.add(new FlatLayer(BlockTypes.BEDROCK, 1));
        layers.add(new FlatLayer(BlockTypes.NETHERRACK, 3));
        layers.add(new FlatLayer(BlockTypes.AIR, 120));
        layers.add(new FlatLayer(BlockTypes.NETHERRACK, 3));
        layers.add(new FlatLayer(BlockTypes.BEDROCK, 1));
        return new FlatGeneratorSettings(BiomeTypes.HELL, layers);
    }
}
