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
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode

sealed class ScoreboardObjectivePacket : Packet {

    abstract val objectiveName: String

    abstract class CreateOrUpdate : ScoreboardObjectivePacket() {

        abstract val displayName: Text
        abstract val displayMode: ObjectiveDisplayMode
    }

    data class Remove(override val objectiveName: String) : ScoreboardObjectivePacket()

    data class Create(
            override val objectiveName: String,
            override val displayName: Text,
            override val displayMode: ObjectiveDisplayMode
    ) : CreateOrUpdate()

    data class Update(
            override val objectiveName: String,
            override val displayName: Text,
            override val displayMode: ObjectiveDisplayMode
    ) : CreateOrUpdate()

}
