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
package org.lanternpowered.server.world.biome;

import static com.google.common.base.Preconditions.checkState;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import org.lanternpowered.server.catalog.LanternCatalogTypeRegistry;
import org.spongepowered.api.world.biome.BiomeType;

import gnu.trove.TCollections;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TObjectShortHashMap;
import gnu.trove.map.hash.TShortObjectHashMap;

public class LanternBiomeRegistry extends LanternCatalogTypeRegistry<BiomeType> {

    // A lookup for the biomes by it's (internal) id
    private final TShortObjectMap<BiomeType> biomesById = TCollections.synchronizedMap(new TShortObjectHashMap<BiomeType>());
    private final TObjectShortMap<BiomeType> idsByBiome = TCollections.synchronizedMap(new TObjectShortHashMap<BiomeType>());

    // The counter for custom biome ids. (Non vanilla ones.)
    private final AtomicInteger biomeIdCounter = new AtomicInteger(256);

    // TODO: Make a string to id lookup to save/load the internal ids

    @Nullable
    public BiomeType getById(int internalId) {
        if (internalId < 0 || internalId >= Short.MAX_VALUE) {
            return null;
        }
        return this.biomesById.get((short) internalId);
    }

    @Nullable
    public Short getId(BiomeType biomeType) {
        if (!this.idsByBiome.containsKey(biomeType)) {
            return null;
        }
        return this.idsByBiome.get(biomeType);
    }

    /**
     * Registers a {@link BiomeType} with the specified biome id. This method
     * should only be used to register default (vanilla) biome types.
     * 
     * @param biomeType the biome type
     * @param biomeId the biome id
     */
    public void register(byte biomeId, BiomeType biomeType) {
        short biomeId0 = (short) (biomeId & 0xff);
        checkState(!this.biomesById.containsKey(biomeId), "Biome id already present! (" + biomeId + ")");
        checkState(!this.idsByBiome.containsKey(biomeType), "Biome type already present! (" + biomeType.getId() + ")");
        this.biomesById.put(biomeId0, biomeType);
        this.idsByBiome.put(biomeType, biomeId0);
        super.register(biomeType);
    }

    /**
     * Registers a {@link BiomeType}. This method should be used
     * to register custom (modded) biome types.
     * 
     * @param biomeType the biome type
     */
    @Override
    public void register(BiomeType catalogType) {
        int biomeId = biomeIdCounter.getAndIncrement();
        if (biomeId > Short.MAX_VALUE) {
            throw new IllegalStateException("Exceeded the biome limit. (" + Short.MAX_VALUE + ")");
        }
        biomesById.put((short) biomeId, catalogType);
        idsByBiome.put(catalogType, (short) biomeId);
        super.register(catalogType);
    }
}
