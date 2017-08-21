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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import org.lanternpowered.server.catalog.VirtualCatalogType;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.forge.ForgeCatalogRegistryModule;
import org.lanternpowered.server.game.registry.forge.ForgeRegistryData;
import org.lanternpowered.server.world.biome.LanternBiomeType;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.util.Optional;

// TODO Lookup biome registry data from the worlds.
public final class BiomeRegistryModule extends AdditionalPluginCatalogRegistryModule<BiomeType> implements BiomeRegistry,
        ForgeCatalogRegistryModule<BiomeType> {

    private static final BiomeRegistryModule INSTANCE = new BiomeRegistryModule();

    public static BiomeRegistryModule get() {
        return INSTANCE;
    }

    private final Short2ObjectMap<BiomeType> biomeTypeByInternalId = new Short2ObjectOpenHashMap<>();
    private final Object2ShortMap<BiomeType> internalIdByBiomeType = new Object2ShortOpenHashMap<>();

    private int biomeIdCounter = 1024;

    private BiomeRegistryModule() {
        super(BiomeTypes.class);
    }

    @Override
    public ForgeRegistryData getRegistryData() {
        final Object2IntMap<String> map = new Object2IntOpenHashMap<>();
        this.biomeTypeByInternalId.short2ObjectEntrySet().forEach(entry -> {
            if (!(entry.getValue() instanceof VirtualCatalogType)) {
                map.put(entry.getValue().getId(), entry.getShortKey());
            }
        });
        return new ForgeRegistryData("minecraft:biomes", map);
    }

    @Override
    public void register(byte internalId, BiomeType biomeType) {
        this.register((short) (internalId & 0xff), biomeType);
    }

    private void register(short internalId, BiomeType biomeType) {
        checkState(!this.biomeTypeByInternalId.containsKey(internalId), "The internal id is already used: %s", internalId);
        super.register(biomeType);
        this.biomeTypeByInternalId.put(internalId, biomeType);
        this.internalIdByBiomeType.put(biomeType, internalId);
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
        return this.internalIdByBiomeType.getShort(checkNotNull(biomeType, "biomeType"));
    }

    @Override
    public Optional<BiomeType> getByInternalId(int internalId) {
        return Optional.ofNullable(this.biomeTypeByInternalId.get((short) internalId));
    }

    @Override
    public void registerDefaults() {
        register((byte) 0, new LanternBiomeType("minecraft", "ocean"));
        register((byte) 1, new LanternBiomeType("minecraft", "plains"));
        register((byte) 2, new LanternBiomeType("minecraft", "desert"));
        register((byte) 3, new LanternBiomeType("minecraft", "extreme_hills"));
        register((byte) 4, new LanternBiomeType("minecraft", "forest"));
        register((byte) 5, new LanternBiomeType("minecraft", "taiga"));
        register((byte) 6, new LanternBiomeType("minecraft", "swampland"));
        register((byte) 7, new LanternBiomeType("minecraft", "river"));
        register((byte) 8, new LanternBiomeType("minecraft", "hell"));
        register((byte) 9, new LanternBiomeType("minecraft", "sky"));
        register((byte) 10, new LanternBiomeType("minecraft", "frozen_ocean"));
        register((byte) 11, new LanternBiomeType("minecraft", "frozen_river"));
        register((byte) 12, new LanternBiomeType("minecraft", "ice_flats"));
        register((byte) 13, new LanternBiomeType("minecraft", "ice_mountains"));
        register((byte) 14, new LanternBiomeType("minecraft", "mushroom_island"));
        register((byte) 15, new LanternBiomeType("minecraft", "mushroom_island_shore"));
        register((byte) 16, new LanternBiomeType("minecraft", "beaches"));
        register((byte) 17, new LanternBiomeType("minecraft", "desert_hills"));
        register((byte) 18, new LanternBiomeType("minecraft", "forest_hills"));
        register((byte) 19, new LanternBiomeType("minecraft", "taiga_hills"));
        register((byte) 20, new LanternBiomeType("minecraft", "smaller_extreme_hills"));
        register((byte) 21, new LanternBiomeType("minecraft", "jungle"));
        register((byte) 22, new LanternBiomeType("minecraft", "jungle_hills"));
        register((byte) 23, new LanternBiomeType("minecraft", "jungle_edge"));
        register((byte) 24, new LanternBiomeType("minecraft", "deep_ocean"));
        register((byte) 25, new LanternBiomeType("minecraft", "stone_beach"));
        register((byte) 26, new LanternBiomeType("minecraft", "cold_beach"));
        register((byte) 27, new LanternBiomeType("minecraft", "birch_forest"));
        register((byte) 28, new LanternBiomeType("minecraft", "birch_forest_hills"));
        register((byte) 29, new LanternBiomeType("minecraft", "roofed_forest"));
        register((byte) 30, new LanternBiomeType("minecraft", "taiga_cold"));
        register((byte) 31, new LanternBiomeType("minecraft", "taiga_cold_hills"));
        register((byte) 32, new LanternBiomeType("minecraft", "redwood_taiga"));
        register((byte) 33, new LanternBiomeType("minecraft", "redwood_taiga_hills"));
        register((byte) 34, new LanternBiomeType("minecraft", "extreme_hills_with_trees"));
        register((byte) 35, new LanternBiomeType("minecraft", "savanna"));
        register((byte) 36, new LanternBiomeType("minecraft", "savanna_rock"));
        register((byte) 37, new LanternBiomeType("minecraft", "mesa"));
        register((byte) 38, new LanternBiomeType("minecraft", "mesa_rock"));
        register((byte) 39, new LanternBiomeType("minecraft", "mesa_clear_rock"));
        register((byte) 127, new LanternBiomeType("minecraft", "void"));
        register((byte) 129, new LanternBiomeType("minecraft", "mutated_plains"));
        register((byte) 130, new LanternBiomeType("minecraft", "mutated_desert"));
        register((byte) 131, new LanternBiomeType("minecraft", "mutated_extreme_hills"));
        register((byte) 132, new LanternBiomeType("minecraft", "mutated_forest"));
        register((byte) 133, new LanternBiomeType("minecraft", "mutated_taiga"));
        register((byte) 134, new LanternBiomeType("minecraft", "mutated_swampland"));
        register((byte) 140, new LanternBiomeType("minecraft", "mutated_ice_flats"));
        register((byte) 149, new LanternBiomeType("minecraft", "mutated_jungle"));
        register((byte) 151, new LanternBiomeType("minecraft", "mutated_jungle_edge"));
        register((byte) 155, new LanternBiomeType("minecraft", "mutated_birch_forest"));
        register((byte) 156, new LanternBiomeType("minecraft", "mutated_birch_forest_hills"));
        register((byte) 157, new LanternBiomeType("minecraft", "mutated_roofed_forest"));
        register((byte) 158, new LanternBiomeType("minecraft", "mutated_taiga_cold"));
        register((byte) 160, new LanternBiomeType("minecraft", "mutated_redwood_taiga"));
        register((byte) 161, new LanternBiomeType("minecraft", "mutated_redwood_taiga_hills"));
        register((byte) 162, new LanternBiomeType("minecraft", "mutated_extreme_hills_with_trees"));
        register((byte) 163, new LanternBiomeType("minecraft", "mutated_savanna"));
        register((byte) 164, new LanternBiomeType("minecraft", "mutated_savanna_rock"));
        register((byte) 165, new LanternBiomeType("minecraft", "mutated_mesa"));
        register((byte) 166, new LanternBiomeType("minecraft", "mutated_mesa_rock"));
        register((byte) 167, new LanternBiomeType("minecraft", "mutated_mesa_clear_rock"));
    }
}
