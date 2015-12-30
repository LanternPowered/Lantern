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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.entity.living.player.tab.LanternPlayerTabInfo.UpdateEntry;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries.Entry;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListHeaderAndFooter;
import org.spongepowered.api.entity.living.player.tab.PlayerTabInfo;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NonnullByDefault
public class LanternTabList implements TabList {

    final Deque<LanternPlayerTabInfo.UpdateEntry> updateEntries = new ArrayDeque<>();

    private final List<LanternPlayerTabInfo> infoEntries = Lists.newArrayList();
    private final LanternPlayer player;

    private Text header = Texts.of();
    private Text footer = Texts.of();

    public LanternTabList(LanternPlayer player) {
        this.player = player;
    }

    void pulse() {
        List<Entry> entries = null;

        LanternPlayerTabInfo.UpdateEntry entry;
        while ((entry = this.updateEntries.poll()) != null) {
            if (entries == null) {
                entries = Lists.newArrayList();
            }
            if (!entry.tabInfo.tabLists.contains(this)) {
                continue;
            }
            if (entry.entryCache != null) {
                entries.addAll(entry.entryCache);
            } else {
                List<Entry> entries0 = Lists.newArrayList();
                LanternPlayerTabInfo info = entry.tabInfo;
                if (entry.remove) {
                    entries.add(new Entry.Remove(info.uniqueId));
                } else if (entry.gameProfileOrName) {
                    entries0.add(new Entry.Add(info.uniqueId, info.name,
                            info.gameProfile.getProperties(), info.gameMode,
                            info.displayName, info.latency));
                } else {
                    if (entry.gameMode) {
                        entries0.add(new Entry.UpdateGameMode(info.uniqueId, info.gameMode));
                    }
                    if (entry.displayName) {
                        entries0.add(new Entry.UpdateDisplayName(info.uniqueId, info.displayName));
                    }
                    if (entry.latency) {
                        entries0.add(new Entry.UpdateLatency(info.uniqueId, info.latency));
                    }
                }
                entry.entryCache = entries0;
                info.updateEntry = null;
                entries.addAll(entries0);
            }
        }
        if (entries != null) {
            this.player.getConnection().send(new MessagePlayOutTabListEntries(entries));
        }
    }

    @Override
    public Text getHeader() {
        return this.header;
    }

    @Override
    public void setHeader(Text header) {
        this.header = checkNotNull(header, "header");
        this.player.getConnection().send(new MessagePlayOutTabListHeaderAndFooter(this.header, this.footer));
    }

    @Override
    public Text getFooter() {
        return this.footer;
    }

    @Override
    public void setFooter(Text footer) {
        this.footer = checkNotNull(footer, "footer");
        this.player.getConnection().send(new MessagePlayOutTabListHeaderAndFooter(this.header, this.footer));
    }

    @Override
    public List<PlayerTabInfo> getPlayers() {
        return ImmutableList.copyOf(this.infoEntries);
    }

    @Override
    public void addPlayer(PlayerTabInfo player) throws IllegalArgumentException {
        LanternPlayerTabInfo info = (LanternPlayerTabInfo) checkNotNull(player, "player");
        checkArgument(!this.getPlayer(info.uniqueId).isPresent(),
                "Already a player present with uniqueId " + info.uniqueId);
        info.tabLists.add(this);
        this.infoEntries.add(info);
        UpdateEntry entry = new UpdateEntry(info);
        entry.gameProfileOrName = true;
        this.updateEntries.add(entry);
    }

    @Override
    public LanternPlayerTabInfo removePlayer(UUID playerId) {
        checkNotNull(playerId, "playerId");
        LanternPlayerTabInfo info = (LanternPlayerTabInfo) this.getPlayer(playerId).orElse(null);
        if (info != null) {
            this.removeInfo(info);
        }
        return info;
    }

    void removeInfo(LanternPlayerTabInfo info) {
        info.tabLists.remove(this);
        this.infoEntries.remove(info);
        UpdateEntry entry = new UpdateEntry(info);
        entry.remove = true;
        this.updateEntries.add(entry);
    }

    @Override
    public Optional<PlayerTabInfo> getPlayer(UUID playerId) {
        checkNotNull(playerId, "playerId");
        for (LanternPlayerTabInfo info : this.infoEntries) {
            if (info.uniqueId.equals(playerId)) {
                return Optional.of(info);
            }
        }
        return Optional.empty();
    }
}
