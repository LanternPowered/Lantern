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
package org.lanternpowered.server.entity.living.player.tab

import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListPacket
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.entity.living.player.tab.TabListEntry
import org.spongepowered.api.profile.GameProfile
import java.util.Optional

class LanternTabListEntry internal constructor(
        val globalEntry: GlobalTabListEntry,
        private val tabList: LanternTabList,
        private var gameMode: GameMode,
        private var latency: Int,
        private var displayName: Text?
) : TabListEntry {

    // Whether this tab list entry is attached to the tab list
    var attached = false

    override fun getList(): LanternTabList = this.tabList
    override fun getProfile(): GameProfile = this.globalEntry.profile
    override fun getDisplayName(): Optional<Text> = this.displayName.optional()
    override fun getLatency(): Int = this.latency
    override fun getGameMode(): GameMode = this.gameMode

    /**
     * Sets the display name without triggering any updates.
     *
     * @param displayName The display name
     */
    fun setRawDisplayName(displayName: Text?) { this.displayName = displayName }

    override fun setDisplayName(displayName: Text?): LanternTabListEntry = apply {
        setRawDisplayName(displayName)
        if (this.attached)
            sendDisplayName(displayName)
    }

    fun refreshDisplayName() {
        sendDisplayName(this.displayName)
    }

    private fun sendDisplayName(displayName: Text?) {
        this.tabList.player.connection.send(TabListPacket(
                TabListPacket.Entry.UpdateDisplayName(this.profile, displayName)))
    }

    /**
     * Sets the latency without triggering any updates.
     *
     * @param latency The latency
     */
    fun setRawLatency(latency: Int) { this.latency = latency }

    override fun setLatency(latency: Int): LanternTabListEntry = apply {
        setRawLatency(latency)
        if (this.attached)
            this.tabList.player.connection.send(TabListPacket(
                    TabListPacket.Entry.UpdateLatency(this.profile, latency)))
    }

    /**
     * Sets the game mode without triggering any updates.
     *
     * @param gameMode The game mode
     */
    fun setRawGameMode(gameMode: GameMode) { this.gameMode = gameMode }

    override fun setGameMode(gameMode: GameMode): LanternTabListEntry = apply {
        setRawGameMode(gameMode)
        if (this.attached)
            this.tabList.player.connection.send(TabListPacket(
                    TabListPacket.Entry.UpdateGameMode(this.profile, gameMode)))
    }

}