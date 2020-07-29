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

sealed class ClientRequestDataPacket : Packet {

    abstract val transactionId: Int

    data class Entity(
            override val transactionId: Int,
            val entityId: Int
    ) : ClientRequestDataPacket()

    data class Block(
            override val transactionId: Int,
            val position: Vector3i
    ) : ClientRequestDataPacket()
}
