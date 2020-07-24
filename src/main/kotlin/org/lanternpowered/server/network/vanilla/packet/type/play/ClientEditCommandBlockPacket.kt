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

import org.lanternpowered.server.network.packet.Packet
import org.spongepowered.math.vector.Vector3i

sealed class ClientEditCommandBlockPacket : Packet {

    abstract val command: String
    abstract val shouldTrackOutput: Boolean

    data class Entity(
            val entityId: Int,
            override val command: String,
            override val shouldTrackOutput: Boolean
    ) : ClientEditCommandBlockPacket()

    data class Block(
            val position: Vector3i,
            override val command: String,
            override val shouldTrackOutput: Boolean,
            val mode: Mode,
            val conditional: Boolean,
            val automatic: Boolean
    ) : ClientEditCommandBlockPacket() {

        enum class Mode {
            SEQUENCE, AUTO, REDSTONE
        }
    }
}
