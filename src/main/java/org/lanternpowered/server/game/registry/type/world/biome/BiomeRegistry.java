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
package org.lanternpowered.server.game.registry.type.world.biome;

import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.world.biome.BiomeType;

import java.util.Optional;

public interface BiomeRegistry extends CatalogRegistryModule<BiomeType> {

    /**
     * Registers a default {@link BiomeType} with a specific internal id.
     *
     * @param internalId the internal id
     * @param biomeType the biome type
     */
    void register(byte internalId, BiomeType biomeType);

    /**
     * Registers a new {@link BiomeType}.
     *
     * @param biomeType the biome type
     */
    void register(BiomeType biomeType);

    /**
     * Gets the internal id of the biome type.
     *
     * @param biomeType the biome type
     * @return the internal id
     */
    short getInternalId(BiomeType biomeType);

    /**
     * Gets a biome type by using it's internal id.
     *
     * @param internalId the internal id
     * @return the biome type
     */
    Optional<BiomeType> getByInternalId(int internalId);

}
