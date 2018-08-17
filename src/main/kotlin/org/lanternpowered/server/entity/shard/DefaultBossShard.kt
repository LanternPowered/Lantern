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
package org.lanternpowered.server.entity.shard

import org.lanternpowered.api.boss.BossBar
import org.lanternpowered.api.boss.BossBarColors
import org.lanternpowered.api.boss.BossBarOverlays
import org.lanternpowered.api.entity.shard.BossShard
import org.lanternpowered.api.text.Text
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.api.entity.event.TrackerChangeShardevent
import org.lanternpowered.server.shards.OnAttach
import org.lanternpowered.server.shards.OnUpdate
import org.lanternpowered.api.shard.event.ShardeventListener
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player

class DefaultBossShard : BossShard() {

    private val entityHolder: LanternEntity by requireHolderOfType()

    private lateinit var defaultName: Text
    private lateinit var theBossBar: BossBar

    /**
     * The boss bar that will be displayed to the surrounding players.
     */
    override val bossBar: BossBar get() = this.theBossBar

    @OnAttach
    private fun onAttach() {
        // Get a name we can fallback to if there is no display name
        this.defaultName = Text(this.entityHolder.type.translation)
        // Create the boss bar
        this.theBossBar = BossBar(this.defaultName) {
            overlay(BossBarOverlays.NOTCHED_12) // Is this OK?
            color(BossBarColors.PURPLE)
            visible(true)
        }
        // TODO: More boss bar settings?
    }

    /**
     * Updates the [BossBar] for the
     * given health and max health.
     */
    private fun update() {
        val health = this.entityHolder.require(Keys.HEALTH)
        val maxHealth = this.entityHolder.require(Keys.MAX_HEALTH)
        // Update the percent of the boss bar
        this.bossBar.percent = Math.min(health / maxHealth, 1.0).toFloat()
        // Update the name of the boss bar
        this.bossBar.name = this.entityHolder[Keys.DISPLAY_NAME].orElse(this.defaultName)
    }

    @OnUpdate(pulseRate = 5)
    private fun onUpdate() {
        if (this.bossBar.players.isEmpty()) { // Only update if necessary.
            return
        }
        update()
    }

    /**
     * Is called when trackers are added.
     */
    @ShardeventListener
    private fun onTrackerChangeAdd(event: TrackerChangeShardevent.Add) {
        if (this.bossBar.players.isEmpty()) {
            update() // Update things when no players were present
        }
        this.bossBar.addPlayers(event.players as List<Player>)
    }

    /**
     * Is called when trackers are removed.
     */
    @ShardeventListener
    private fun onTrackerChangeRemove(event: TrackerChangeShardevent.Remove) {
        this.bossBar.removePlayers(event.players as List<Player>)
    }
}
