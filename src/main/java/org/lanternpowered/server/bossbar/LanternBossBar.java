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
package org.lanternpowered.server.bossbar;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.objects.LocalizedText;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBossBar;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarOverlay;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class LanternBossBar implements ServerBossBar {

    private final UUID uniqueId;
    private Text name;
    private float percent;
    private BossBarColor color;
    private BossBarOverlay overlay;
    private boolean darkenSky;
    private boolean playEndBossMusic;
    private boolean createFog;
    private boolean visible;

    /**
     * All the {@link LanternPlayer}s that are currently viewing
     * this boss bar.
     */
    private final Set<LanternPlayer> viewers = new HashSet<>();

    LanternBossBar(UUID uniqueId, Text name, BossBarColor color, BossBarOverlay overlay,
            float percent, boolean darkenSky, boolean playEndBossMusic, boolean createFog, boolean visible) {
        this.playEndBossMusic = playEndBossMusic;
        this.createFog = createFog;
        this.darkenSky = darkenSky;
        this.uniqueId = uniqueId;
        this.overlay = overlay;
        this.visible = visible;
        this.percent = percent;
        this.color = color;
        this.name = name;
    }

    @Override
    public Text getName() {
        return this.name;
    }

    @Override
    public LanternBossBar setName(Text name) {
        this.name = checkNotNull(name, "name");
        if (!this.viewers.isEmpty()) {
            this.viewers.forEach(player -> player.getConnection().send(new MessagePlayOutBossBar.UpdateTitle(
                    this.uniqueId, new LocalizedText(name, player.getLocale()))));
        }
        return this;
    }

    @Override
    public float getPercent() {
        return this.percent;
    }

    @Override
    public LanternBossBar setPercent(float percent) {
        checkNotNull(percent >= 0f && percent <= 1f, "Percent must be between 0 and 1, but %s is not", percent);
        if (percent != this.percent && !this.viewers.isEmpty()) {
            final Message message = new MessagePlayOutBossBar.UpdatePercent(
                    this.uniqueId, percent);
            this.viewers.forEach(player -> player.getConnection().send(message));
        }
        this.percent = percent;
        return this;
    }

    @Override
    public BossBarColor getColor() {
        return this.color;
    }

    @Override
    public LanternBossBar setColor(BossBarColor color) {
        boolean update = this.color != color;
        this.color = checkNotNull(color, "color");
        if (update) {
            this.sendStyleUpdate();
        }
        return this;
    }

    @Override
    public BossBarOverlay getOverlay() {
        return this.overlay;
    }

    @Override
    public LanternBossBar setOverlay(BossBarOverlay overlay) {
        boolean update = this.overlay != overlay;
        this.overlay = checkNotNull(overlay, "overlay");
        if (update) {
            this.sendStyleUpdate();
        }
        return this;
    }

    private void sendStyleUpdate() {
        if (!this.viewers.isEmpty()) {
            final Message message = new MessagePlayOutBossBar.UpdateStyle(
                    this.uniqueId, this.color, this.overlay);
            this.viewers.forEach(player -> player.getConnection().send(message));
        }
    }

    @Override
    public boolean shouldDarkenSky() {
        return this.darkenSky;
    }

    @Override
    public LanternBossBar setDarkenSky(boolean darkenSky) {
        boolean update = this.darkenSky != darkenSky;
        this.darkenSky = darkenSky;
        if (update) {
            this.sendMiscUpdate();
        }
        return this;
    }

    @Override
    public boolean shouldPlayEndBossMusic() {
        return this.playEndBossMusic;
    }

    @Override
    public LanternBossBar setPlayEndBossMusic(boolean playEndBossMusic) {
        boolean update = this.playEndBossMusic != playEndBossMusic;
        this.playEndBossMusic = playEndBossMusic;
        if (update) {
            this.sendMiscUpdate();
        }
        return this;
    }

    @Override
    public boolean shouldCreateFog() {
        return this.createFog;
    }

    @Override
    public LanternBossBar setCreateFog(boolean createFog) {
        boolean update = this.createFog != createFog;
        this.createFog = createFog;
        if (update) {
            this.sendMiscUpdate();
        }
        return this;
    }

    private void sendMiscUpdate() {
        if (!this.viewers.isEmpty()) {
            final Message message = new MessagePlayOutBossBar.UpdateMisc(
                    this.uniqueId, this.darkenSky, this.playEndBossMusic || this.createFog);
            this.viewers.forEach(player -> player.getConnection().send(message));
        }
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public LanternBossBar setVisible(boolean visible) {
        if (visible != this.visible) {
            if (visible) {
                this.viewers.forEach(player -> player.getConnection().send(this.createAddMessage(player.getLocale())));
            } else if (!this.viewers.isEmpty()) {
                final Message message = new MessagePlayOutBossBar.Remove(this.uniqueId);
                this.viewers.forEach(player -> player.getConnection().send(message));
            }
        }
        this.visible = visible;
        return this;
    }

    @Override
    public Collection<Player> getPlayers() {
        return ImmutableList.copyOf(this.viewers);
    }

    @Override
    public LanternBossBar addPlayer(Player player) {
        checkNotNull(player, "player");
        final LanternPlayer player1 = (LanternPlayer) player;
        if (this.viewers.add(player1) && this.visible) {
            player1.getConnection().send(this.createAddMessage(player1.getLocale()));
        }
        return this;
    }

    @Override
    public LanternBossBar removePlayer(Player player) {
        checkNotNull(player, "player");
        final LanternPlayer player1 = (LanternPlayer) player;
        if (this.viewers.remove(player1) && this.visible) {
            this.resendBossBar(player1);
        }
        return this;
    }

    public LanternBossBar removeRawPlayer(LanternPlayer player) {
        checkNotNull(player, "player");
        this.viewers.remove(player);
        return this;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public void resendBossBar(LanternPlayer player) {
        player.getConnection().send(new MessagePlayOutBossBar.Remove(this.uniqueId));
    }

    private MessagePlayOutBossBar.Add createAddMessage(Locale locale) {
        return new MessagePlayOutBossBar.Add(this.uniqueId, new LocalizedText(this.name, locale),
                this.color, this.overlay, this.percent, this.darkenSky, this.playEndBossMusic);
    }
}
