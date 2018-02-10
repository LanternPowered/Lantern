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
package org.lanternpowered.server.entity.shards;

import com.google.inject.Inject;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.event.TrackerChangeShardevent;
import org.lanternpowered.server.shards.Shard;
import org.lanternpowered.server.shards.Holder;
import org.lanternpowered.server.shards.OnAttach;
import org.lanternpowered.server.shards.OnUpdate;
import org.lanternpowered.server.shards.event.ShardeventListener;
import org.spongepowered.api.boss.BossBar;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.text.Text;

import java.util.List;

public final class BossShard extends Shard {

    @Inject @Holder private LanternEntity holder;

    private Text defaultName;
    private ServerBossBar bossBar;

    @OnAttach
    private void onAttach() {
        // Get a name we can fallback to if there is no display name
        this.defaultName = Text.of(this.holder.getType().getTranslation());
        // Create the boss bar
        this.bossBar = ServerBossBar.builder()
                .overlay(BossBarOverlays.NOTCHED_12) // Is this OK?
                .color(BossBarColors.PURPLE)
                .visible(true)
                .build();
        // TODO: More boss bar settings?
    }

    /**
     * Updates the {@link BossBar} for the
     * given health and max health.
     */
    private void update() {
        final double health = this.holder.get(Keys.HEALTH).get();
        final double maxHealth = this.holder.get(Keys.MAX_HEALTH).get();
        // Update the percent of the boss bar
        this.bossBar.setPercent((float) Math.min(health / maxHealth, 1.0));
        // Update the name of the boss bar
        this.bossBar.setName(this.holder.get(Keys.DISPLAY_NAME).orElse(this.defaultName));
    }

    @OnUpdate(pulseRate = 5)
    private void onUpdate() {
        if (this.bossBar.getPlayers().isEmpty()) { // Only update if necessary.
            return;
        }
        update();
    }

    /**
     * Is called when trackers are added.
     */
    @SuppressWarnings("unchecked")
    @ShardeventListener
    private void onTrackerChangeAdd(TrackerChangeShardevent.Add event) {
        if (this.bossBar.getPlayers().isEmpty()) {
            update(); // Update things when no players were present
        }
        this.bossBar.addPlayers((List) event.getPlayers());
    }

    /**
     * Is called when trackers are removed.
     */
    @SuppressWarnings("unchecked")
    @ShardeventListener
    private void onTrackerChangeRemove(TrackerChangeShardevent.Remove event) {
        this.bossBar.removePlayers((List) event.getPlayers());
    }

    /**
     * Gets the {@link BossBar} that will be
     * displayed to surrounding players.
     *
     * @return The boss bar
     */
    public BossBar getBossBar() {
        return this.bossBar;
    }

}
