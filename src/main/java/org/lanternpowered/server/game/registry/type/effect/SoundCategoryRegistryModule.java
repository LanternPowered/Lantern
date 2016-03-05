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
import org.lanternpowered.server.effect.sound.LanternSoundCategory;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SoundCategoryRegistryModule implements CatalogRegistryModule<SoundCategory> {

    @RegisterCatalog(SoundCategories.class)
    private final Map<String, SoundCategory> soundCategoryTypes = Maps.newHashMap();

    @Override
    public void registerDefaults() {
        List<SoundCategory> types = Lists.newArrayList();
        types.add(new LanternSoundCategory("minecraft", "master", 0));
        types.add(new LanternSoundCategory("minecraft", "music", 1));
        types.add(new LanternSoundCategory("minecraft", "record", 2));
        types.add(new LanternSoundCategory("minecraft", "weather", 3));
        types.add(new LanternSoundCategory("minecraft", "block", 4));
        types.add(new LanternSoundCategory("minecraft", "hostile", 5));
        types.add(new LanternSoundCategory("minecraft", "neutral", 6));
        types.add(new LanternSoundCategory("minecraft", "player", 7));
        types.add(new LanternSoundCategory("minecraft", "ambient", 8));
        types.add(new LanternSoundCategory("minecraft", "voice", 9));
        types.forEach(type -> {
            this.soundCategoryTypes.put(type.getId(), type);
            this.soundCategoryTypes.put(type.getName(), type);
        });
    }

    @Override
    public Optional<SoundCategory> getById(String id) {
        return Optional.ofNullable(this.soundCategoryTypes.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<SoundCategory> getAll() {
        return ImmutableSet.copyOf(this.soundCategoryTypes.values());
    }

}
