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
package org.lanternpowered.server.game.registry.type.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.lanternpowered.server.config.user.ban.LanternBanType;
import org.lanternpowered.server.game.registry.EarlyRegistration;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanType;
import org.spongepowered.api.util.ban.BanTypes;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class BanTypeRegistryModule implements CatalogRegistryModule<BanType> {

    @RegisterCatalog(BanTypes.class)
    private final Map<String, BanType> banTypes = Maps.newHashMap();

    @EarlyRegistration
    @Override
    public void registerDefaults() {
        List<BanType> types = Lists.newArrayList();
        types.add(new LanternBanType("profile", Ban.Profile.class));
        types.add(new LanternBanType("ip", Ban.Ip.class));
        types.forEach(type -> this.banTypes.put(type.getId(), type));
    }

    @Override
    public Optional<BanType> getById(String id) {
        return Optional.ofNullable(this.banTypes.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<BanType> getAll() {
        return ImmutableSet.copyOf(this.banTypes.values());
    }

}
