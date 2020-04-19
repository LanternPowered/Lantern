/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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
