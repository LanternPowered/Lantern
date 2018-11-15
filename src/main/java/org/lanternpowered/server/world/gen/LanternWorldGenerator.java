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
package org.lanternpowered.server.world.gen;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.api.util.collect.NonNullMutableList;
import org.lanternpowered.server.world.biome.LanternBiomeType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeGenerationSettings;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.gen.BiomeGenerator;
import org.spongepowered.api.world.gen.GenerationPopulator;
import org.spongepowered.api.world.gen.Populator;
import org.spongepowered.api.world.gen.WorldGenerator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class LanternWorldGenerator implements WorldGenerator {

    // Using concurrent lists, we have no idea what plugin devs will do with them...
    private final List<GenerationPopulator> generationPopulators = new NonNullMutableList<>(new CopyOnWriteArrayList<>());
    private final List<Populator> populators = new NonNullMutableList<>(new CopyOnWriteArrayList<>());

    // The biome generation settings
    private final Map<LanternBiomeType, BiomeGenerationSettings> biomeGenSettings = new ConcurrentHashMap<>();

    private volatile GenerationPopulator baseGenerationPopulator;
    private volatile BiomeGenerator biomeGenerator;

    private final World world;

    public LanternWorldGenerator(World world, BiomeGenerator biomeGenerator, GenerationPopulator baseGenerationPopulator) {
        this.world = checkNotNull(world, "world");
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
        return this.generationPopulators.stream().filter(type::isInstance).collect(ImmutableList.toImmutableList());
    }

    @Override
    public List<Populator> getPopulators() {
        return this.populators;
    }

    @Override
    public List<Populator> getPopulators(Class<? extends Populator> type) {
        return this.populators.stream().filter(type::isInstance).collect(ImmutableList.toImmutableList());
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
        return this.biomeGenSettings.computeIfAbsent(biomeType, t -> t.createDefaultGenerationSettings(this.world));
    }
}
