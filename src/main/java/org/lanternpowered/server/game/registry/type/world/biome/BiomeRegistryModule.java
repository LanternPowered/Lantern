/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.server.game.registry.type.world.biome;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TObjectShortHashMap;
import gnu.trove.map.hash.TShortObjectHashMap;
import org.lanternpowered.server.world.biome.LanternBiomeType;
import org.spongepowered.api.registry.AlternateCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

// TODO Lookup biome registry data from the worlds.
public final class BiomeRegistryModule implements BiomeRegistry, AlternateCatalogRegistryModule<BiomeType> {

    private static final BiomeRegistryModule INSTANCE = new BiomeRegistryModule();

    public static BiomeRegistryModule get() {
        return INSTANCE;
    }

    @RegisterCatalog(BiomeTypes.class) private final Map<String, BiomeType> biomeTypes = Maps.newHashMap();

    private final TShortObjectMap<BiomeType> biomeTypeByInternalId = new TShortObjectHashMap<>();
    private final TObjectShortMap<BiomeType> internalIdByBiomeType = new TObjectShortHashMap<>();

    private int biomeIdCounter = 1024;

    private BiomeRegistryModule() {
    }

    @Override
    public Map<String, BiomeType> provideCatalogMap() {
        Map<String, BiomeType> mappings = Maps.newHashMap();
        this.biomeTypes.forEach((key, value) -> {
            if (key.startsWith("minecraft:")) {
                mappings.put(key.replace("minecraft:", ""), value);
            }
        });
        return mappings;
    }

    @Override
    public void register(byte internalId, BiomeType biomeType) {
        this.register((short) (internalId & 0xff), biomeType);
    }

    private void register(short internalId, BiomeType biomeType) {
        checkNotNull(biomeType, "biomeType");
        checkState(!this.biomeTypeByInternalId.containsKey(internalId), "Biome internal id already present! (" + internalId + ")");
        checkState(!this.internalIdByBiomeType.containsKey(biomeType), "Biome type already present! (" + biomeType.getId() + ")");
        String id = biomeType.getId().toLowerCase();
        checkState(!this.biomeTypes.containsKey(id), "Identifier is already used! (" + id + ")");
        this.biomeTypeByInternalId.put(internalId, biomeType);
        this.internalIdByBiomeType.put(biomeType, internalId);
        this.biomeTypes.put(id, biomeType);
    }

    private int nextInternalId() {
        int internalId;
        do {
            internalId = this.biomeIdCounter++;
        } while (this.biomeTypeByInternalId.containsKey((short) internalId));
        return internalId;
    }

    @Override
    public void register(BiomeType biomeType) {
        this.register((short) this.nextInternalId(), biomeType);
    }

    @Override
    public short getInternalId(BiomeType biomeType) {
        return this.internalIdByBiomeType.get(checkNotNull(biomeType, "biomeType"));
    }

    @Override
    public Optional<BiomeType> getByInternalId(int internalId) {
        return Optional.ofNullable(this.biomeTypeByInternalId.get((short) internalId));
    }

    @Override
    public Optional<BiomeType> getById(String id) {
        if (checkNotNull(id, "identifier").indexOf(':') == -1) {
            id = "minecraft:" + id;
        }
        return Optional.ofNullable(this.biomeTypes.get(id.toLowerCase()));
    }

    @Override
    public Collection<BiomeType> getAll() {
        return ImmutableSet.copyOf(this.biomeTypes.values());
    }

    @Override
    public void registerDefaults() {
        this.register((byte) 0, new LanternBiomeType("minecraft", "ocean"));
        this.register((byte) 1, new LanternBiomeType("minecraft", "plains"));
    }
}
