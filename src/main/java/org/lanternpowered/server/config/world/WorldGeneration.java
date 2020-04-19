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
package org.lanternpowered.server.config.world;

import static com.google.common.base.Preconditions.checkNotNull;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.world.gen.GeneratorType;
import org.spongepowered.api.world.gen.GeneratorTypes;

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
