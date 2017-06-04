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
package org.lanternpowered.server.world.gen.skylands;

import org.lanternpowered.server.world.gen.LanternGeneratorType;
import org.lanternpowered.server.world.gen.LanternWorldGenerator;
import org.spongepowered.api.extra.modifier.skylands.SkylandsBiomeGenerator;
import org.spongepowered.api.extra.modifier.skylands.SkylandsGrassPopulator;
import org.spongepowered.api.extra.modifier.skylands.SkylandsGroundCoverPopulator;
import org.spongepowered.api.extra.modifier.skylands.SkylandsTerrainGenerator;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.GenerationPopulator;
import org.spongepowered.api.world.gen.WorldGenerator;

import java.util.List;

public final class SkylandsGeneratorType extends LanternGeneratorType {

    public SkylandsGeneratorType(String pluginId, String name) {
        super(pluginId, name);
    }

    @Override
    public WorldGenerator createGenerator(World world) {
        final LanternWorldGenerator generator = new LanternWorldGenerator(world, new SkylandsBiomeGenerator(), new SkylandsTerrainGenerator());
        final List<GenerationPopulator> generatorPopulators = generator.getGenerationPopulators();
        generatorPopulators.add(new SkylandsGroundCoverPopulator());
        generatorPopulators.add(new SkylandsGrassPopulator());
        return generator;
    }
}
