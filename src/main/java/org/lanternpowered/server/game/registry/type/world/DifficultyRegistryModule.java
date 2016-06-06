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
package org.lanternpowered.server.game.registry.type.world;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class DifficultyRegistryModule implements CatalogRegistryModule<Difficulty> {

    public static DifficultyRegistryModule getInstance() {
        return Holder.INSTANCE;
    }

    @RegisterCatalog(Difficulties.class)
    private final Map<String, Difficulty> difficulties = new HashMap<>();
    private final Int2ObjectMap<Difficulty> difficultiesByInternalId = new Int2ObjectOpenHashMap<>();

    @Override
    public void registerDefaults() {
        List<LanternDifficulty> types = Lists.newArrayList();
        types.add(new LanternDifficulty("peaceful", 0));
        types.add(new LanternDifficulty("easy", 1));
        types.add(new LanternDifficulty("normal", 2));
        types.add(new LanternDifficulty("hard", 3));
        types.forEach(type -> {
            this.difficulties.put(type.getId(), type);
            this.difficultiesByInternalId.put(type.getInternalId(), type);
        });
    }

    @Override
    public Optional<Difficulty> getById(String id) {
        return Optional.ofNullable(this.difficulties.get(checkNotNull(id).toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public Collection<Difficulty> getAll() {
        return ImmutableSet.copyOf(this.difficulties.values());
    }

    public Optional<Difficulty> getByInternalId(int internalId) {
        return Optional.ofNullable(this.difficultiesByInternalId.get(internalId));
    }

    private static final class Holder {
        private static final DifficultyRegistryModule INSTANCE = new DifficultyRegistryModule();
    }
}
