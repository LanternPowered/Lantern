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
package org.lanternpowered.server.game.registry.type.effect;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.lanternpowered.server.effect.sound.LanternSoundType;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public final class SoundTypeRegistryModule implements CatalogRegistryModule<SoundType> {

    @RegisterCatalog(SoundTypes.class)
    private final Map<String, SoundType> soundTypes = Maps.newHashMap();

    @Override
    public void registerDefaults() {
        final Gson gson = new Gson();
        final JsonArray array = gson.fromJson(new BufferedReader(new InputStreamReader(SoundTypeRegistryModule.class
                .getResourceAsStream("/assets/lantern/internal/sound-events.json"))), JsonArray.class);

        for (int i = 0; i < array.size(); i++) {
            String name = array.get(i).getAsString();
            LanternSoundType soundType = new LanternSoundType(name, i);
            this.soundTypes.put(name, soundType);
            String field = name.replaceAll("\\.", "_");
            this.soundTypes.put(field, soundType);
            this.soundTypes.put("minecraft:" + field, soundType);
        }
    }

    @Override
    public Optional<SoundType> getById(String id) {
        return Optional.ofNullable(this.soundTypes.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<SoundType> getAll() {
        return ImmutableSet.copyOf(this.soundTypes.values());
    }

}
