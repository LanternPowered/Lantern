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
package org.lanternpowered.server.boss

import com.google.common.collect.ImmutableList
import org.lanternpowered.api.boss.BossBar
import org.lanternpowered.api.boss.BossBarColor
import org.lanternpowered.api.boss.BossBarOverlay
import org.lanternpowered.api.text.Text
import org.lanternpowered.server.entity.living.player.LanternPlayer
import org.lanternpowered.server.network.message.Message
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBossBar
import org.spongepowered.api.entity.living.player.Player
import java.util.HashSet
import java.util.UUID

data class LanternBossBar internal constructor(
        private val uniqueId: UUID,
        private var name: Text,
        private var color: BossBarColor,
        private var overlay: BossBarOverlay,
        private var percent: Float,
        private var darkenSky: Boolean,
        private var playEndBossMusic: Boolean,
        private var createFog: Boolean,
        private var visible: Boolean
) : BossBar {

    /**
     * All the [LanternPlayer]s that are currently viewing this boss bar.
     */
    private val viewers = HashSet<LanternPlayer>()

    override fun getName(): Text = this.name
    override fun getPercent(): Float = this.percent
    override fun getColor(): BossBarColor = this.color
    override fun getOverlay(): BossBarOverlay = this.overlay
    override fun shouldDarkenSky(): Boolean = this.darkenSky
    override fun shouldPlayEndBossMusic(): Boolean = this.playEndBossMusic
    override fun shouldCreateFog(): Boolean = this.createFog
    override fun getUniqueId(): UUID = this.uniqueId
    override fun isVisible(): Boolean = this.visible

    private fun sendToViewers(message: () -> Message) {
        if (!this.viewers.isEmpty()) {
            val message1 = message()
            this.viewers.forEach { player -> player.connection.send(message1) }
        }
    }

    override fun setName(name: Text): LanternBossBar = apply {
        this.name = name
        sendToViewers { MessagePlayOutBossBar.UpdateTitle(this.uniqueId, name) }
    }

    override fun setPercent(percent: Float): LanternBossBar = apply {
        check(percent in 0f..1f) { "Percent must be between 0 and 1, but $percent is not" }
        if (percent != this.percent) {
            sendToViewers { MessagePlayOutBossBar.UpdatePercent(this.uniqueId, percent) }
        }
        this.percent = percent
    }

    override fun setColor(color: BossBarColor): LanternBossBar = apply {
        val update = this.color != color
        this.color = color
        if (update) {
            sendStyleUpdate()
        }
    }

    override fun setOverlay(overlay: BossBarOverlay): LanternBossBar = apply {
        val update = this.overlay != overlay
        this.overlay = overlay
        if (update) {
            sendStyleUpdate()
        }
    }

    private fun sendStyleUpdate() {
        sendToViewers { MessagePlayOutBossBar.UpdateStyle(this.uniqueId, this.color, this.overlay) }
    }

    override fun setDarkenSky(darkenSky: Boolean): LanternBossBar = apply {
        val update = this.darkenSky != darkenSky
        this.darkenSky = darkenSky
        if (update) {
            sendMiscUpdate()
        }
    }

    override fun setPlayEndBossMusic(playEndBossMusic: Boolean): LanternBossBar = apply {
        val update = this.playEndBossMusic != playEndBossMusic
        this.playEndBossMusic = playEndBossMusic
        if (update) {
            sendMiscUpdate()
        }
    }

    override fun setCreateFog(createFog: Boolean): LanternBossBar = apply {
        val update = this.createFog != createFog
        this.createFog = createFog
        if (update) {
            sendMiscUpdate()
        }
    }

    private fun sendMiscUpdate() {
        sendToViewers { MessagePlayOutBossBar.UpdateMisc(this.uniqueId, this.darkenSky, this.createFog, this.createFog) }
    }

    override fun setVisible(visible: Boolean): LanternBossBar = apply {
        if (visible != this.visible) {
            if (visible) {
                sendToViewers { createAddMessage() }
            } else {
                sendToViewers { MessagePlayOutBossBar.Remove(this.uniqueId) }
            }
        }
        this.visible = visible
    }

    override fun getPlayers(): Collection<Player> = ImmutableList.copyOf(this.viewers)

    override fun addPlayer(player: Player): LanternBossBar = apply {
        player as LanternPlayer
        if (this.viewers.add(player) && this.visible) {
            resendBossBar(player)
        }
    }

    override fun removePlayer(player: Player): LanternBossBar = apply {
        player as LanternPlayer
        if (this.viewers.remove(player) && this.visible) {
            player.connection.send(MessagePlayOutBossBar.Remove(this.uniqueId))
        }
    }

    fun removeRawPlayer(player: LanternPlayer): LanternBossBar = apply {
        this.viewers.remove(player)
    }

    fun resendBossBar(player: LanternPlayer) {
        player.connection.send(createAddMessage())
    }

    private fun createAddMessage(): MessagePlayOutBossBar.Add {
        return MessagePlayOutBossBar.Add(this.uniqueId, this.name,
                this.color, this.overlay, this.percent, this.darkenSky, this.playEndBossMusic, this.createFog)
    }
}
