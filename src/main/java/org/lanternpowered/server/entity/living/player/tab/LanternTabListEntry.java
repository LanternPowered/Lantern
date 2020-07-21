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

import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTabListEntries;
import org.lanternpowered.server.text.translation.TranslationHelper;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class LanternTabListEntry implements TabListEntry {

    private final GlobalTabListEntry globalEntry;
    private final LanternTabList tabList;

    private Optional<Text> displayName;
    private GameMode gameMode;
    private int latency;

    // Whether this tab list entry is attached to the tab list
    boolean attached;

    LanternTabListEntry(GlobalTabListEntry globalEntry, LanternTabList tabList, GameMode gameMode, int latency, @Nullable Text displayName) {
        this.displayName = Optional.ofNullable(displayName);
        this.globalEntry = globalEntry;
        this.gameMode = gameMode;
        this.tabList = tabList;
        this.latency = latency;
    }

    void refreshDisplayName() {
        this.displayName.filter(TranslationHelper::containsNonMinecraftTranslation).ifPresent(this::sendDisplayName);
    }

    private void sendDisplayName(@Nullable Text displayName) {
        this.tabList.getPlayer().getConnection().send(new PacketPlayOutTabListEntries(Collections.singletonList(
                new PacketPlayOutTabListEntries.Entry.UpdateDisplayName(getProfile(), displayName))));
    }

    /**
     * Gets the {@link GlobalTabListEntry} of this entry.
     *
     * @return The global tab list entry
     */
    public GlobalTabListEntry getGlobalEntry() {
        return this.globalEntry;
    }

    @Override
    public LanternTabList getList() {
        return this.tabList;
    }

    @Override
    public GameProfile getProfile() {
        return this.globalEntry.getProfile();
    }

    @Override
    public Optional<Text> getDisplayName() {
        return this.displayName;
    }

    /**
     * Sets the display name without triggering any updates.
     *
     * @param displayName The display name
     */
    void setRawDisplayName(@Nullable Text displayName) {
        this.displayName = Optional.ofNullable(displayName);
    }

    @Override
    public LanternTabListEntry setDisplayName(@Nullable Text displayName) {
        setRawDisplayName(displayName);
        if (this.attached) {
            sendDisplayName(displayName);
        }
        return this;
    }

    @Override
    public int getLatency() {
        return this.latency;
    }

    /**
     * Sets the latency without triggering any updates.
     *
     * @param latency The latency
     */
    void setRawLatency(int latency) {
        this.latency = latency;
    }

    @Override
    public LanternTabListEntry setLatency(int latency) {
        setRawLatency(latency);
        if (this.attached) {
            this.tabList.getPlayer().getConnection().send(new PacketPlayOutTabListEntries(Collections.singletonList(
                    new PacketPlayOutTabListEntries.Entry.UpdateLatency(getProfile(), latency))));
        }
        return this;
    }

    @Override
    public GameMode getGameMode() {
        return this.gameMode;
    }

    /**
     * Sets the game mode without triggering any updates.
     *
     * @param gameMode The game mode
     */
    void setRawGameMode(GameMode gameMode) {
        this.gameMode = checkNotNull(gameMode, "gameMode");
    }

    @Override
    public LanternTabListEntry setGameMode(GameMode gameMode) {
        this.setRawGameMode(gameMode);
        if (this.attached) {
            this.tabList.getPlayer().getConnection().send(new PacketPlayOutTabListEntries(Collections.singletonList(
                    new PacketPlayOutTabListEntries.Entry.UpdateGameMode(getProfile(), gameMode))));
        }
        return this;
    }
}
