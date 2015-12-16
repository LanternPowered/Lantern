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
package org.lanternpowered.server.world.gen;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import org.lanternpowered.server.util.Lists2;
import org.lanternpowered.server.world.biome.LanternBiomeType;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.world.biome.BiomeGenerationSettings;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.gen.BiomeGenerator;
import org.spongepowered.api.world.gen.GenerationPopulator;
import org.spongepowered.api.world.gen.Populator;
import org.spongepowered.api.world.gen.WorldGenerator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class LanternWorldGenerator implements WorldGenerator {

    // Using concurrent lists, we have no idea what plugin devs will do with them...
    private final List<GenerationPopulator> generationPopulators = Lists2.nonNullOf(Lists.newCopyOnWriteArrayList());
    private final List<Populator> populators = Lists2.nonNullOf(Lists.newCopyOnWriteArrayList());

    // The biome generation settings
    private final Map<LanternBiomeType, BiomeGenerationSettings> biomeGenSettings = Maps.newConcurrentMap();

    private volatile GenerationPopulator baseGenerationPopulator;
    private volatile BiomeGenerator biomeGenerator;

    public LanternWorldGenerator(GenerationPopulator baseGenerationPopulator,
            BiomeGenerator biomeGenerator) {
        this.baseGenerationPopulator = checkNotNull(baseGenerationPopulator, "baseGenerationPopulator");
        this.biomeGenerator = checkNotNull(biomeGenerator, "biomeGenerator");
    }

    @Override
    public GenerationPopulator getBaseGenerationPopulator() {
        return this.baseGenerationPopulator;
    }

    @Override
    public void setBaseGenerationPopulator(GenerationPopulator generator) {
        this.baseGenerationPopulator = checkNotNull(generator, "generator");
    }

    @Override
    public List<GenerationPopulator> getGenerationPopulators() {
        return this.generationPopulators;
    }

    @Override
    public List<GenerationPopulator> getGenerationPopulators(Class<? extends GenerationPopulator> type) {
        return this.generationPopulators.stream().filter(type::isInstance).collect(GuavaCollectors.toImmutableList());
    }

    @Override
    public List<Populator> getPopulators() {
        return this.populators;
    }

    @Override
    public List<Populator> getPopulators(Class<? extends Populator> type) {
        return this.populators.stream().filter(type::isInstance).collect(GuavaCollectors.toImmutableList());
    }

    @Override
    public BiomeGenerator getBiomeGenerator() {
        return this.biomeGenerator;
    }

    @Override
    public void setBiomeGenerator(BiomeGenerator biomeGenerator) {
        this.biomeGenerator = checkNotNull(biomeGenerator, "biomeGenerator");
    }

    @Override
    public BiomeGenerationSettings getBiomeSettings(BiomeType type) {
        final LanternBiomeType biomeType = (LanternBiomeType) checkNotNull(type, "type");
        return this.biomeGenSettings.computeIfAbsent(biomeType, t -> t.getDefaultGenerationSettings().copy());
    }
}
