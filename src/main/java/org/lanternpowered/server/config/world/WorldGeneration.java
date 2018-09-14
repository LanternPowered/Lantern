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
package org.lanternpowered.server.config.world;

import static com.google.common.base.Preconditions.checkNotNull;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@ConfigSerializable
public final class WorldGeneration {

    @Setting(value = "modifiers", comment = "The generation modifiers to apply to this world.")
    private List<String> generationModifiers = new ArrayList<>();

    @Setting(value = "seed", comment = "The seed that will be used to generate this world.")
    private long seed = ThreadLocalRandom.current().nextLong();

    @Setting(value = "generate-spawn-on-load", comment =
            "Whether the spawn should be generated when the world loads,\n" +
            "otherwise will the spawn generate when a player joins.")
    private boolean generateSpawnOnLoad = true;

    @Setting(value = "type")
    private GeneratorType generatorType = GeneratorTypes.OVERWORLD;

    @Setting(value = "settings")
    private DataContainer generatorSettings = DataContainer.createNew();

    public List<String> getGenerationModifiers() {
        return this.generationModifiers;
    }

    public long getSeed() {
        return this.seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public boolean doesGenerateSpawnOnLoad() {
        return this.generateSpawnOnLoad;
    }

    public void setGenerateSpawnOnLoad(boolean state) {
        this.generateSpawnOnLoad = state;
    }

    public GeneratorType getGeneratorType() {
        return this.generatorType;
    }

    public void setGeneratorType(GeneratorType generatorType) {
        this.generatorType = checkNotNull(generatorType, "generatorType");
    }

    public DataContainer getGeneratorSettings() {
        return this.generatorSettings;
    }

    public void setGeneratorSettings(DataContainer generatorSettings) {
        this.generatorSettings = checkNotNull(generatorSettings, "generatorSettings");
    }
}
