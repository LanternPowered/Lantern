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
package org.lanternpowered.server.boss

import org.lanternpowered.api.boss.BossBar
import org.lanternpowered.api.boss.BossBarColor
import org.lanternpowered.api.boss.BossBarOverlay
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.server.entity.living.player.LanternPlayer
import org.lanternpowered.server.network.message.Message
import org.lanternpowered.server.network.vanilla.message.type.play.BossBarMessage
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
    private val viewers = mutableSetOf<LanternPlayer>()

    override fun getName(): Text = this.name
    override fun getPercent(): Float = this.percent
    override fun getColor(): BossBarColor = this.color
    override fun getOverlay(): BossBarOverlay = this.overlay
    override fun shouldDarkenSky(): Boolean = this.darkenSky
    override fun shouldPlayEndBossMusic(): Boolean = this.playEndBossMusic
    override fun shouldCreateFog(): Boolean = this.createFog
    override fun getUniqueId(): UUID = this.uniqueId
    override fun isVisible(): Boolean = this.visible

    private fun sendToViewers(supplier: () -> Message) {
        if (this.viewers.isEmpty())
            return
        val message = supplier()
        this.viewers.forEach { player -> player.connection.send(message) }
    }

    override fun setName(name: Text): LanternBossBar = apply {
        this.name = name
        sendToViewers { BossBarMessage.UpdateTitle(this.uniqueId, name) }
    }

    override fun setPercent(percent: Float): LanternBossBar = apply {
        check(percent in 0f..1f) { "Percent must be between 0 and 1, but $percent is not" }
        if (percent != this.percent) {
            sendToViewers { BossBarMessage.UpdatePercent(this.uniqueId, percent) }
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
        sendToViewers { BossBarMessage.UpdateStyle(this.uniqueId, this.color, this.overlay) }
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
        sendToViewers { BossBarMessage.UpdateMisc(this.uniqueId, this.darkenSky, this.createFog, this.createFog) }
    }

    override fun setVisible(visible: Boolean): LanternBossBar = apply {
        if (visible != this.visible) {
            if (visible) {
                sendToViewers { createAddMessage() }
            } else {
                sendToViewers { BossBarMessage.Remove(this.uniqueId) }
            }
        }
        this.visible = visible
    }

    override fun getPlayers(): Collection<Player> = this.viewers.toImmutableList()

    override fun addPlayer(player: Player): LanternBossBar = apply {
        player as LanternPlayer
        if (this.viewers.add(player) && this.visible) {
            resendBossBar(player)
        }
    }

    override fun removePlayer(player: Player): LanternBossBar = apply {
        player as LanternPlayer
        if (this.viewers.remove(player) && this.visible) {
            player.connection.send(BossBarMessage.Remove(this.uniqueId))
        }
    }

    fun removeRawPlayer(player: LanternPlayer): LanternBossBar = apply {
        this.viewers.remove(player)
    }

    fun resendBossBar(player: LanternPlayer) {
        player.connection.send(createAddMessage())
    }

    private fun createAddMessage(): BossBarMessage.Add = BossBarMessage.Add(this.uniqueId, this.name,
            this.color, this.overlay, this.percent, this.darkenSky, this.playEndBossMusic, this.createFog)
}
