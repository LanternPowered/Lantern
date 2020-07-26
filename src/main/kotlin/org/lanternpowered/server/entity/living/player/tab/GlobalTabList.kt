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

import org.lanternpowered.api.util.collections.concurrentHashMapOf
import org.spongepowered.api.profile.GameProfile
import java.util.UUID

object GlobalTabList {

    private val entries = concurrentHashMapOf<UUID, GlobalTabListEntry>()

    fun add(entry: GlobalTabListEntry) {
        this.entries[entry.profile.uniqueId] = entry
    }

    operator fun get(gameProfile: GameProfile): GlobalTabListEntry? =
            this.entries[gameProfile.uniqueId]

    fun getOrCreate(gameProfile: GameProfile): GlobalTabListEntry =
            this.entries.computeIfAbsent(gameProfile.uniqueId) { GlobalTabListEntry(this, gameProfile) }

    fun remove(gameProfile: GameProfile): GlobalTabListEntry? =
            this.entries.remove(gameProfile.uniqueId)
}
