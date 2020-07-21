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
import org.lanternpowered.api.boss.BossBarFlag
import org.lanternpowered.api.boss.BossBarOverlay
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.collections.concurrentHashMapOf
import org.lanternpowered.api.util.collections.toImmutableMap
import org.lanternpowered.server.entity.living.player.LanternPlayer
import org.lanternpowered.server.network.vanilla.packet.type.play.BossBarPacket
import java.util.UUID

/**
 * The boss bar manager of a specific player.
 */
class PlayerBossBarManager(
        private val player: LanternPlayer
) : net.kyori.adventure.bossbar.BossBar.Listener {

    private val bossBars = concurrentHashMapOf<BossBar, UUID>()

    override fun bossBarColorChanged(bar: BossBar, oldColor: BossBarColor, newColor: BossBarColor) {
        val uniqueId = this.bossBars[bar] ?: return
        this.player.connection.send(BossBarPacket.UpdateStyle(uniqueId, newColor, bar.overlay()))
    }

    override fun bossBarFlagsChanged(bar: BossBar, oldFlags: Set<BossBarFlag>, newFlags: Set<BossBarFlag>) {
        val uniqueId = this.bossBars[bar] ?: return
        this.player.connection.send(BossBarPacket.UpdateFlags(uniqueId, newFlags))
    }

    override fun bossBarNameChanged(bar: BossBar, oldName: Text, newName: Text) {
        val uniqueId = this.bossBars[bar] ?: return
        this.player.connection.send(BossBarPacket.UpdateName(uniqueId, newName))
    }

    override fun bossBarOverlayChanged(bar: BossBar, oldOverlay: BossBarOverlay, newOverlay: BossBarOverlay) {
        val uniqueId = this.bossBars[bar] ?: return
        this.player.connection.send(BossBarPacket.UpdateStyle(uniqueId, bar.color(), newOverlay))
    }

    override fun bossBarPercentChanged(bar: BossBar, oldPercent: Float, newPercent: Float) {
        val uniqueId = this.bossBars[bar] ?: return
        this.player.connection.send(BossBarPacket.UpdatePercent(uniqueId, newPercent))
    }

    fun show(bossBar: BossBar) {
        if (this.bossBars.containsKey(bossBar))
            return
        val uniqueId = UUID.randomUUID()
        val packet = BossBarPacket.Add(uniqueId, bossBar.name(), bossBar.color(),
                bossBar.overlay(), bossBar.percent(), bossBar.flags())
        this.player.connection.send(packet)
        bossBar.addListener(this)
    }

    fun hide(bossBar: BossBar) {
        val uniqueId = this.bossBars.remove(bossBar) ?: return
        bossBar.removeListener(this)
        this.player.connection.send(BossBarPacket.Remove(uniqueId))
    }

    fun clear() {
        val all = this.bossBars.toImmutableMap()
        this.bossBars.clear()
        for ((bossBar, uniqueId) in all) {
            bossBar.removeListener(this)
            this.player.connection.send(BossBarPacket.Remove(uniqueId))
        }
    }
}
