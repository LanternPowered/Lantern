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
package org.lanternpowered.server.game.registry.type.bossbar;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.bossbar.LanternBossBarColor;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class BossBarColorRegistryModule implements CatalogRegistryModule<BossBarColor> {

    @RegisterCatalog(BossBarColors.class)
    private final Map<String, BossBarColor> bossBarColors = new HashMap<>();

    @Override
    public void registerDefaults() {
        List<BossBarColor> types = new ArrayList<>();
        types.add(new LanternBossBarColor("pink", 0));
        types.add(new LanternBossBarColor("blue", 1));
        types.add(new LanternBossBarColor("red", 2));
        types.add(new LanternBossBarColor("green", 3));
        types.add(new LanternBossBarColor("yellow", 4));
        types.add(new LanternBossBarColor("purple", 5));
        types.add(new LanternBossBarColor("white", 6));
        types.forEach(type -> this.bossBarColors.put(type.getId(), type));
    }

    @Override
    public Optional<BossBarColor> getById(String id) {
        return Optional.ofNullable(this.bossBarColors.get(checkNotNull(id).toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public Collection<BossBarColor> getAll() {
        return ImmutableSet.copyOf(this.bossBarColors.values());
    }

}
