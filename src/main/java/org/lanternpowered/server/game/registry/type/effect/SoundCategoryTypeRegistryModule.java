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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.lanternpowered.server.effect.sound.LanternSoundCategoryType;
import org.spongepowered.api.effect.sound.SoundCategoryType;
import org.spongepowered.api.effect.sound.SoundCategoryTypes;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SoundCategoryTypeRegistryModule implements CatalogRegistryModule<SoundCategoryType> {

    @RegisterCatalog(SoundCategoryTypes.class)
    private final Map<String, SoundCategoryType> soundCategoryTypes = Maps.newHashMap();

    @Override
    public void registerDefaults() {
        List<SoundCategoryType> types = Lists.newArrayList();
        types.add(new LanternSoundCategoryType("master", 0));
        types.add(new LanternSoundCategoryType("music", 1));
        types.add(new LanternSoundCategoryType("record", 2));
        types.add(new LanternSoundCategoryType("weather", 3));
        types.add(new LanternSoundCategoryType("block", 4));
        types.add(new LanternSoundCategoryType("hostile", 5));
        types.add(new LanternSoundCategoryType("neutral", 6));
        types.add(new LanternSoundCategoryType("player", 7));
        types.add(new LanternSoundCategoryType("ambient", 8));
        types.add(new LanternSoundCategoryType("voice", 9));
        types.forEach(type -> this.soundCategoryTypes.put(type.getId(), type));
    }

    @Override
    public Optional<SoundCategoryType> getById(String id) {
        return Optional.ofNullable(this.soundCategoryTypes.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<SoundCategoryType> getAll() {
        return ImmutableSet.copyOf(this.soundCategoryTypes.values());
    }

}
