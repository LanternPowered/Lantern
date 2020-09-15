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
package org.lanternpowered.server.entity.player.tab

import org.lanternpowered.api.text.Text
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListPacket.Entry.UpdateDisplayName
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListPacket.Entry.UpdateGameMode
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListPacket.Entry.UpdateLatency
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.profile.GameProfile

class GlobalTabListEntry internal constructor(
        private val tabList: GlobalTabList,
        val profile: GameProfile
) {

    private val entries = mutableListOf<LanternTabListEntry>()

    /**
     * Adds a [LanternTabListEntry] to this global tab list entry.
     *
     * @param tabListEntry The tab list entry
     */
    fun addEntry(tabListEntry: LanternTabListEntry) {
        val empty = this.entries.isEmpty()
        this.entries.add(tabListEntry)
        if (empty)
            this.tabList.add(this)
    }

    /**
     * Removes a [LanternTabListEntry] from this global tab list entry.
     *
     * @param entry The tab list entry
     */
    fun removeEntry(entry: LanternTabListEntry) {
        this.entries.remove(entry)
        if (this.entries.isEmpty())
            this.tabList.remove(this.profile)
    }

    /**
     * Removes this entry from all the [LanternTabList]s.
     */
    fun removeEntry() {
        if (this.entries.isEmpty())
            return
        val packet = TabListPacket(TabListPacket.Entry.Remove(this.profile))
        for (entry in this.entries) {
            entry.list.removeRawEntry(this.profile.uniqueId)
            entry.list.player.connection.send(packet)
        }
        this.entries.clear()
        this.tabList.remove(this.profile)
    }

    fun setDisplayName(displayName: Text?) {
        if (this.entries.isEmpty())
            return
        val packet = TabListPacket(UpdateDisplayName(this.profile, displayName))
        for (entry in this.entries) {
            entry.setRawDisplayName(displayName)
            entry.list.player.connection.send(packet)
        }
    }

    fun setLatency(latency: Int) {
        if (this.entries.isEmpty())
            return
        val packet = TabListPacket(UpdateLatency(this.profile, latency))
        for (entry in this.entries) {
            entry.setRawLatency(latency)
            entry.list.player.connection.send(packet)
        }
    }

    fun setGameMode(gameMode: GameMode) {
        if (this.entries.isEmpty())
            return
        val packet = TabListPacket(UpdateGameMode(this.profile, gameMode))
        for (entry in this.entries) {
            entry.setRawGameMode(gameMode)
            entry.list.player.connection.send(packet)
        }
    }
}
