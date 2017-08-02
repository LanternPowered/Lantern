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
package org.lanternpowered.server.game.version;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

@Singleton
public final class MinecraftVersionCache {

    private final Int2ObjectMap<LanternMinecraftVersion> versionsByProtocol = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<LanternMinecraftVersion> legacyVersionsByProtocol = new Int2ObjectOpenHashMap<>();

    @Inject
    private MinecraftVersionCache() {
        load();
    }

    public void load() {
        load(MinecraftVersionCache.class.getResourceAsStream("/internal/mc_versions.json"), false);
        load(MinecraftVersionCache.class.getResourceAsStream("/internal/mc_legacy_versions.json"), true);
    }

    private void load(InputStream inputStream, boolean legacy) {
        final Gson gson = new Gson();
        final JsonArray array = gson.fromJson(new BufferedReader(new InputStreamReader(inputStream)), JsonArray.class);

        for (int i = 0; i < array.size(); i++) {
            final JsonObject obj = array.get(i).getAsJsonObject();

            final String name = obj.get("name").getAsString();
            final int protocol = obj.get("version").getAsInt();

            final LanternMinecraftVersion version = new LanternMinecraftVersion(name, protocol, legacy);
            (legacy ? this.legacyVersionsByProtocol : this.versionsByProtocol).put(protocol, version);
        }
    }

    public Optional<LanternMinecraftVersion> getVersion(int protocol, boolean legacy) {
        return Optional.ofNullable((legacy ? this.legacyVersionsByProtocol : this.versionsByProtocol).get(protocol));
    }

    public LanternMinecraftVersion getVersionOrUnknown(int protocol, boolean legacy) {
        return getVersion(protocol, legacy).orElseGet(
                () -> legacy ? LanternMinecraftVersion.UNKNOWN_LEGACY : LanternMinecraftVersion.UNKNOWN);
    }
}
