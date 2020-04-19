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
package org.lanternpowered.server.world.gen;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.util.collect.Lists2;
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
    private final List<GenerationPopulator> generationPopulators = Lists2.nonNullOf(new CopyOnWriteArrayList<>());
    private final List<Populator> populators = Lists2.nonNullOf(new CopyOnWriteArrayList<>());

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
