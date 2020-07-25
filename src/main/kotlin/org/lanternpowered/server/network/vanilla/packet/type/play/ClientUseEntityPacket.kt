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
import org.spongepowered.api.data.type.HandType
import org.spongepowered.math.vector.Vector3d

sealed class ClientUseEntityPacket : Packet {

    abstract val entityId: Int
    abstract val isSneaking: Boolean

    data class Attack(
            override val entityId: Int,
            override val isSneaking: Boolean
    ) : ClientUseEntityPacket()

    data class Interact(
            override val entityId: Int,
            val handType: HandType,
            val position: Vector3d?,
            override val isSneaking: Boolean
    ) : ClientUseEntityPacket()
}
