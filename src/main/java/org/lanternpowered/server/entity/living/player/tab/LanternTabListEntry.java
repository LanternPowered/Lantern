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

import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.Optional;

import javax.annotation.Nullable;

public final class LanternTabListEntry implements TabListEntry {

    private final LanternTabList tabList;
    private final GameProfile gameProfile;

    private Optional<Text> displayName;
    private GameMode gameMode;
    private int latency;

    // Whether this tab list entry is attached to the tab list
    boolean attached;

    LanternTabListEntry(LanternTabList tabList, GameProfile gameProfile, GameMode gameMode, int latency,
            @Nullable Text displayName) {
        this.displayName = Optional.ofNullable(displayName);
        this.gameProfile = gameProfile;
        this.gameMode = gameMode;
        this.tabList = tabList;
        this.latency = latency;
    }

    @Override
    public LanternTabList getList() {
        return this.tabList;
    }

    @Override
    public GameProfile getProfile() {
        return this.gameProfile;
    }

    @Override
    public Optional<Text> getDisplayName() {
        return this.displayName;
    }

    @Override
    public LanternTabListEntry setDisplayName(@Nullable Text displayName) {
        this.displayName = Optional.ofNullable(displayName);
        if (this.attached) {
            this.tabList.getPlayer().getConnection().send(new MessagePlayOutTabListEntries(Collections.singletonList(
                    new MessagePlayOutTabListEntries.Entry.UpdateDisplayName(this.gameProfile, displayName))));
        }
        return this;
    }

    @Override
    public int getLatency() {
        return this.latency;
    }

    @Override
    public LanternTabListEntry setLatency(int latency) {
        this.latency = latency;
        if (this.attached) {
            this.tabList.getPlayer().getConnection().send(new MessagePlayOutTabListEntries(Collections.singletonList(
                    new MessagePlayOutTabListEntries.Entry.UpdateLatency(this.gameProfile, latency))));
        }
        return this;
    }

    @Override
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Override
    public LanternTabListEntry setGameMode(GameMode gameMode) {
        this.gameMode = checkNotNull(gameMode, "gameMode");
        if (this.attached) {
            this.tabList.getPlayer().getConnection().send(new MessagePlayOutTabListEntries(Collections.singletonList(
                    new MessagePlayOutTabListEntries.Entry.UpdateGameMode(this.gameProfile, gameMode))));
        }
        return this;
    }
}
