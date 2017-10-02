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
package org.lanternpowered.server.game.registry.type.effect.sound;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.lanternpowered.server.effect.sound.LanternSoundType;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public final class SoundTypeRegistryModule extends AdditionalPluginCatalogRegistryModule<SoundType> {

    public SoundTypeRegistryModule() {
        super(SoundTypes.class);
    }

    @Override
    public void registerDefaults() {
        final Gson gson = new Gson();
        final JsonArray array = gson.fromJson(new BufferedReader(new InputStreamReader(SoundTypeRegistryModule.class
                .getResourceAsStream("/internal/sound_events.json"))), JsonArray.class);
        for (int i = 0; i < array.size(); i++) {
            final String name = array.get(i).getAsString();
            final String id = name.replaceAll("\\.", "_");
            register(new LanternSoundType("minecraft", id, name, i));
        }
    }
}
