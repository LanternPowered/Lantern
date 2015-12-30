/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries.Entry;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.util.Sets2;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.tab.PlayerTabInfo;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public final class LanternPlayerTabInfo implements PlayerTabInfo {

    // All the tab lists this tab info is attached to
    final Set<LanternTabList> tabLists = Sets2.newWeakHashSet();

    // The changes of the tab info since the last tick
    static class UpdateEntry {

        final LanternPlayerTabInfo tabInfo;

        boolean gameProfileOrName;
        boolean gameMode;
        boolean displayName;
        boolean latency;
        boolean remove;

        List<Entry> entryCache;

        public UpdateEntry(LanternPlayerTabInfo tabInfo) {
            this.tabInfo = tabInfo;
        }

    }

    @Nullable UpdateEntry updateEntry;

    // The unique id of the player this info is attached to
    final UUID uniqueId;

    @Nullable Text displayName;
    LanternGameProfile gameProfile;
    GameMode gameMode;
    String name;

    int latency;

    public LanternPlayerTabInfo(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    UpdateEntry updateEntry() {
        if (this.updateEntry == null) {
            this.updateEntry = new UpdateEntry(this);
            for (LanternTabList tabList : this.tabLists) {
                tabList.updateEntries.add(this.updateEntry);
            }
        }
        return this.updateEntry;
    }

    @Override
    public int getLatency() {
        return this.latency;
    }

    @Override
    public void setLatency(int latency) {
        this.latency = latency;
        this.updateEntry().latency = true;
    }

    @Override
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.gameMode = checkNotNull(gameMode, "gameMode");
        this.updateEntry().gameMode = true;
    }

    @Override
    public Text getDisplayName() {
        if (this.displayName == null) {
            return Texts.of(this.name);
        }
        return this.displayName;
    }

    @Override
    public void setDisplayName(Text displayName) {
        this.displayName = displayName;
        this.updateEntry().displayName = true;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = checkNotNull(name, "name");
        this.updateEntry().gameProfileOrName = true;
    }

    @Override
    public GameProfile getProfile() {
        return this.gameProfile;
    }

    @Override
    public void setProfile(GameProfile profile) {
        this.gameProfile = (LanternGameProfile) checkNotNull(profile, "profile");
        this.updateEntry().gameProfileOrName = true;
    }

}
