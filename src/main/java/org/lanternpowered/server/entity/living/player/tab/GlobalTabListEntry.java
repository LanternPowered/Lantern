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

import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

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
