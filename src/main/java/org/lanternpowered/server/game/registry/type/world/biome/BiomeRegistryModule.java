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

import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.world.biome.LanternBiomeType;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.util.Optional;

// TODO Lookup biome registry data from the worlds.
public final class BiomeRegistryModule extends AdditionalPluginCatalogRegistryModule<BiomeType> implements BiomeRegistry {

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
        register((byte) 8, new LanternBiomeType("minecraft", "hell"));
        register((byte) 9, new LanternBiomeType("minecraft", "sky"));
    }
}
