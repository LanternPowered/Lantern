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

import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.world.gen.GeneratorType;

public interface IGeneratorType extends GeneratorType {

    DataQuery MINIMAL_SPAWN_HEIGHT = DataQuery.of('.', "Lantern.MinimalSpawnHeight");
    DataQuery GENERATOR_HEIGHT = DataQuery.of('.', "Lantern.GeneratorHeight");
    DataQuery SEA_LEVEL = DataQuery.of('.', "Lantern.SeaLevel");

    /**
     * Gets the minimal spawn height that is required for
     * this {@link GeneratorType}.
     *
     * @param settings The settings of the generator
     * @return The minimal spawn height
     */
    int getMinimalSpawnHeight(DataView settings);

    /**
     * Gets the maximum height that this {@link GeneratorType} will
     * generate.
     *
     * @param settings The settings of the generator
     * @return The generator height
     */
    int getGeneratorHeight(DataView settings);

    /**
     * Gets the sea level for this {@link GeneratorType}.
     *
     * @param settings The settings of the generator
     * @return The sea level
     */
    int getSeaLevel(DataView settings);

    /**
     * Gets the minimal spawn height that is required
     * for the specified {@link GeneratorType}.
     *
     * @param settings The settings of the generator
     * @return The minimal spawn height
     */
    static int getMinimalSpawnHeight(GeneratorType generatorType, DataView settings) {
        if (generatorType instanceof IGeneratorType) {
            return ((IGeneratorType) generatorType).getMinimalSpawnHeight(settings);
        }
        return settings.getInt(MINIMAL_SPAWN_HEIGHT).orElse(1);
    }

    /**
     * Gets the maximum height that the specified
     * {@link GeneratorType} will generate.
     *
     * @param settings The settings of the generator
     * @return The generator height
     */
    static int getGeneratorHeight(GeneratorType generatorType, DataView settings) {
        if (generatorType instanceof IGeneratorType) {
            return ((IGeneratorType) generatorType).getGeneratorHeight(settings);
        }
        return settings.getInt(GENERATOR_HEIGHT).orElse(256);
    }

    /**
     * Gets the sea level for the specified {@link GeneratorType}.
     *
     * @param generatorType The generator type
     * @param settings The settings of the generator
     * @return The sea level
     */
    static int getSeaLevel(GeneratorType generatorType, DataView settings) {
        if (generatorType instanceof IGeneratorType) {
            return ((IGeneratorType) generatorType).getSeaLevel(settings);
        }
        return settings.getInt(SEA_LEVEL).orElse(62);
    }
}
