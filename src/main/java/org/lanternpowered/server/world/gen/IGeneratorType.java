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

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.world.GeneratorType;

public interface IGeneratorType extends GeneratorType {

    DataQuery MINIMAL_SPAWN_HEIGHT = DataQuery.of('.', "Lantern.MinimalSpawnHeight");
    DataQuery GENERATOR_HEIGHT = DataQuery.of('.', "Lantern.GeneratorHeight");

    /**
     * Gets the minimal spawn height that is required for
     * this {@link GeneratorType}.
     *
     * @return The minimal spawn height
     */
    int getMinimalSpawnHeight();

    /**
     * Gets the maximum height that this {@link GeneratorType} will
     * generate.
     *
     * @return The generator height
     */
    int getGeneratorHeight();

    /**
     * Gets the minimal spawn height that is required
     * for the specified {@link GeneratorType}.
     *
     * @return The minimal spawn height
     */
    static int getMinimalSpawnHeight(GeneratorType generatorType) {
        if (generatorType instanceof IGeneratorType) {
            return ((IGeneratorType) generatorType).getMinimalSpawnHeight();
        }
        return generatorType.getGeneratorSettings().getInt(MINIMAL_SPAWN_HEIGHT).orElse(1);
    }

    /**
     * Gets the maximum height that the specified
     * {@link GeneratorType} will generate.
     *
     * @return The generator height
     */
    static int getGeneratorHeight(GeneratorType generatorType) {
        if (generatorType instanceof IGeneratorType) {
            return ((IGeneratorType) generatorType).getGeneratorHeight();
        }
        return generatorType.getGeneratorSettings().getInt(GENERATOR_HEIGHT).orElse(256);
    }
}
