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
import org.lanternpowered.api.util.collections.concurrentHashMapOf
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.server.entity.living.player.LanternPlayer
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListHeaderAndFooterPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListPacket
import org.spongepowered.api.entity.living.player.tab.TabList
import org.spongepowered.api.entity.living.player.tab.TabListEntry
import java.util.Optional
import java.util.UUID

class LanternTabList(private val player: LanternPlayer) : TabList {

    private val tabListEntries = concurrentHashMapOf<UUID, LanternTabListEntry>()

    private var header: Text? = null
    private var footer: Text? = null

    /**
     * Initializes the [TabList] for the player and sends
     * the initial entries as a bulk.
     *
     * @param entries the entries
     */
    fun init(entries: List<LanternTabListEntry>) {
        if (entries.isEmpty()) {
            return
        }
        val packetEntries = mutableListOf<TabListPacket.Entry>()
        for (entry in entries) {
            check(entry.list == this) { "Tab list entry targets the wrong tab list!" }
            this.tabListEntries[entry.profile.uniqueId] = entry
            entry.globalEntry.addEntry(entry)
            packetEntries += TabListPacket.Entry.Add(entry.profile, entry.gameMode,
                    entry.displayName.orElse(null), entry.latency)
        }
        this.player.connection.send(TabListPacket(packetEntries))
        if (this.footer != null || this.header != null)
            sendHeaderAndFooterUpdate()
    }

    private fun sendHeaderAndFooterUpdate() {
        this.player.connection.send(TabListHeaderAndFooterPacket(this.header, this.footer))
    }

    fun refresh() {
        sendHeaderAndFooterUpdate()
        for (entry in this.tabListEntries.values)
            entry.refreshDisplayName()
    }

    fun clear() {
        for (entry in this.tabListEntries.values)
            entry.globalEntry.removeEntry(entry)
    }

    override fun getPlayer(): LanternPlayer = this.player

    override fun getHeader(): Optional<Text> = this.header.optional()

    override fun setHeader(header: Text?): LanternTabList = apply {
        this.header = header
        sendHeaderAndFooterUpdate()
    }

    override fun getFooter(): Optional<Text> = this.footer.optional()

    override fun setFooter(footer: Text?): LanternTabList = apply {
        this.footer = footer
        sendHeaderAndFooterUpdate()
    }

    override fun setHeaderAndFooter(header: Text?, footer: Text?): LanternTabList = apply {
        this.header = header
        this.footer = footer
        sendHeaderAndFooterUpdate()
    }

    override fun getEntries(): Collection<TabListEntry> = this.tabListEntries.values.toImmutableList()

    override fun getEntry(uniqueId: UUID): Optional<TabListEntry> = this.tabListEntries[uniqueId].optional()

    override fun addEntry(entry: TabListEntry): TabList {
        val uniqueId = entry.profile.uniqueId
        check(entry.list === this) { "The tab list entries #getList() list does not match to this list." }
        check(!this.tabListEntries.containsKey(uniqueId)) { "There is already a tab list entry assigned with the unique id: $uniqueId" }
        this.tabListEntries[uniqueId] = entry as LanternTabListEntry
        this.player.connection.send(TabListPacket(TabListPacket.Entry.Add(
                entry.profile, entry.gameMode, entry.displayName.orElse(null), entry.getLatency())))
        entry.attached = true
        entry.globalEntry.addEntry(entry)
        return this
    }

    fun removeRawEntry(uniqueId: UUID): LanternTabListEntry? {
        val entry = this.tabListEntries.remove(uniqueId)
        entry?.attached = false
        return entry
    }

    override fun removeEntry(uniqueId: UUID): Optional<TabListEntry> {
        val entry = removeRawEntry(uniqueId)
        if (entry != null) {
            this.player.connection.send(TabListPacket(TabListPacket.Entry.Remove(entry.profile)))
            entry.globalEntry.removeEntry(entry)
        }
        return entry.optional()
    }
}
