/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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

import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

public class GlobalTabListEntry {

    private final GlobalTabList tabList;
    private final List<LanternTabListEntry> tabListEntries = new ArrayList<>();
    private final GameProfile gameProfile;

    GlobalTabListEntry(GlobalTabList tabList, GameProfile gameProfile) {
        this.gameProfile = gameProfile;
        this.tabList = tabList;
    }

    public GameProfile getProfile() {
        return this.gameProfile;
    }

    /**
     * Adds a {@link LanternTabListEntry} to this global tab list entry.
     *
     * @param tabListEntry The tab list entry
     */
    void addEntry(LanternTabListEntry tabListEntry) {
        boolean empty = this.tabListEntries.isEmpty();
        this.tabListEntries.add(tabListEntry);
        if (empty) {
            this.tabList.addEntry(this);
        }
    }

    /**
     * Removes a {@link LanternTabListEntry} from this global tab list entry.
     *
     * @param tabListEntry The tab list entry
     */
    void removeEntry(LanternTabListEntry tabListEntry) {
        this.tabListEntries.remove(tabListEntry);
        if (this.tabListEntries.isEmpty()) {
            this.tabList.remove(this.gameProfile);
        }
    }

    /**
     * Removes this entry from all the {@link LanternTabList}s.
     */
   public void removeEntry() {
        if (this.tabListEntries.isEmpty()) {
            return;
        }
        MessagePlayOutTabListEntries message = new MessagePlayOutTabListEntries(Collections.singletonList(
                new MessagePlayOutTabListEntries.Entry.Remove(this.gameProfile)));
        this.tabListEntries.forEach(tabListEntry -> {
            tabListEntry.getList().removeRawEntry(this.gameProfile.getUniqueId());
            tabListEntry.getList().getPlayer().getConnection().send(message);
        });
        this.tabListEntries.clear();
        this.tabList.remove(this.gameProfile);
    }

    public void setDisplayName(@Nullable Text displayName) {
        if (this.tabListEntries.isEmpty()) {
            return;
        }
        MessagePlayOutTabListEntries message = new MessagePlayOutTabListEntries(Collections.singletonList(
                new MessagePlayOutTabListEntries.Entry.UpdateDisplayName(this.gameProfile, displayName)));
        this.tabListEntries.forEach(tabListEntry -> {
            tabListEntry.setRawDisplayName(displayName);
            tabListEntry.getList().getPlayer().getConnection().send(message);
        });
    }

    public void setLatency(int latency) {
        if (this.tabListEntries.isEmpty()) {
            return;
        }
        MessagePlayOutTabListEntries message = new MessagePlayOutTabListEntries(Collections.singletonList(
                new MessagePlayOutTabListEntries.Entry.UpdateLatency(this.gameProfile, latency)));
        this.tabListEntries.forEach(tabListEntry -> {
            tabListEntry.setRawLatency(latency);
            tabListEntry.getList().getPlayer().getConnection().send(message);
        });
    }

    public void setGameMode(GameMode gameMode) {
        if (this.tabListEntries.isEmpty()) {
            return;
        }
        MessagePlayOutTabListEntries message = new MessagePlayOutTabListEntries(Collections.singletonList(
                new MessagePlayOutTabListEntries.Entry.UpdateGameMode(this.gameProfile, gameMode)));
        this.tabListEntries.forEach(tabListEntry -> {
            tabListEntry.setRawGameMode(gameMode);
            tabListEntry.getList().getPlayer().getConnection().send(message);
        });
    }
}
