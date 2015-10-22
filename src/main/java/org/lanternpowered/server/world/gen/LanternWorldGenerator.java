/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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

import org.lanternpowered.server.util.NonNullArrayList;
import org.spongepowered.api.world.gen.BiomeGenerator;
import org.spongepowered.api.world.gen.GeneratorPopulator;
import org.spongepowered.api.world.gen.Populator;
import org.spongepowered.api.world.gen.WorldGenerator;

public class LanternWorldGenerator implements WorldGenerator {

    private final List<GeneratorPopulator> generatorPopulators = new NonNullArrayList<>();
    private final List<Populator> populators = new NonNullArrayList<>();

    private GeneratorPopulator baseGeneratorPopulator;
    private BiomeGenerator biomeGenerator;

    public LanternWorldGenerator(GeneratorPopulator baseGeneratorPopulator,
            BiomeGenerator biomeGenerator) {
        this.baseGeneratorPopulator = checkNotNull(baseGeneratorPopulator, "baseGeneratorPopulator");
        this.biomeGenerator = checkNotNull(biomeGenerator, "biomeGenerator");
    }

    @Override
    public GeneratorPopulator getBaseGeneratorPopulator() {
        return this.baseGeneratorPopulator;
    }

    @Override
    public void setBaseGeneratorPopulator(GeneratorPopulator generator) {
        this.baseGeneratorPopulator = checkNotNull(generator, "generator");
    }

    @Override
    public List<GeneratorPopulator> getGeneratorPopulators() {
        return this.generatorPopulators;
    }

    @Override
    public List<Populator> getPopulators() {
        return this.populators;
    }

    @Override
    public BiomeGenerator getBiomeGenerator() {
        return this.biomeGenerator;
    }

    @Override
    public void setBiomeGenerator(BiomeGenerator biomeGenerator) {
        this.biomeGenerator = checkNotNull(biomeGenerator, "biomeGenerator");
    }
}
