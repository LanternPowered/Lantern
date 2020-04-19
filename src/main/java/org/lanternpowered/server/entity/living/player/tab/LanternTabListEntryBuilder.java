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
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class LanternTabListEntryBuilder implements TabListEntry.Builder {

    @Nullable private LanternTabList list;
    @Nullable private GameProfile profile;
    @Nullable private Text displayName;
    private int latency;
    @Nullable private GameMode gameMode;

    @Override
    public LanternTabListEntryBuilder list(TabList list) {
        this.list = (LanternTabList) checkNotNull(list, "list");
        return this;
    }

    @Override
    public LanternTabListEntryBuilder profile(GameProfile profile) {
        this.profile = checkNotNull(profile, "profile");
        return this;
    }

    @Override
    public LanternTabListEntryBuilder displayName(@Nullable Text displayName) {
        this.displayName = displayName;
        return this;
    }

    @Override
    public LanternTabListEntryBuilder latency(int latency) {
        this.latency = latency;
        return this;
    }

    @Override
    public LanternTabListEntryBuilder gameMode(GameMode gameMode) {
        this.gameMode = checkNotNull(gameMode, "game mode");
        return this;
    }

    @Override
    public LanternTabListEntry build() {
        checkState(this.list != null, "list must be set");
        checkState(this.profile != null, "profile must be set");
        checkState(this.gameMode != null, "game mode must be set");
        return new LanternTabListEntry(GlobalTabList.getInstance().getOrCreate(this.profile), this.list, this.gameMode,
                this.latency, this.displayName);
    }

    @Override
    public LanternTabListEntryBuilder from(TabListEntry value) {
        this.list = (LanternTabList) checkNotNull(value.getList(), "list");
        this.profile = checkNotNull(value.getProfile(), "profile");
        this.displayName = value.getDisplayName().orElse(null);
        this.latency = value.getLatency();
        this.gameMode = checkNotNull(value.getGameMode(), "game mode");
        return this;
    }

    @Override
    public TabListEntry.Builder reset() {
        this.list = null;
        this.profile = null;
        this.displayName = null;
        this.latency = 0;
        this.gameMode = null;
        return this;
    }

}
