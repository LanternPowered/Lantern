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

sealed class ScoreboardScorePacket : Packet {

    abstract val objectiveName: String
    abstract val scoreName: Text

    data class CreateOrUpdate(override val objectiveName: String, override val scoreName: Text, val value: Int) : ScoreboardScorePacket()
    data class Remove(override val objectiveName: String, override val scoreName: Text) : ScoreboardScorePacket()
}
