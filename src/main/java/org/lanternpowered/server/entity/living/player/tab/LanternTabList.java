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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListHeaderAndFooter;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

public class LanternTabList implements TabList {

    private final LanternPlayer player;

    private final Map<UUID, LanternTabListEntry> tabListEntries = new ConcurrentHashMap<>();
    private Optional<Text> header = Optional.empty();
    private Optional<Text> footer = Optional.empty();

    public LanternTabList(LanternPlayer player) {
        this.player = player;
    }

    /**
     * Initializes the {@link TabList} for the player and sends
     * the initial entries as a bulk.
     *
     * @param entries the entries
     */
    public void init(List<LanternTabListEntry> entries) {
        if (entries.isEmpty()) {
            return;
        }
        List<MessagePlayOutTabListEntries.Entry> messageEntries = new ArrayList<>();
        entries.forEach(e -> {
            checkArgument(e.getList() == this, "Tab list entry targets the wrong tab list!");
            this.tabListEntries.put(e.getProfile().getUniqueId(), e);
            e.getGlobalEntry().addEntry(e);
            messageEntries.add(new MessagePlayOutTabListEntries.Entry.Add(e.getProfile(), e.getGameMode(),
                    e.getDisplayName().orElse(null), e.getLatency()));
        });
        this.player.getConnection().send(new MessagePlayOutTabListEntries(messageEntries));
        if (this.footer.isPresent() || this.header.isPresent()) {
            this.sendHeaderAndFooterUpdate();
        }
    }

    private void sendHeaderAndFooterUpdate() {
        this.player.getConnection().send(new MessagePlayOutTabListHeaderAndFooter(this.header.orElse(null), this.footer.orElse(null)));
    }

    public void clear() {
        this.tabListEntries.values().forEach(entry -> entry.getGlobalEntry().removeEntry(entry));
    }

    @Override
    public LanternPlayer getPlayer() {
        return this.player;
    }

    @Override
    public Optional<Text> getHeader() {
        return this.header;
    }

    @Override
    public LanternTabList setHeader(@Nullable Text header) {
        this.header = Optional.ofNullable(header);
        this.sendHeaderAndFooterUpdate();
        return this;
    }

    @Override
    public Optional<Text> getFooter() {
        return this.footer;
    }

    @Override
    public LanternTabList setFooter(@Nullable Text footer) {
        this.footer = Optional.ofNullable(footer);
        this.sendHeaderAndFooterUpdate();
        return this;
    }

    @Override
    public LanternTabList setHeaderAndFooter(@Nullable Text header, @Nullable Text footer) {
        this.header = Optional.ofNullable(header);
        this.footer = Optional.ofNullable(footer);
        this.sendHeaderAndFooterUpdate();
        return this;
    }

    @Override
    public Collection<TabListEntry> getEntries() {
        return ImmutableList.copyOf(this.tabListEntries.values());
    }

    @Override
    public Optional<TabListEntry> getEntry(UUID uniqueId) {
        return Optional.ofNullable(this.tabListEntries.get(checkNotNull(uniqueId, "uniqueId")));
    }

    @Override
    public TabList addEntry(TabListEntry entry) throws IllegalArgumentException {
        checkNotNull(entry, "entry");
        UUID uniqueId = entry.getProfile().getUniqueId();
        checkArgument(entry.getList() == this,
                "The tab list entries #getList() list does not match to this list.");
        checkArgument(!this.tabListEntries.containsKey(uniqueId),
                "There is already a tab list entry assigned with the unique id: " + uniqueId.toString());
        this.tabListEntries.put(uniqueId, (LanternTabListEntry) entry);
        this.player.getConnection().send(new MessagePlayOutTabListEntries(Collections.singletonList(new MessagePlayOutTabListEntries.Entry.Add(
                entry.getProfile(), entry.getGameMode(), entry.getDisplayName().orElse(null), entry.getLatency()))));
        LanternTabListEntry entry0 = (LanternTabListEntry) entry;
        entry0.attached = true;
        entry0.getGlobalEntry().addEntry(entry0);
        return this;
    }

    Optional<TabListEntry> removeRawEntry(UUID uniqueId) {
        LanternTabListEntry entry = this.tabListEntries.remove(checkNotNull(uniqueId, "uniqueId"));
        if (entry != null) {
            entry.attached = false;
            return Optional.of(entry);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TabListEntry> removeEntry(UUID uniqueId) {
        final Optional<TabListEntry> entry = this.removeRawEntry(uniqueId);
        entry.ifPresent(entry0 -> {
            this.player.getConnection().send(new MessagePlayOutTabListEntries(Collections.singletonList(
                    new MessagePlayOutTabListEntries.Entry.Remove(entry0.getProfile()))));
            ((LanternTabListEntry) entry0).getGlobalEntry().removeEntry((LanternTabListEntry) entry0);
        });
        return entry;
    }
}
