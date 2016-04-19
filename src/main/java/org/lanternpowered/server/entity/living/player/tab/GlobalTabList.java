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
package org.lanternpowered.server.entity.living.player.tab;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.profile.GameProfile;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalTabList {

    private static final GlobalTabList instance = new GlobalTabList();

    public static GlobalTabList getInstance() {
        return instance;
    }

    private final Map<UUID, GlobalTabListEntry> tabListEntries = new ConcurrentHashMap<>();

    void addEntry(GlobalTabListEntry tabListEntry) {
        this.tabListEntries.put(tabListEntry.getProfile().getUniqueId(), tabListEntry);
    }

    public Optional<GlobalTabListEntry> get(GameProfile gameProfile) {
        return Optional.ofNullable(this.tabListEntries.get(checkNotNull(gameProfile, "gameProfile").getUniqueId()));
    }

    public GlobalTabListEntry getOrCreate(GameProfile gameProfile) {
        return this.tabListEntries.computeIfAbsent(checkNotNull(gameProfile, "gameProfile").getUniqueId(),
                uuid -> new GlobalTabListEntry(this, gameProfile));
    }

    public GlobalTabListEntry remove(GameProfile gameProfile) {
        return this.tabListEntries.remove(checkNotNull(gameProfile, "gameProfile").getUniqueId());
    }
}
