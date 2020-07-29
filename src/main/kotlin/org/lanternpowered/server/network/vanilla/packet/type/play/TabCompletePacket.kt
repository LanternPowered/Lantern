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

data class TabCompletePacket(
        val matches: List<Match>,
        val id: Int,
        val start: Int,
        val length: Int
) : Packet {

    data class Match(
            val value: String,
            val tooltip: Text?
    )
}
