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
package org.lanternpowered.server.network.vanilla.packet.type.play

import it.unimi.dsi.fastutil.objects.Object2LongMap
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.advancement.NetworkAdvancement

class AdvancementsPacket(
        val reset: Boolean,
        val added: List<NetworkAdvancement>,
        val removed: List<String>,
        val progress: Map<String, Object2LongMap<String>>
) : Packet {

    override fun toString(): String {
        val progressOutput = ToStringHelper("")
        this.progress.forEach { (key: String?, value: Object2LongMap<String>) ->
            val progressEntry = ToStringHelper("")
            for ((entryKey, entryValue) in value)
                progressEntry.add(entryKey, entryValue)
            progressOutput.add(key, progressEntry.toString())
        }
        return ToStringHelper(this)
                .omitNullValues()
                .add("reset", this.reset)
                .add("added", this.added.joinToString(separator = ",", prefix = "[", postfix = "]"))
                .add("removed", this.removed.joinToString(separator = ",", prefix = "[", postfix = "]"))
                .add("progress", this.progress.toString())
                .toString()
    }
}
