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

import org.lanternpowered.api.text.Text
import org.lanternpowered.server.network.packet.Packet
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.profile.GameProfile

data class TabListPacket(val entries: Collection<Entry>) : Packet {

    constructor(vararg entries: Entry) : this(entries.asList())

    sealed class Entry {

        abstract val gameProfile: GameProfile

        data class Add(
                override val gameProfile: GameProfile,
                val gameMode: GameMode,
                val displayName: Text?,
                val ping: Int
        ) : Entry()

        data class UpdateGameMode(
                override val gameProfile: GameProfile,
                val gameMode: GameMode
        ) : Entry()

        data class UpdateLatency(
                override val gameProfile: GameProfile,
                val ping: Int
        ) : Entry()

        data class UpdateDisplayName(
                override val gameProfile: GameProfile,
                val displayName: Text?
        ) : Entry()

        data class Remove(
                override val gameProfile: GameProfile
        ) : Entry()
    }
}
