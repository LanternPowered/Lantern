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
import org.spongepowered.math.vector.Vector3d

sealed class PlayerFaceAtPacket : Packet {

    abstract val position: Vector3d
    abstract val sourceBodyPosition: BodyPosition

    data class Position(
            override val position: Vector3d,
            override val sourceBodyPosition: BodyPosition
    ) : PlayerFaceAtPacket()

    data class Entity(
            override val position: Vector3d,
            override val sourceBodyPosition: BodyPosition,
            val entityId: Int,
            val entityBodyPosition: BodyPosition
    ) : PlayerFaceAtPacket()

    enum class BodyPosition {
        FEET,
        EYES
    }
}
