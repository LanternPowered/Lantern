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
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.entity.living.player.tab.TabList
import org.spongepowered.api.entity.living.player.tab.TabListEntry
import org.spongepowered.api.profile.GameProfile

class LanternTabListEntryBuilder : TabListEntry.Builder {

    private var list: LanternTabList? = null
    private var profile: GameProfile? = null
    private var displayName: Text? = null
    private var latency = 0
    private var gameMode: GameMode? = null

    override fun list(list: TabList): LanternTabListEntryBuilder = apply { this.list = list as LanternTabList }
    override fun profile(profile: GameProfile): LanternTabListEntryBuilder = apply { this.profile = profile }
    override fun displayName(displayName: Text?): LanternTabListEntryBuilder = apply { this.displayName = displayName }
    override fun latency(latency: Int): LanternTabListEntryBuilder = apply { this.latency = latency }
    override fun gameMode(gameMode: GameMode): LanternTabListEntryBuilder = apply { this.gameMode = gameMode }

    override fun build(): LanternTabListEntry {
        val list = checkNotNull(this.list) { "list must be set" }
        val profile = checkNotNull(this.profile) { "profile must be set" }
        val gameMode = checkNotNull(this.gameMode) { "game mode must be set" }
        return LanternTabListEntry(GlobalTabList.instance.getOrCreate(profile), list, gameMode, latency, displayName)
    }

    override fun from(value: TabListEntry): LanternTabListEntryBuilder = apply {
        this.list = value.list as LanternTabList
        this.profile = value.profile
        this.displayName = value.displayName.orElse(null)
        this.latency = value.latency
        this.gameMode = value.gameMode
    }

    override fun reset(): LanternTabListEntryBuilder = apply {
        this.list = null
        this.profile = null
        this.displayName = null
        this.latency = 0
        this.gameMode = null
    }
}